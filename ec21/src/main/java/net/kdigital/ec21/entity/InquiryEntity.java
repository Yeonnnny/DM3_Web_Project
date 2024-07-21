package net.kdigital.ec21.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.InquiryDTO;
import net.kdigital.ec21.dto.check.InquiryEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "inquiry")
public class InquiryEntity {
    @SequenceGenerator(name = "inquiry_seq", sequenceName = "inquiry_seq", allocationSize = 1, initialValue = 1)
    @Id
    @Column(name = "inquiry_id")
    @GeneratedValue(generator = "inquiry_seq")
    private Long inquiryId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private CustomerEntity customerEntity;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "inquiry_title", nullable = false)
    private String inquiryTitle;

    @Column(name = "inquiry_content", nullable = false)
    private String inquiryContent;

    @Column(name = "send_date")
    @CreationTimestamp
    private LocalDateTime sendDate;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "saved_file_name")
    private String savedFileName;

    @Enumerated(EnumType.STRING)
    private InquiryEnum saved;
    @Enumerated(EnumType.STRING)
    private InquiryEnum trash;
    @Enumerated(EnumType.STRING)
    private InquiryEnum spam;
    @Enumerated(EnumType.STRING)
    private InquiryEnum deleted;
    @Enumerated(EnumType.STRING)
    private InquiryEnum checked;

    public static InquiryEntity toEntity(InquiryDTO dto, CustomerEntity customerEntity, ProductEntity productEntity) {
        return InquiryEntity.builder()
                .inquiryId(dto.getInquiryId())
                .customerEntity(customerEntity)
                .receiverId(dto.getReceiverId())
                .productEntity(productEntity)
                .quantity(dto.getQuantity())
                .inquiryTitle(dto.getInquiryTitle())
                .inquiryContent(dto.getInquiryContent())
                .sendDate(dto.getSendDate())
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .saved(dto.getSaved())
                .trash(dto.getTrash())
                .spam(dto.getSpam())
                .deleted(dto.getDeleted())
                .checked(dto.getChecked())
                .build();
    }
}
