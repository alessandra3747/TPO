package pjatk.tpo.tpo6_fa_s30395.model;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Horse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private double weight;
    private double speed;
    private double temperament;
    private int wins;
    private int races;
}

