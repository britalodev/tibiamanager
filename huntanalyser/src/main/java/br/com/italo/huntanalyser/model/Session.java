package br.com.italo.huntanalyser.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String duration;
    private String lootType;
    private Long totalLoot;
    private Long totalSupplies;
    private Long totalBalance;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlayerSession> players;
}
