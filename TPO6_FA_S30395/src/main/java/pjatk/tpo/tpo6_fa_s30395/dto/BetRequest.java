package pjatk.tpo.tpo6_fa_s30395.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BetRequest {
    private Long userId;
    private Long horseId;
    private double amount;
}

