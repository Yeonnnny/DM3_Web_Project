package net.kdigital.ec21.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.kdigital.ec21.dto.check.ReportCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.entity.ReportCustomerEntity;
import java.util.List;

public interface ReportCustomerRepository extends JpaRepository<ReportCustomerEntity, Long> {
        // 미처리된 신고 개수 반환
        long countByManagerCheck(YesOrNo yesOrNo);

        // 미처리된 신고데이터들을 최근 신고된 날짜 순으로 반환
        List<ReportCustomerEntity> findByManagerCheckOrderByReportDateDesc(YesOrNo managerCheck);


        // 신고테이블에서 블랙리스트가 아니고, 관리자가 미처리한 데이터들 중에  
        // 신고당한 회원ID, 담당자명, 회사명 필드에 검색어가 포함된 데이터들을 최근 신고된 날짜 순으로 반환
        @Query("SELECT r FROM ReportCustomerEntity r " +
                "JOIN r.customerEntity c " +
                "WHERE r.managerCheck = 'N' AND c.blacklistCheck = 'N' AND " +
                "(LOWER(c.customerId) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
                "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
                "LOWER(c.compName) LIKE LOWER(CONCAT('%', :searchWord, '%')))" +
                "ORDER BY r.reportDate DESC")
        List<ReportCustomerEntity> findBySearchWordWithManagerCheckNAndBlacklistCheckNOrderByReportDateDesc(@Param("searchWord") String searchWord);
        


        // 신고테이블에서 블랙리스트가 아니고, 관리자가 미처리한 데이터들 중에 전달받은 신고 카테고리에 해당하고
        // 신고당한 회원ID, 담당자명, 회사명 필드에 검색어가 포함된 데이터들을 최근 신고된 날짜 순으로 반환
        @Query("SELECT r FROM ReportCustomerEntity r " +
                        "JOIN r.customerEntity c " +
                        "WHERE r.managerCheck = 'N' AND c.blacklistCheck = 'N' AND " +
                        "r.reportCategory = :category AND " +
                        "(LOWER(c.customerId) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
                        "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
                        "LOWER(c.compName) LIKE LOWER(CONCAT('%', :searchWord, '%')))" +
                        "ORDER BY r.reportDate DESC")
        List<ReportCustomerEntity> findByCategoryAndSearchWordWithManagerCheckNAndBlacklistCheckNOrderByReportDateDesc(
                        @Param("category") ReportCategory category,
                        @Param("searchWord") String searchWord);


        // // 신고테이블에서 미처리된 데이터들 중에
        // // 신고당한 회원ID, 담당자명, 회사명 필드에 검색어가 포함된 데이터들을 최근 신고된 날짜 순으로 반환
        // @Query("SELECT rc FROM ReportCustomerEntity rc JOIN rc.customerEntity c WHERE " +
        //                 "rc.managerCheck = 'N' AND " +
        //                 "(LOWER(c.customerId) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
        //                 "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
        //                 "LOWER(c.compName) LIKE LOWER(CONCAT('%', :searchWord, '%')))" +
        //                 "ORDER BY rc.reportDate DESC")
        // List<ReportCustomerEntity> findBySearchWordWithManagerCheckNOrderByReportDateDesc(
        //                 @Param("searchWord") String searchWord);

        // // 신고테이블에서 미처리된 데이터들 중에 전달받은 신고 카테고리에 해당하고
        // // 신고당한 회원ID, 담당자명, 회사명 필드에 검색어가 포함된 데이터들을 최근 신고된 날짜 순으로 반환
        // @Query("SELECT p FROM ReportCustomerEntity p WHERE " +
        //                 "p.reportCategory = :category AND " +
        //                 "(LOWER(p.customerEntity.customerId) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
        //                 "LOWER(p.customerEntity.customerName) LIKE LOWER(CONCAT('%', :searchWord, '%')) OR " +
        //                 "LOWER(p.customerEntity.compName) LIKE LOWER(CONCAT('%', :searchWord, '%'))) AND " +
        //                 "p.managerCheck = 'N' " +
        //                 "ORDER BY p.reportDate DESC")
        // List<ReportCustomerEntity> findByReportCategoryAndMultipleFieldsContainingOrderByReportDateDesc(
        //                 @Param("category") ReportCategory category, @Param("searchWord") String searchWord);
}
