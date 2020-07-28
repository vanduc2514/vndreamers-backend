package com.codegym.vndreamers.apis;

import com.codegym.vndreamers.exceptions.CommentNotFound;
import com.codegym.vndreamers.exceptions.EntityExistException;
import com.codegym.vndreamers.exceptions.PostNotFoundException;
import com.codegym.vndreamers.models.Comment;
import com.codegym.vndreamers.models.Post;
import com.codegym.vndreamers.models.User;
import com.codegym.vndreamers.services.comment.CommentService;
import com.codegym.vndreamers.services.post.PostCRUDService;
import com.codegym.vndreamers.services.user.UserCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(
        value = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
//@PropertySource("classpath:config/status.properties")
public class CommentAPI {
    @Autowired
    private PostCRUDService postCRUDService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserCRUDService userCRUDService;

    @PostMapping(value = "/posts/{postId}/comments")
    public Comment createComment(@RequestBody Comment model, @PathVariable("postId") int id, UriComponentsBuilder ucBuilder) throws SQLIntegrityConstraintViolationException, EntityExistException {
        Post post = postCRUDService.findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.setPost(post);
        model.setUser(user);
        return commentService.save(model);
    }

    @GetMapping(value = "/posts/{id}/comments")
    public List<Comment> getAllCommentsPost(@PathVariable("id") int id) {
        List<Comment> comments = commentService.findAllByPostId(id);
        Collections.reverse(comments);
        return comments;
    }

//    @GetMapping(value = "comments/{id}")
//    public Comment getCommentsById (@PathVariable ("id")int id){
////        Comment comment = commentService.findById(id);
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<Comment> listComments = commentService.findAllCommentByUserId(user.getId());
//        return listComments.get(id);
//    }

    @PutMapping(value = "/comments/{id}")
    public Object getCommentById(@PathVariable("id") int id, @RequestBody Comment comment) throws SQLIntegrityConstraintViolationException, EntityExistException {
        Comment comment1 = commentService.findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user1 = comment1.getUser();
        if (user.getId() == user1.getId()) {
            comment1.setContent(comment.getContent());
            commentService.save(comment1);
            return comment1;
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "/comments/{id}")
    public Comment deleteComments(@PathVariable("id") int id) throws CommentNotFound {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            Comment comment = commentService.findById(id);
            if (comment == null){
                return null;
            }else {
                User user1 = comment.getUser();
                if (user.getId() == user1.getId()) {
                    commentService.removeComment(id);
                    return comment;
                } else {
                    return null;
                }
            }
        }catch (Exception e){
            throw new CommentNotFound();
        }


    }

    @GetMapping(value = "/notification/comments")
    public List<Comment> getNewAllCommentsByUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Comment> commentList = commentService.findAllCommentByUserId(user.getId());
        List<Comment> comments;
        List<Comment> tenComment;
        if (commentList.size() < 10) {
            comments = commentList.subList(0, commentList.size());
            Collections.reverse(comments);
            return comments;
        } else {
            tenComment = commentList.subList(commentList.size() - 10, commentList.size());
            Collections.reverse(tenComment);
            return tenComment;
        }
    }

    @ExceptionHandler(CommentNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCommentNotFoundException() {
        return "{\"error\":\"Comment not found!\"}";
    }
}
