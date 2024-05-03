package com.example.store.user;

import lombok.Data;

import java.sql.Date;

public class UserRequest {

    @Data
    public static class UpdateDTO{
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;
    }

    @Data
    public static class LoginDTO {
        private String username;
        private String password;
    }

    @Data
    public static class JoinDTO {
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;

        public User toEntity(){
            return User.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .tel(tel)
                    .birth(birth)
                    .address(address)
                    .build();
        }
    }
}
