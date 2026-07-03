package com.bookrealm.ai.auth;

public record AuthenticatedUser(Long userId, Integer role) {
    public boolean isAdmin() {
        return Integer.valueOf(1).equals(role);
    }
}
