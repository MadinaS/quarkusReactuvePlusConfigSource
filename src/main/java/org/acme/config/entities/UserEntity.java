package org.acme.config.entities;

import javax.persistence.*;


/**
 * The persistent class for the user database table.
 */
@Entity
@Table(name = "user")
public class UserEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private int id;

    @Column(name = "avatar_base64")
    private String avatarBase64;

    @Column(name = "jwt")
    private String jwt;

    @Column(name = "chat_token", length = 800)
    private String chatToken;

    @Column(name = "user_nickname", length = 100)
    private String userNickname;


    public UserEntity() {
    }

    public UserEntity(String userNickname, String chatToken) {
        this.userNickname = userNickname;
        this.chatToken = chatToken;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarBase64() {
        return this.avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    public String getJwt() {
        return this.jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
