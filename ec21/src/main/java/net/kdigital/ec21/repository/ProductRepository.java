package net.kdigital.ec21.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.kdigital.ec21.dto.check.ProductCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {

        // ================== 메인보드 : count 관련 ========================
        // 당일 등록 상품 개수
        Long countByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

        // 당일 등록된 이상 상품 개수
        Long countByCreateDateBetweenAndLstmPredict(LocalDateTime startDate, LocalDateTime endDate,
                        boolean lstmPredict);

        // updateDate 또는 createDate가 오늘인 데이터 중에서 lstmPredict가 false인 데이터의 개수를 세는 메서드
        @Query("SELECT COUNT(p) FROM ProductEntity p WHERE " +
                        "(p.updateDate BETWEEN :startOfDay AND :endOfDay OR p.createDate BETWEEN :startOfDay AND :endOfDay) "
                        +
                        "AND p.lstmPredict = FALSE")
        Long countByDateAndLstmPredictFalse(LocalDateTime startOfDay, LocalDateTime endOfDay);

        // ============================ 관리자 전체 상품 화면 =================================

        // 회원ID, 상품ID, 상품명 (3개지 필드 비교)에 전달받은 검색어가 포함된 상품들을 정렬 조건순으로 반환
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord% AND " +
                        "p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContaining(@Param("searchWord") String searchWord, Sort sort);

        // 전달받은 카테고리에 해당하는 상품 중 회원ID, 상품ID, 상품명에 전달받은 검색어가 포함된 상품들을 정렬 조건순으로 반환
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "p.category = :category AND " + // 카테고리 조건 추가
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND "+
                        "p.productDelete = 'N'")
        List<ProductEntity> findByCategoryAndMultipleFieldsContaining(
                        @Param("category") ProductCategory category, // 카테고리 파라미터 추가
                        @Param("searchWord") String searchWord,
                        Sort sort);

        // judge == null 
        // 검색어가 존재하는 경우
        @Query("SELECT p FROM ProductEntity p WHERE " +
                "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                "LOWER(p.productId) LIKE %:searchWord% OR " +
                "LOWER(p.productName) LIKE %:searchWord%) AND " +
                "p.judge IS NULL AND p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsNull(@Param("searchWord") String searchWord, Sort sort);
        // 카테고리와 검색어가 존재하는 경우
        @Query("SELECT p FROM ProductEntity p WHERE " +
                "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                "LOWER(p.productId) LIKE %:searchWord% OR " +
                "LOWER(p.productName) LIKE %:searchWord%) AND " +
                "p.judge IS NULL AND " +
                "p.category = :category AND p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsNullAndCategory(
                @Param("searchWord") String searchWord,
                @Param("category") ProductCategory category,
                Sort sort);

        // judge == Y
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.judge = 'Y' AND p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsY(@Param("searchWord") String searchWord,
                        Sort sort);

        // 카테고리와 검색어가 존재하는 경우
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.judge = 'Y' AND " +
                        "p.category = :category AND p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsYAndCategory(
                        @Param("searchWord") String searchWord,
                        @Param("category") ProductCategory category,
                        Sort sort);
        
        
        // judge == N
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.judge = 'N' AND p.productDelete = 'N' ")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsN(@Param("searchWord") String searchWord,
                        Sort sort);

        // 카테고리와 검색어가 존재하는 경우
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.judge = 'N' AND " +
                        "p.category = :category AND p.productDelete = 'N'")
        List<ProductEntity> findByMultipleFieldsContainingAndJudgeIsNAndCategory(
                        @Param("searchWord") String searchWord,
                        @Param("category") ProductCategory category,
                        Sort sort);
        

        // =================== 모델로 이상 상품 판별 화면 ========================
        // lstmPredict 값이 이상, 즉 false(0)인 모든 ProductEntity 리스트로 반환
        List<ProductEntity> findByLstmPredict(boolean lstmPredict);

        // lstmPredict 값이 이상, 즉 false(0)이고 judge가 null(관리자 미처리)인 모든 ProductEntity 리스트 반환
        // (검색어 X, 최신 상품 등록일 순으로)
        List<ProductEntity> findByLstmPredictAndJudgeOrderByCreateDateDesc(boolean lstmPredict, String judgeIsNull);

        // lstmPredict 값이 이상, 즉 false(0)이고 judge가 null(관리자 미처리)인 모든 ProductEntity들 중에
        // 전달 받은 검색어가 회원ID, 상품ID, 상품명에 포함된 상품들의 리스트를 최신 등록 순으로 반환
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.lstmPredict = false AND " +
                        "p.judge IS NULL AND p.productDelete = 'N' " +
                        "ORDER BY p.createDate DESC")
        List<ProductEntity> findByMultipleFieldsContainingOrderByCreateDateDesc(@Param("searchWord") String searchWord);

        // lstmPredict 값이 이상, 즉 false(0)이고 judge가 null(관리자 미처리)인 모든 ProductEntity들 중에
        // 전달 받은 카테고리에 해당하는 상품들 중에 전달 받은 검색어가 회원ID, 상품ID, 상품명에 포함된 상품들의 리스트를 최신 등록 순으로
        // 반환
        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "p.category = :category AND " +
                        "(LOWER(p.customerEntity.customerId) LIKE %:searchWord% OR " +
                        "LOWER(p.productId) LIKE %:searchWord% OR " +
                        "LOWER(p.productName) LIKE %:searchWord%) AND " +
                        "p.lstmPredict = false AND " +
                        "p.judge IS NULL AND p.productDelete = 'N' " +
                        "ORDER BY p.createDate DESC")
        List<ProductEntity> findByCategoryAndMultipleFieldsContainingOrderByCreateDateDesc(
                        @Param("category") ProductCategory category, @Param("searchWord") String searchWord);

        // ============================== 메인페이지 ==================================

        // 상위(조회수, lstmPredictProba, 등록일) 8개 상품 데이터 조회를 위해 Pageable 사용 ( pageable :정렬
        // 조건과 조회 개수 조건 넘김)
        @Query("SELECT p FROM ProductEntity p JOIN p.customerEntity c " +
                        "WHERE p.judge = :judge AND c.blacklistCheck = :blacklistCheck AND p.productDelete = 'N'")
        Page<ProductEntity> findTopProductsByJudgeAndBlacklistCheckAndNotDeleted(
                        @Param("judge") YesOrNo judge,
                        @Param("blacklistCheck") YesOrNo blacklistCheck,
                        Pageable pageable);

        // ================ 메인 카테고리별 상품 목록 화면 ===================

        // 상품명에 전달받은 검색어가 포함된 상품들 중
        // judge==Y & productDelete==N & customerEntity의 blacklistCheck == N 인 상품들을
        // 최신 등록 순으로 반환
        @Query("SELECT p FROM ProductEntity p " +
                        "WHERE LOWER(p.productName) LIKE %:searchWord% " +
                        "AND p.judge = 'Y' " +
                        "AND p.productDelete = 'N' " +
                        "AND p.customerEntity.blacklistCheck = 'N' " +
                        "ORDER BY p.createDate DESC")
        List<ProductEntity> findProductsByProductNameContainingAndAdditionalConditions(
                        @Param("searchWord") String searchWord);

        // 위의 jpa에 카테고리 조건 추가 ver
        @Query("SELECT p FROM ProductEntity p " +
                        "WHERE LOWER(p.productName) LIKE %:searchWord% " +
                        "AND p.judge = 'Y' " +
                        "AND p.productDelete = 'N' " +
                        "AND p.customerEntity.blacklistCheck = 'N' " +
                        "AND p.category = :category " +
                        "ORDER BY p.createDate DESC")
        List<ProductEntity> findProductsByProductNameContainingAndAdditionalConditionsAndCategory(
                        @Param("searchWord") String searchWord,
                        @Param("category") ProductCategory category);

        // ============== 메인 카테고리별 상품 목록 화면 ===================

        // 전달받은 카테고리에 해당하고 전달받은 상품id에 해당하는 상품은 제외한 상품들 중
        // 상위(조회수, lstmPredictProba, 등록일) 5개 상품 데이터 조회를 위해 Pageable 사용
        // (pageable :정렬 조건과 조회 개수 조건 넘김)
        @Query("SELECT p FROM ProductEntity p JOIN p.customerEntity c " +
                        "WHERE p.judge = 'Y' AND c.blacklistCheck = 'N' AND p.productDelete = 'N' " +
                        "AND p.category = :category AND p.productId != :productId")
        Page<ProductEntity> findTopProductsByCategoryAndJudgeAndBlacklistCheckAndNotDeletedExcludingProductId(
                        @Param("category") ProductCategory category,
                        @Param("productId") String productId,
                        Pageable pageable);

        // =============== 관리자페이지 mainboard 화면 ====================
        // 상품등록일자별 카운트하여 전달
        @Query(value = "SELECT TO_CHAR(TRUNC(create_date), 'YYYY-MM-DD') as truncated_date, COUNT(*) as count " +
                        "FROM product " +
                        "GROUP BY TRUNC(create_date) " +
                        "ORDER BY TRUNC(create_date)", nativeQuery = true)
        List<Object[]> countProductsByCreationDate();

        // ============= 관리자페이지 mainboard 화면 =====================
        // 카테고리별 장상/이상 개수 전달
        @Query(value = "SELECT category, " +
                        "SUM(CASE WHEN lstm_predict = 0 THEN 1 ELSE 0 END) AS judge_0_count, " +
                        "SUM(CASE WHEN lstm_predict = 1 THEN 1 ELSE 0 END) AS judge_1_count " +
                        "FROM product " +
                        "GROUP BY category ORDER BY category", nativeQuery = true)
        List<Object[]> findCategoryCounts();

        // ============ 관리자페이지 mainboard 화면 =================
        // 등록된 상품의 등록일자, 카테고리별 개수를 전달.
        @Query(value = "SELECT TRUNC(create_date) as truncated_date, category, COUNT(*) as count " +
                        "FROM product " +
                        "WHERE create_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY TRUNC(create_date), category " +
                        "ORDER BY TRUNC(create_date), category", nativeQuery = true)

        List<Object[]> countProductsByCategoryAndDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // ============ 관리자페이지 mainboard 화면 ==================
        // 금지어 top 10
        @Query(value = "SELECT similar_word, COUNT(*) as count " +
                        "FROM prohibit_similar_word " +
                        "GROUP BY similar_word " +
                        "ORDER BY count DESC", nativeQuery = true)
        Page<Object[]> countSimilarWords(Pageable pageable);

}
