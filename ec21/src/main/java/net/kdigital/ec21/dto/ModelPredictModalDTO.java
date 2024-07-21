package net.kdigital.ec21.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kdigital.ec21.dto.check.ProhibitReason;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModelPredictModalDTO {
    private String productId; // 상품 아이디
    private String similarWord; // 유사단어
    private String prohibitWord; // 금지 단어
    private ProhibitReason prohibitReason; // 금지 사유
    private double similarProba;
}
