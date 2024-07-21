package net.kdigital.ec21.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.kdigital.ec21.dto.check.CustomerGubun;
import net.kdigital.ec21.dto.check.ReportCategory;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
// 신고 회원 및 블랙리스트 목록 조회를 위한 DTO
public class ReportedCustomerWithInfoDTO {
    private String reportedId;              // 회원ID
    private Long reportCustomerId;          // 테이블ID(일련번호) - reportCustomerId / blacklistId
    private int reportedCount;              // 회원의 신고당한 횟수
    private CustomerGubun customerGubun;    // 회원의 구분
    private String customerName;            // 회원의 이름
    private String customerComp;            // 회원의 회사명
    private String customerDepartment;      // 회원의 부서명
    private String remoteIp;                // 회원 정보의 remoteIP
    private ReportCategory reportCategory;  // 신고 카테고리 / 블랙 타입
    private String reportReason;            // 신고 사유 / 블랙 사유
}
