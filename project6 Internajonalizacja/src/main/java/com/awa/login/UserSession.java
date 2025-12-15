package com.awa.login;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import com.awa.entities.User;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private List<String> roles;


    public User getUser() {
        return user;
    }

    public List<String> getRoles() {
        return roles;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public boolean hasRole(String role) {
        if (roles == null) return false;
        return roles.contains(role);
    }

    public void invalidate() {
        user = null;
        roles = null;
    }
}
