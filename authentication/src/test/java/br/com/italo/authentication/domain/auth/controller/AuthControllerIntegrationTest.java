package br.com.italo.authentication.domain.auth.controller;

import br.com.italo.authentication.domain.auth.dto.AuthDTO;
import br.com.italo.authentication.domain.token.repository.RefreshTokenRepository;
import br.com.italo.authentication.domain.user.entity.Role;
import br.com.italo.authentication.domain.user.entity.User;
import br.com.italo.authentication.domain.user.repository.RoleRepository;
import br.com.italo.authentication.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // Adicione esta anotação
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Limpa tudo
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Cria a role padrão
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        roleRepository.save(userRole);
    }

    @Test
    void register_ValidInput_ReturnsAuthResponse() throws Exception {
        // Arrange
        AuthDTO.RegisterRequest registerRequest = new AuthDTO.RegisterRequest(
                "testuser",
                "test@example.com",
                "password123"
        );

        // Act
        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Assert
        result.andExpect(status().isOk());
        // Adicione mais assertions para verificar o corpo da resposta, se necessário
    }

    @Test
    void login_ValidInput_ReturnsAuthResponse() throws Exception {
        // Arrange
        // Criar um usuário no banco de dados para o teste de login
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        userRepository.save(user);

        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest(
                "testuser",
                "password123"
        );

        // Act
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        // Assert
        result.andExpect(status().isOk());
        // Adicione mais assertions para verificar o corpo da resposta, se necessário
    }

    @Test
    void refreshToken_ValidInput_ReturnsAuthResponse() throws Exception {
        // 1. Registra o usuário
        AuthDTO.RegisterRequest registerRequest = new AuthDTO.RegisterRequest(
                "testuser",
                "test@example.com",
                "password123"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Faz login para obter o refresh token
        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest(
                "testuser",
                "password123"
        );
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 3. Extrai o refresh token da resposta do login
        String refreshToken = objectMapper.readTree(loginResponse).get("refreshToken").asText();

        // 4. Usa o refresh token real no endpoint de refresh
        AuthDTO.RefreshTokenRequest refreshTokenRequest = new AuthDTO.RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk());
    }
}
