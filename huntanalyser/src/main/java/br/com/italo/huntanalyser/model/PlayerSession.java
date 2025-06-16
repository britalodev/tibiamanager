package br.com.italo.huntanalyser.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "player_id", referencedColumnName = "id"),
            @JoinColumn(name = "player_name", referencedColumnName = "playerName")
    })
    private Player player;

    private boolean leader;
    private Long loot;
    private Long supplies;
    private Long balance;
    private Long damage;
    private Long healing;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private Session session;
}
