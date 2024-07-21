package net.kdigital.ec21.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.kdigital.ec21.dto.check.ReportCategory;
import net.kdigital.ec21.entity.BlacklistEntity;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Long> {

        // 회원Id와 회사명에 검색어가 포함된 데이터들을 최근 등록된 순으로 반환
        @Query("SELECT b FROM BlacklistEntity b " +
                "WHERE LOWER(b.customerId) LIKE %:searchWord% " +
                "OR LOWER(b.compName) LIKE %:searchWord% " +
                "ORDER BY b.inputDate DESC")
        List<BlacklistEntity> findByCustomerIdOrCompNameContainingAndOrderByInputDateDesc(
                @Param("searchWord") String searchWord);
                
                
        // 전달받은 블랙카테고리에 해당하고, 회원Id와 회사명에 검색어가 포함된 데이터들을 최근 등록된 순으로 반환
        @Query("SELECT b FROM BlacklistEntity b " +
                "WHERE (LOWER(b.customerId) LIKE %:searchWord% OR LOWER(b.compName) LIKE %:searchWord%) " +
                "AND b.blackType = :blackType " +
                "ORDER BY b.inputDate DESC")
        List<BlacklistEntity> findBySearchWordAndBlackTypeOrderByInputDateDesc(
                @Param("searchWord") String searchWord,
                @Param("blackType") ReportCategory blackType);


        //전달받은 회원ID와 동일한 customerId가 있다면 그 데이터 반환
        Optional<BlacklistEntity> findByCustomerId(String customerId);

}
