package com.donate4life.demo.dto;

import com.donate4life.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;
    private String username;
    private String name;
    private Integer age;
    private String gender;
    private String bloodGroup;
    private String address;
    private String phone;
    private User.UserType userType;
}