package io.github.johneliud.letsplay.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String email;
    private String name;
    private String role;
    
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    
    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
}
