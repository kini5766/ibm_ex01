package com.example.demo.domain.user.entity;

public enum Role {
    USER,
    ADMIN;

    public static final String ROLE = "ROLE_";

    public static String toAuthorityName(Role role) {
        return ROLE + role.name();
    }
}
