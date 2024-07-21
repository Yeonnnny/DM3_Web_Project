package net.kdigital.ec21.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import net.kdigital.ec21.entity.ProhibitSimilarWordEntity;

public interface ProhibitSimilarWordRepository extends JpaRepository<ProhibitSimilarWordEntity,Long>{

    // productId에 해당하는 데이터들을 이상단어 유사확률이 높은 순으로 리스트 반환
    List<ProhibitSimilarWordEntity> findProbaByProductEntity_ProductIdOrderBySimilarProbaDesc(String productId);
    
    // productId에 해당하는 데이터들을 금지어 유사단어의 알파벳 오름차순, 유사확률의 내림차순으로 리스트 반환
    List<ProhibitSimilarWordEntity> findByProductEntity_ProductIdOrderBySimilarWordAscSimilarProbaDesc(String productId);

    // productId에 해당하는 모든 ProhibitSimilarWordEntity 객체를 찾는 메서드
    List<ProhibitSimilarWordEntity> findByProductEntity_ProductId(String productId);

    // productId에 해당하는 ProhibitSimilarWordEntity 객체를 삭제하는 메서드
    @Transactional
    @Modifying
    @Query("DELETE FROM ProhibitSimilarWordEntity p WHERE p.productEntity.productId = :productId")
    void deleteByProductEntity_ProductId(String productId);

}
