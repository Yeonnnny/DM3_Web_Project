package net.kdigital.ec21.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.ProhibitSimilarWordDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "prohibit_similar_word")
public class ProhibitSimilarWordEntity {
    @SequenceGenerator(name = "prohibit_similar_word_seq", sequenceName = "prohibit_similar_word_seq", allocationSize = 1, initialValue = 1)
    @Id
    @Column(name = "prohibit_similar_id")
    @GeneratedValue(generator = "prohibit_similar_word_seq")
    private Long prohibitSimilarId;

    @Column(name = "similar_word", nullable = false)
    private String similarWord;

    @Column(name = "similar_proba", nullable = false)
    private double similarProba;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prohibit_word")
    private ProhibitWordEntity prohibitWordEntity;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    public static ProhibitSimilarWordEntity toEntity(ProhibitSimilarWordDTO dto, ProhibitWordEntity prohibitWordEntity,
            ProductEntity productEntity) {
        return ProhibitSimilarWordEntity.builder()
                .prohibitSimilarId(dto.getProhibitSimilarId())
                .similarWord(dto.getSimilarWord())
                .similarProba(dto.getSimilarProba())
                .prohibitWordEntity(prohibitWordEntity)
                .productEntity(productEntity)
                .build();
    }
}
