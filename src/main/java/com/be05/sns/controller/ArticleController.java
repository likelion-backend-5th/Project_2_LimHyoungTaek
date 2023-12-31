package com.be05.sns.controller;

import com.be05.sns.dto.ResponseDto;
import com.be05.sns.dto.feed.AFeedDto;
import com.be05.sns.dto.feed.UserFeedsDto;
import com.be05.sns.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class ArticleController {
    private final ResponseDto response;
    private final ArticleService articleService;

    @PostMapping(value = "/create")
    public ResponseDto createdFeed(
            @RequestParam String title, @RequestParam String content,
            @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles,
            Authentication authentication) {
        articleService.createFeed(title, content, imageFiles, authentication);
        return response.toMessage("피드를 생성하였습니다.");
    }

    @GetMapping("/{userName}")
    public Page<UserFeedsDto> readFollowFeeds(@PathVariable("userName") String userName) {
        return articleService.readAllFeed(userName);
    }

    @GetMapping("/follow")
    public Page<UserFeedsDto> readFollowFeeds(Authentication authentication) {
        return articleService.readAllFollowFeed(authentication);
    }

    @GetMapping("/friend")
    public Page<UserFeedsDto> readFriendFeeds(Authentication authentication) {
        return articleService.readAllFriendFeed(authentication);
    }

    @GetMapping("/read/{articleId}")
    public AFeedDto readFeed(@PathVariable("articleId") Long articleId) {
        return articleService.read(articleId);
    }

    @DeleteMapping("/read/{articleId}")
    public ResponseDto deleteFeed(@PathVariable("articleId") Long articleId,
                                  Authentication authentication) {
        articleService.delete(articleId, authentication);
        return response.toMessage("피드가 삭제되었습니다.");
    }

    @PutMapping("/read/{articleId}")
    public ResponseDto updateFeed(
            @PathVariable("articleId") Long articleId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> deleteImage,
            @RequestPart(value = "image", required = false)
            List<MultipartFile> imageFiles,
            Authentication authentication) {
        articleService.updateFeed
                (articleId, title, content, deleteImage, imageFiles, authentication);
        return response.toMessage("피드를 수정하였습니다.");
    }
}