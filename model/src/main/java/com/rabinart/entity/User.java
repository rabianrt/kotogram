package com.rabinart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class User {

    private Long id;
    private String login;
    private String password;
}
