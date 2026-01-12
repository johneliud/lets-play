package io.github.johneliud.letsplay.dto.product;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    
    private String id;
    private String name;
    private String description;
    private Double price;
    private String userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
