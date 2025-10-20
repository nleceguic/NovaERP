package com.nleceguic.novaerp.dto;

import com.nleceguic.novaerp.entity.User;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

}
