package net.kdigital.ec21.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.check.CustomerGubun;
import net.kdigital.ec21.dto.check.YesOrNo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "customer_pw", nullable = false)
    private String customerPw;
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(name = "customer_department", nullable = false)
    private String customerDepartment;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "customer_gubun", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerGubun customerGubun;
    @Column(name = "zip_no", nullable = false)
    private String zipNo;
    @Column(name = "fax_no")
    private String faxNo;
    @Column(name = "busi_no")
    private String busiNo;
    @Column(name = "trade_no")
    private String tradeNo;
    @Column(name = "comp_name", nullable = false)
    private String compName;
    @Column(name = "comp_desc")
    private String compDesc;
    @Column(name = "comp_url")
    private String compUrl;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "remote_ip", nullable = false)
    private String remoteIp;
    @Column(name = "country")
    private String country;
    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;
    @Column(name = "update_date")
    @UpdateTimestamp
    private LocalDateTime updateDate;
    @Column(name = "roles")
    private String roles;
    @Column(name = "enabled")
    @Enumerated(EnumType.STRING)
    private YesOrNo enabled;
    @Column(name = "reported_cnt")
    private int reportedCnt;
    @Column(name = "blacklist_check")
    @Enumerated(EnumType.STRING)
    private YesOrNo blacklistCheck;

    // 자식
    // 1) 인콰이어리 차단 회원
    @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("inquiry_block_id")
    private List<InquiryBlockEntity> inquiryBlockEntity;
    // 2) 상품
    @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("product_id")
    private List<ProductEntity> productEntity;
    // 3) 인콰이어리
    @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("inquiry_id")
    private List<InquiryEntity> inquiryEntity;
    // 4) 신고당한 회원
    @OneToMany(mappedBy = "customerEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("report_customer_id")
    private List<ReportCustomerEntity> reportCustomerEntity;

    public static CustomerEntity toEntity(CustomerDTO customerDTO) {
        return CustomerEntity.builder()
                .customerId(customerDTO.getCustomerId())
                .customerPw(customerDTO.getCustomerPw())
                .customerName(customerDTO.getCustomerName())
                .customerDepartment(customerDTO.getCustomerDepartment())
                .email(customerDTO.getEmail())
                .customerGubun(customerDTO.getCustomerGubun())
                .zipNo(customerDTO.getZipNo())
                .faxNo(customerDTO.getFaxNo())
                .busiNo(customerDTO.getBusiNo())
                .tradeNo(customerDTO.getTradeNo())
                .compName(customerDTO.getCompName())
                .compDesc(customerDTO.getCompDesc())
                .compUrl(customerDTO.getCompUrl())
                .address(customerDTO.getAddress())
                .remoteIp(customerDTO.getRemoteIp())
                .country(customerDTO.getCountry())
                .createDate(customerDTO.getCreateDate())
                .updateDate(customerDTO.getUpdateDate())
                .roles(customerDTO.getRoles())
                .enabled(customerDTO.getEnabled())
                .reportedCnt(customerDTO.getReportedCnt())
                .blacklistCheck(customerDTO.getBlacklistCheck())
                .build();
    }
    
}
