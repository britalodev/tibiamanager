package br.com.italo.authentication.domain.auth.service;

import br.com.italo.authentication.domain.auth.dto.AuthDTO;
import br.com.italo.authentication.domain.security.service.JwtService;
import br.com.italo.authentication.domain.token.entity.RefreshToken;
import br.com.italo.authentication.domain.token.service.RefreshTokenService;
import br.com.italo.authentication.domain.user.entity.Role;
import br.com.italo.authentication.domain.user.entity.User;
import br.com.italo.authentication.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthDTO.RegisterResponse register(AuthDTO.RegisterRequest request) {
        // Lógica de cadastro do usuário
        User user = userService.createUser(request); // seu método de criação
        return new AuthDTO.RegisterResponse("Usuário registrado com sucesso", user.getUsername(), user.getEmail());
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.getUserByUsername(request.username());
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);

        return new AuthDTO.AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthDTO.AuthResponse refreshToken(AuthDTO.RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user);
                    return new AuthDTO.AuthResponse(accessToken, refreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not valid"));
    }
}
