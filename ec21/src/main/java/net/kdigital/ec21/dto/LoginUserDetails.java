package net.kdigital.ec21.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.ToString;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ArrayList;

import net.kdigital.ec21.dto.check.CustomerGubun;
import net.kdigital.ec21.dto.check.YesOrNo;

@ToString
public class LoginUserDetails implements UserDetails{
    private static final long serialVersionUID =1L;
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
    
    
    // 생성자
    public LoginUserDetails(CustomerDTO customerDTO) {
        this.customerId = customerDTO.getCustomerId();
        this.customerPw = customerDTO.getCustomerPw();
        this.customerName = customerDTO.getCustomerName();
        this.customerDepartment = customerDTO.getCustomerDepartment();
        this.email = customerDTO.getEmail();
        this.customerGubun = customerDTO.getCustomerGubun();
        this.zipNo = customerDTO.getZipNo();
        this.faxNo = customerDTO.getFaxNo();
        this.busiNo = customerDTO.getBusiNo();
        this.tradeNo = customerDTO.getTradeNo();
        this.compName = customerDTO.getCompName();
        this.compDesc = customerDTO.getCompDesc();
        this.compUrl = customerDTO.getCompUrl();
        this.address = customerDTO.getAddress();
        this.remoteIp = customerDTO.getRemoteIp();
        this.country = customerDTO.getCountry();
        this.createDate = customerDTO.getCreateDate();
        this.updateDate = customerDTO.getUpdateDate();
        this.roles = customerDTO.getRoles();
        this.enabled = customerDTO.getEnabled();
        this.reportedCnt = customerDTO.getReportedCnt();
        this.blacklistCheck = customerDTO.getBlacklistCheck();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            private static final long serialVersionUID = 1L;
            @Override
            public String getAuthority(){
                return roles;
            }
        });
        return collection;
    }


    @Override
    public String getPassword() {
        return this.customerPw;
    }


    @Override
    public String getUsername() {
        return this.customerId;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    // 탈퇴 여부
    public YesOrNo getEnabled() {
        return enabled;
    }

    // 블랙리스트 여부
    public YesOrNo getBlacklistCheck() {
        return blacklistCheck;
    }

}
