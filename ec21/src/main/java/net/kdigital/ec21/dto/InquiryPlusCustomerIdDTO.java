package net.kdigital.ec21.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.InquiryEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InquiryPlusCustomerIdDTO {
    private Long inquiryId;
    private String senderId;
    private String receiverId;
    private String inquiryTitle;
    private LocalDateTime sendDate;
    private InquiryEnum saved;
    private InquiryEnum trash;
    private InquiryEnum spam;
    private InquiryEnum deleted;
    private String customerId;
}
