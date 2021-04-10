package com.booleank2j.recipe_mate;

public class user {
    private String name;
    private String email;
    private String likes;
    private String dislikes;
    private String profileImage;

    public user(){

    }

    public user(String name, String email, String likes, String dislikes,String profileImage) {
        this.name = name;
        this.email = email;
        this.likes = likes;
        this.dislikes = dislikes;
        this.profileImage=profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
