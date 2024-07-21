package net.kdigital.ec21.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.entity.InquiryBlockEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InquiryBlockDTO {
    private Long inquiryBlockId;
    private String cutomerId;
    private String blockedId;

    public static InquiryBlockDTO toDTO(InquiryBlockEntity entity, String customerId) {
        return InquiryBlockDTO.builder()
                .inquiryBlockId(entity.getInquiryBlockId())
                .cutomerId(customerId)
                .blockedId(entity.getBlockedId())
                .build();
    }
}
