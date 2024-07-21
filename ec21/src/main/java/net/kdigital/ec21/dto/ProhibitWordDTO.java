package net.kdigital.ec21.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.ProhibitReason;
import net.kdigital.ec21.entity.ProhibitWordEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProhibitWordDTO {
    private String prohibitWord;
    private ProhibitReason prohibitReason;

    public static ProhibitWordDTO toEntity(ProhibitWordEntity entity) {
        return ProhibitWordDTO.builder()
                .prohibitWord(entity.getProhibitWord())
                .prohibitReason(entity.getProhibitReason())
                .build();
    }
}
