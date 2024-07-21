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
public class ModelPredictDTO {
    private String customerId;
    private String productId;
    private String productName;
    private String productDesc;
    private double lstmPredictProba;    // 환산된 확률
    private boolean lstmPredict;
    private String similarWord;     //유사도 가장 높은 유사단어
    private double similarProba;    // 금지어 유사 확률
    private String prohibitWord;     // 금지 단어
    private ProhibitReason prohibitReason; // 금지 사유
}
