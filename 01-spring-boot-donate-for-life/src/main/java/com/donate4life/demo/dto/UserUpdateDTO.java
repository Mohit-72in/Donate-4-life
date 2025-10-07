package com.donate4life.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String name;
    private Integer age;
    private String address;
    private String phone;
}