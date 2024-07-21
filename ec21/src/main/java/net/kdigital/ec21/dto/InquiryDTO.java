package net.kdigital.ec21.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.InquiryEnum;
import net.kdigital.ec21.entity.InquiryEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InquiryDTO {
    private Long inquiryId;
    private String senderId; // FK
    private String receiverId;
    private String productId; // FK
    private int quantity;
    private String inquiryTitle;
    private String inquiryContent;
    private LocalDateTime sendDate;

    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;

    private InquiryEnum saved;
    private InquiryEnum trash;
    private InquiryEnum spam;
    private InquiryEnum deleted;
    private InquiryEnum checked;

    public static InquiryDTO toDTO(InquiryEntity entity, String senderId, String productId) {
        return InquiryDTO.builder()
                .inquiryId(entity.getInquiryId())
                .senderId(senderId)
                .receiverId(entity.getReceiverId())
                .productId(productId)
                .quantity(entity.getQuantity())
                .inquiryTitle(entity.getInquiryTitle())
                .inquiryContent(entity.getInquiryContent())
                .sendDate(entity.getSendDate())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .saved(entity.getSaved())
                .trash(entity.getTrash())
                .spam(entity.getSpam())
                .deleted(entity.getDeleted())
                .checked(entity.getChecked())
                .build();
    }

}
