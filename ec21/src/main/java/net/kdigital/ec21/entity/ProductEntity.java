package net.kdigital.ec21.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.ProductDTO;
import net.kdigital.ec21.dto.check.ProductCategory;
import net.kdigital.ec21.dto.check.YesOrNo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_desc", nullable = false)
    private String productDesc;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "saved_file_name")
    private String savedFileName;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "moq", nullable = false)
    private int moq;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "remote_ip", nullable = false)
    private String remoteIp;

    @Column(name = "country")
    private String country;

    @Column(name = "hit_count")
    private int hitCount;

    @Column(name = "lstm_predict_proba", nullable = false)
    private double lstmPredictProba;

    @Column(name = "lstm_predict", nullable = false)
    private boolean lstmPredict;

    @Column(name = "judge")
    @Enumerated(EnumType.STRING)
    private YesOrNo judge;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @Column(name = "product_delete")
    @Enumerated(EnumType.STRING)
    private YesOrNo productDelete;

    // 자식
    // 1) 금지어 유사 단어 테이블
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("prohibit_similar_id")
    private List<ProhibitSimilarWordEntity> prohibitSimilarWordEntity;
    // 2) 인콰이어리
    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("inquiry_id")
    private List<InquiryEntity> inquiryEntity;

    public static ProductEntity toEntity(ProductDTO dto, CustomerEntity customerEntity) {
        return ProductEntity.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productDesc(dto.getProductDesc())
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .price(dto.getPrice())
                .origin(dto.getOrigin())
                .moq(dto.getMoq())
                .unit(dto.getUnit())
                .category(dto.getCategory())
                .createDate(dto.getCreateDate())
                .updateDate(dto.getUpdateDate())
                .remoteIp(dto.getRemoteIp())
                .country(dto.getCountry())
                .hitCount(dto.getHitCount())
                .lstmPredictProba(dto.getLstmPredictProba())
                .lstmPredict(dto.isLstmPredict())
                .judge(dto.getJudge())
                .customerEntity(customerEntity)
                .productDelete(dto.getProductDelete())
                .build();
    }

}
