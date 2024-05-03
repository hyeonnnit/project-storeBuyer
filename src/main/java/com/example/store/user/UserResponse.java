package com.example.store.user;

import lombok.Data;

import java.sql.Date;

public class UserResponse {
    @Data
    public static class UserDTO {
        private Integer id;
        private String username;
        private String password;
        private String name;
        private String email;
        private String tel;
        private Date birth;
        private String address;

        public UserDTO (User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.name = user.getName();
            this.email = user.getEmail();
            this.tel = user.getTel();
            this.birth = user.getBirth();
            this.address = user.getAddress();
        }
    }
}
