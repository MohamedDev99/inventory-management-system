package com.moeware.ims.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 Configuration for Inventory Management System
 *
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access OpenAPI JSON at: http://localhost:8080/v3/api-docs
 *
 * @author MoeWare Team
 * @version 1.0
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Inventory Management System API", version = "1.0.0", description = """
        RESTful API for MoeWare Inventory Management System.

        ## Features
        - User authentication with JWT tokens
        - Role-based access control (RBAC)
        - Multi-warehouse inventory tracking
        - Purchase and sales order management
        - Real-time stock alerts
        - Comprehensive reporting

        ## Authentication
        Most endpoints require authentication. Use the `/api/auth/login` endpoint to obtain a JWT token,
        then include it in the Authorization header as: `Bearer <your-token>`

        ## Roles
        - **ADMIN**: Full system access
        - **MANAGER**: Warehouse and order management
        - **WAREHOUSE_STAFF**: Inventory operations only
        - **VIEWER**: Read-only access
        """, contact = @Contact(name = "MoeWare Development Team", email = "support@moeware.com", url = "https://moeware.com"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(url = "http://localhost:8080", description = "Local Development Server"),
        @Server(url = "https://staging-api.moeware.com", description = "Staging Server"),
        @Server(url = "https://api.moeware.com", description = "Production Server")
})
@SecurityScheme(name = "bearerAuth", description = "JWT authentication token. Format: Bearer <token>", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
    // Configuration is done via annotations
    // No additional code needed
}