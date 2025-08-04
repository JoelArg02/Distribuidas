package com.allpasoft.authservice.Dto;

public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String role;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(boolean valid, String username, String role) {
        this.valid = valid;
        this.username = username;
        this.role = role;
    }
    
    // Getters y setters
    public boolean isValid() { 
        return valid; 
    }
    
    public void setValid(boolean valid) { 
        this.valid = valid; 
    }
    
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
}
