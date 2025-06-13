package br.com.italo.authentication.domain.user.service;

import br.com.italo.authentication.domain.auth.dto.AuthDTO;
import br.com.italo.authentication.domain.user.entity.Role;
import br.com.italo.authentication.domain.user.entity.User;
import br.com.italo.authentication.domain.user.repository.RoleRepository;
import br.com.italo.authentication.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User createUser(AuthDTO.RegisterRequest request) {
        // Verifica se o username ou email já existem
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username já existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email já existe");
        }

        // Busca a role padrão
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role padrão não encontrada"));

        // Cria o usuário
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(userRole));

        // Salva no banco
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UUID id, String username, String email, Set<Role> roles) {
        User user = getUserById(id);

        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Transactional
    public Role createRole(String name) {
        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role already exists");
        }

        Role role = Role.builder()
                .name(name)
                .build();

        return roleRepository.save(role);
    }
}
