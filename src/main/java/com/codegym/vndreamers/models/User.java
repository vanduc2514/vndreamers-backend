package com.codegym.vndreamers.models;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "user", schema = "vndreamers")
@Data
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Basic
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Basic
    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Basic
    @Column(name = "birth_date", nullable = false)
    private Timestamp birthDate;

    @Basic
    @Column(name = "gender", nullable = false)
    private int gender;

    @Basic
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate = Timestamp.valueOf(LocalDateTime.now());

    @Basic
    @Column(name = "modify_date", nullable = false)
    private Timestamp modifyDate = Timestamp.valueOf(LocalDateTime.now());

    @Basic
    @Column(name = "status", nullable = false)
    private int status;

    @Basic
    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "user")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user")
    private Set<Post> posts;

    @OneToMany(mappedBy = "user")
    private Set<PostReaction> postLikes;

    @OneToMany(mappedBy = "user")
    private Set<CommentReaction> commentLikes;

    @OneToMany(mappedBy = "userSend")
    private Set<FriendRequest> requestsThisUserSent;

    @OneToMany(mappedBy = "userReceive")
    private Set<FriendRequest> requestsThisUserReceived;

    @ManyToMany
    @JoinTable(
            name = "role_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;
}