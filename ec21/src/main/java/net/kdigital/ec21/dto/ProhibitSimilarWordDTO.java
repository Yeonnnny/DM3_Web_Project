package net.kdigital.ec21.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.entity.ProhibitSimilarWordEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProhibitSimilarWordDTO {
    private Long prohibitSimilarId;
    private String similarWord;
    private double similarProba;
    private String prohibitWord;
    private String productId;

    public static ProhibitSimilarWordDTO toDTO(ProhibitSimilarWordEntity entity, String prohibitWord,
            String productId) {
        return ProhibitSimilarWordDTO.builder()
                .prohibitSimilarId(entity.getProhibitSimilarId())
                .similarWord(entity.getSimilarWord())
                .similarProba(entity.getSimilarProba())
                .prohibitWord(prohibitWord)
                .productId(productId)
                .build();
    }

}
