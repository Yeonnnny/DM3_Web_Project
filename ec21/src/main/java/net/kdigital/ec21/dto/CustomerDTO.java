package net.kdigital.ec21.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.CustomerGubun;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.entity.CustomerEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class CustomerDTO {
    private String customerId;
    private String customerPw;
    private String customerName;
    private String customerDepartment;
    private String email;
    private CustomerGubun customerGubun;
    private String zipNo;
    private String faxNo;
    private String busiNo;
    private String tradeNo;
    private String compName;
    private String compDesc;
    private String compUrl;
    private String address;
    private String remoteIp;
    private String country;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String roles;
    private YesOrNo enabled;
    private int reportedCnt;
    private YesOrNo blacklistCheck;

    public static CustomerDTO toDTO(CustomerEntity customerEntity) {
        return CustomerDTO.builder()
                .customerId(customerEntity.getCustomerId())
                .customerPw(customerEntity.getCustomerPw())
                .customerName(customerEntity.getCustomerName())
                .customerDepartment(customerEntity.getCustomerDepartment())
                .email(customerEntity.getEmail())
                .customerGubun(customerEntity.getCustomerGubun())
                .zipNo(customerEntity.getZipNo())
                .faxNo(customerEntity.getFaxNo())
                .busiNo(customerEntity.getBusiNo())
                .tradeNo(customerEntity.getTradeNo())
                .compName(customerEntity.getCompName())
                .compDesc(customerEntity.getCompDesc())
                .compUrl(customerEntity.getCompUrl())
                .address(customerEntity.getAddress())
                .remoteIp(customerEntity.getRemoteIp())
                .country(customerEntity.getCountry())
                .createDate(customerEntity.getCreateDate())
                .updateDate(customerEntity.getUpdateDate())
                .roles(customerEntity.getRoles())
                .enabled(customerEntity.getEnabled())
                .reportedCnt(customerEntity.getReportedCnt())
                .blacklistCheck(customerEntity.getBlacklistCheck())
                .build();
    }
}
