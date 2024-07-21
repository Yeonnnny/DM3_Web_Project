package net.kdigital.ec21.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.CustomerGubun;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class InquiryBlockedCustomerDTO {
    private Long inquiryBlockId;
    private String cutomerId;
    private String blockedId;
    private CustomerGubun customerGubun;
    private String compName;
    private String remoteIp;
    private String country;
}
