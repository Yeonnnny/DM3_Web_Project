package net.kdigital.ec21.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.ProductCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.entity.ProductEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProductDTO {
    private String productId;
    private String productName;
    private String productDesc;

    private MultipartFile uploadImg;
    private String originalFileName;
    private String savedFileName;

    private Double price;
    private String origin;
    private int moq;
    private String unit;
    private ProductCategory category;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String remoteIp;
    private String country;
    private int hitCount;
    private double lstmPredictProba;
    private boolean lstmPredict;
    private YesOrNo judge;
    private String customerId; // FK
    private YesOrNo productDelete;

    public static ProductDTO toDTO(ProductEntity entity, String customerId) {
        return ProductDTO.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .productDesc(entity.getProductDesc())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .price(entity.getPrice())
                .origin(entity.getOrigin())
                .moq(entity.getMoq())
                .unit(entity.getUnit())
                .category(entity.getCategory())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .remoteIp(entity.getRemoteIp())
                .country(entity.getCountry())
                .hitCount(entity.getHitCount())
                .lstmPredictProba(entity.getLstmPredictProba())
                .lstmPredict(entity.isLstmPredict())
                .judge(entity.getJudge())
                .customerId(customerId)
                .productDelete(entity.getProductDelete())
                .build();
    }
}
