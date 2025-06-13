package br.com.italo.authentication.domain.auth.controller;

import br.com.italo.authentication.domain.auth.dto.AuthDTO;
import br.com.italo.authentication.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registra um novo usuário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
                            content = @Content(schema = @Schema(implementation = AuthDTO.RegisterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
            })
    public ResponseEntity<AuthDTO.RegisterResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthDTO.AuthResponse> refreshToken(@Valid @RequestBody AuthDTO.RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
