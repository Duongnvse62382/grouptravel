package com.fpt.gta.data.dto;

public class UserFireBase {
    private String uId;
    private String name;
    private String email;

    public UserFireBase() {
    }

    public UserFireBase(String uId, String name, String email) {
        this.uId = uId;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserFireBase{" +
                "uId='" + uId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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
}
