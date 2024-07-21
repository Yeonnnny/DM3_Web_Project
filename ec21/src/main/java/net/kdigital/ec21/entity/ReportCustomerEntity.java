package net.kdigital.ec21.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.ReportCustomerDTO;
import net.kdigital.ec21.dto.check.ReportCategory;
import net.kdigital.ec21.dto.check.YesOrNo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "report_customer")
public class ReportCustomerEntity {
    @SequenceGenerator(name = "report_customer_seq", sequenceName = "report_customer_seq", allocationSize = 1, initialValue = 1)
    @Id
    @Column(name = "report_customer_id")
    @GeneratedValue(generator = "report_customer_seq")
    private Long reportCustomerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id")
    private CustomerEntity customerEntity;

    @Column(name = "report_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportCategory reportCategory;

    @Column(name = "report_reason")
    private String reportReason;

    @Column(name = "report_date")
    @CreationTimestamp
    private LocalDateTime reportDate;

    @Column(name = "manager_check")
    @Enumerated(EnumType.STRING)
    private YesOrNo managerCheck;

    public static ReportCustomerEntity toEntity(ReportCustomerDTO dto, CustomerEntity customerEntity) {
        return ReportCustomerEntity.builder()
                .reportCustomerId(dto.getReportCustomerId())
                .customerEntity(customerEntity)
                .reportCategory(dto.getReportCategory())
                .reportReason(dto.getReportReason())
                .reportDate(dto.getReportDate())
                .managerCheck(dto.getManagerCheck())
                .build();
    }

}
