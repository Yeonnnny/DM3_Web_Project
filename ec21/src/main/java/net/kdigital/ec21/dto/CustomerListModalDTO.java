package net.kdigital.ec21.dto;

import net.kdigital.ec21.dto.check.ProductCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CustomerListModalDTO {
    private String customerId;
    private String productId;
    private String productName;
    private String productDesc;
    private String origin;
    private int moq;
    private String unit;
    private Double price;
    private ProductCategory category;
    private boolean lstmPredict;
    private YesOrNo judge;
}
