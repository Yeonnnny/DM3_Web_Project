package net.kdigital.ec21.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.ProductCategory;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InquiryModalDTO {
    private Long inquiryId;
    private String senderId;
    private String receiverId;
    private String productName;
    private ProductCategory category;
    private int quantity;
    private String unit;
    private String inquiryTitle;
    private String inquiryContent;
    private String originalFileName;
    private String savedFileName;
}
