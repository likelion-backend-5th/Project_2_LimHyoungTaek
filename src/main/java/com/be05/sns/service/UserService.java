package com.be05.sns.service;

import com.be05.sns.dto.UserDto;
import com.be05.sns.entity.Users;
import com.be05.sns.repository.UserRepository;
import com.be05.sns.token.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // 회원가입(유저 생성)
    public void createUser(UserDto dto) {
        if (userExists(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "아이디가 이미 존재합니다.");
        try {
            Users user = dto.newUser(passwordEncoder.encode(dto.getPassword()));
            this.userRepository.save(user);
        } catch (ClassCastException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 로그인, Jwt Token 저장
    public void toLoginSaveJwt(UserDto dto) {
        if (dto.getUsername().isEmpty() || dto.getPassword().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 입력해주세요.");

        Users user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        UserDetails userDetails = loadUserByUsername(dto.getUsername());
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) // 순서 조심
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        String token = jwtUtils.generateToken(dto);
        user.setToken(token);
        userRepository.save(user);
    }

    // 해당 유저 찾기
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return UserDto.fromEntity(user);
    }


    public boolean userExists(String username) {
        return this.userRepository.existsByUsername(username);
    }
}