package br.com.italo.huntanalyser.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PlayerId implements Serializable {
    private Long id;
    private String playerName;
}
