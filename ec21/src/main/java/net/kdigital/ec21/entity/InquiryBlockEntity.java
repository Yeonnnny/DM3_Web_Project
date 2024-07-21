package net.kdigital.ec21.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.InquiryBlockDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "inquiry_block")
public class InquiryBlockEntity {

    @SequenceGenerator(name = "inquiry_block_seq", sequenceName = "inquiry_block_seq", allocationSize = 1, initialValue = 1)
    @Id
    @Column(name = "inquiry_block_id")
    @GeneratedValue(generator = "inquiry_block_seq")
    private Long inquiryBlockId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @Column(name = "blocked_id")
    private String blockedId;

    public static InquiryBlockEntity toEntity(InquiryBlockDTO dto, CustomerEntity customerEntity) {
        return InquiryBlockEntity.builder()
                .inquiryBlockId(dto.getInquiryBlockId())
                .customerEntity(customerEntity)
                .blockedId(dto.getBlockedId())
                .build();
    }

}
