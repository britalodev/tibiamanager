package br.com.italo.huntanalyser.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player {

    @EmbeddedId
    private PlayerId id;

}
