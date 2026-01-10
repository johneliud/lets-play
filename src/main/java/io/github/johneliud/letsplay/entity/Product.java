package io.github.johneliud.letsplay.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;

    @Positive
    private Double price;

    @NotBlank
    private String userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
