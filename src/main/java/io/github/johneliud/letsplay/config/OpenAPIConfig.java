//package io.github.johneliud.letsplay.config;
//
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class OpenAPIConfig {
//    @Bean
//    public OpenAPI defineOpenAPI() {
//        Server server = new Server();
//        server.setUrl("http://localhost:8080");
//        server.setDescription("Local development server");
//
//        Info information = new Info().title("Let's Play API").version("1.0").description("A RESTful CRUD API called lets-play, built using Spring Boot with MongoDB. The system will manage users and products, allowing operations such as creating, reading, updating, and deleting both entities.");
//
//        return new OpenAPI().info(information).servers(List.of(server));
//    }
//}