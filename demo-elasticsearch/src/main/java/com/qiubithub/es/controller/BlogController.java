package com.qiubithub.es.controller;

import com.qiubithub.es.model.Blog;
import com.qiubithub.es.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<Void> createBlog(@RequestBody Blog blog) {
        blogService.saveBlog(blog);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id) {
        Blog blog = blogService.getBlogById(id);
        if (blog != null) {
            return ResponseEntity.ok(blog);
        } else {
            return ResponseEntity.notFound().build();
    }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable String id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Blog>> findByTitle(@RequestParam String title) {
        List<Blog> blogs = blogService.findByTitle(title);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/title/keyword")
    public ResponseEntity<List<Blog>> findByTitleContaining(@RequestParam String keyword) {
        List<Blog> blogs = blogService.findByTitleContaining(keyword);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/content")
    public ResponseEntity<List<Blog>> findByContentContaining(@RequestParam String keyword) {
        List<Blog> blogs = blogService.findByContentContaining(keyword);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<Blog>> findByAuthor(@RequestParam String author) {
        List<Blog> blogs = blogService.findByAuthor(author);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<Blog>> findByCategory(@RequestParam String category) {
        List<Blog> blogs = blogService.findByCategory(category);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/published")
    public ResponseEntity<List<Blog>> findPublishedBlogs() {
        List<Blog> blogs = blogService.findPublishedBlogs();
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/hot")
    public ResponseEntity<List<Blog>> findHotBlogs(@RequestParam(defaultValue = "5") int limit) {
        List<Blog> blogs = blogService.findHotBlogs(limit);
        return ResponseEntity.ok(blogs);
    }
}