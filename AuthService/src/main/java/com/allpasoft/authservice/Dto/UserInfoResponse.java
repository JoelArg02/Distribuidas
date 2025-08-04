package com.allpasoft.authservice.Dto;

public class UserInfoResponse {
    private String username;
    private String role;
    private String email;
    
    public UserInfoResponse() {}
    
    public UserInfoResponse(String username, String role) {
        this.username = username;
        this.role = role;
    }
    
    public UserInfoResponse(String username, String role, String email) {
        this.username = username;
        this.role = role;
        this.email = email;
    }
    
    // Getters y setters
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
}
