package net.kdigital.ec21.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.ProhibitWordDTO;
import net.kdigital.ec21.dto.check.ProhibitReason;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "prohibit_word")
public class ProhibitWordEntity {
    @Id
    @Column(name = "prohibit_word", nullable = false)
    private String prohibitWord;

    @Column(name = "prohibit_reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProhibitReason prohibitReason;


    // 자식 - 금지어 유사 단어 테이블
    @OneToMany(mappedBy = "prohibitWordEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("prohibit_similar_id")
    private List<ProhibitSimilarWordEntity> prohibitSimilarWordEntity;


    public static ProhibitWordEntity toEntity(ProhibitWordDTO dto) {
        return ProhibitWordEntity.builder()
                .prohibitWord(dto.getProhibitWord())
                .prohibitReason(dto.getProhibitReason())
                .build();
    }
    
}
