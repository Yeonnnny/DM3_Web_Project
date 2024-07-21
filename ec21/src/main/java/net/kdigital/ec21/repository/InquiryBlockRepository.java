package net.kdigital.ec21.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.kdigital.ec21.entity.InquiryBlockEntity;

public interface InquiryBlockRepository extends JpaRepository<InquiryBlockEntity, Long>{
    
    // 전달받은 customerId와 customerId와 일치하는 모든 데이터 반환
    List<InquiryBlockEntity> findByCustomerEntity_CustomerId(String customerId);
}
