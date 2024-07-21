package net.kdigital.ec21.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.kdigital.ec21.entity.CustomerEntity;
import java.util.List;
import net.kdigital.ec21.dto.check.YesOrNo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    // 당일 등록한 회원 수 반환
    long countByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 정상 회원 (blacklist_check==N)인 모든 customerEntity 최신 등록 순으로 리스트 반환
    List<CustomerEntity> findByBlacklistCheckOrderByCreateDateDesc(YesOrNo blacklistCheck);

    // 정상 회원인 회원들 중 회원ID 혹은 회사명에 검색어가 포함된 회원들을 최신 등록 순으로 반환
    @Query("SELECT c FROM CustomerEntity c WHERE " +
        "(LOWER(c.customerId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(c.compName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
        "c.blacklistCheck = :blacklistStatus " +
        "ORDER BY c.createDate DESC")
    List<CustomerEntity> findBySearchTermAndNotBlacklisted(
            @Param("searchTerm") String searchTerm,
            @Param("blacklistStatus") YesOrNo blacklistStatus);

            
    // 정상 회원 중 회원ID에 검색어가 포함된 회원들을 최신 등록 순으로 반환
    @Query("SELECT c FROM CustomerEntity c WHERE " +
        "LOWER(c.customerId) LIKE LOWER(CONCAT('%', :customerId, '%')) AND " +
        "c.blacklistCheck = :blacklistStatus " +
        "ORDER BY c.createDate DESC")
    List<CustomerEntity> findByCustomerIdContainingAndBlacklistCheckOrderByCreateDateDesc(
            @Param("customerId") String customerId,
            @Param("blacklistStatus") YesOrNo blacklistStatus);


    // 정상 회원 중 회사명에 검색어가 포함된 회원들을 최신 등록 순으로 반환
    @Query("SELECT c FROM CustomerEntity c WHERE " +
            "LOWER(c.compName) LIKE LOWER(CONCAT('%', :compName, '%')) AND " +
            "c.blacklistCheck = :blacklistStatus " +
            "ORDER BY c.createDate DESC")
    List<CustomerEntity> findByCompNameContainingAndBlacklistCheckOrderByCreateDateDesc(
            @Param("compName") String compName,
            @Param("blacklistStatus") YesOrNo blacklistStatus);


}
