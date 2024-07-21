package net.kdigital.ec21.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.kdigital.ec21.entity.InquiryEntity;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long>{

        // 받은 인콰이어리 중 확인하지 않은 인콰이어리 개수 반환
        @Query("SELECT COUNT(i) FROM InquiryEntity i WHERE i.receiverId = :customerId AND (i.checked = 'NN' OR i.checked = 'YN')")
        int countByCustomerIdAndChecked(@Param("customerId") String customerId);

        // Received : spam과 trash의 값이 NN이거나 YN이고 receiver가 신고한 회원ID에 senderId가 포함되지 않는 데이터만 조회
        @Query("SELECT i FROM InquiryEntity i WHERE i.receiverId = :customerId AND " +
                "(i.spam = 'NN' OR i.spam = 'YN') AND " +
                "(i.trash = 'NN' OR i.trash = 'YN') AND " +
                "(i.deleted = 'NN' OR i.deleted = 'YN') AND " +
                "NOT EXISTS (SELECT b FROM InquiryBlockEntity b WHERE b.customerEntity.customerId = i.receiverId AND b.blockedId = i.customerEntity.customerId) ORDER BY i.sendDate DESC")
        List<InquiryEntity> findNonBlockedInquiries(String customerId);

        // Sent : senderId와 전달받은 customerId가 일치하는 데이터들 중 trash값이 NN이거나 NY인 데이터 조회
        @Query("SELECT i FROM InquiryEntity i WHERE i.customerEntity.id = :customerId AND i.trash IN ('NN', 'NY') ORDER BY i.sendDate DESC")
        List<InquiryEntity> findInquiriesByCustomerIdAndTrashStatus(String customerId);
        
        // Saved 
        // 받은 인콰이어리 중 saved한 인콰이어리들 리스트로 반환
        @Query("SELECT i FROM InquiryEntity i " +
                "WHERE i.receiverId = :receiverId " +
                "AND i.saved IN ('NY', 'YY') " +
                "AND i.spam IN ('NN', 'YN') " +
                "AND i.trash IN ('NN', 'YN') " +
                "AND i.deleted IN ('NN', 'YN') " +
                "AND NOT EXISTS (SELECT b FROM InquiryBlockEntity b WHERE b.customerEntity.customerId = i.receiverId AND b.blockedId = i.customerEntity.customerId) ORDER BY i.sendDate DESC")
        List<InquiryEntity> findValidInquiries(@Param("receiverId") String customerId);

        // 보낸 인콰이어리 중 saved한 인콰이어리들 리스트로 반환
        @Query("SELECT i FROM InquiryEntity i " +
                "WHERE i.customerEntity.customerId = :customerId " +
                "AND i.saved IN ('YN', 'YY') " +
                "AND i.spam IN ('NN', 'NY') " +
                "AND i.trash IN ('NN', 'NY')" +
                "AND i.deleted IN ('NN', 'NY') ORDER BY i.sendDate DESC")
        List<InquiryEntity> findInquiriesBySender(@Param("customerId") String customerId);

        // Spam : 받은 인콰이어리 중 spam 값이 NY or YY 이고 trash의 값이 NN이거나 YN이고 
        // receiver가 신고한 회원ID에 senderId가 포함되지 않는 인콰이어리 리스트 반환
        @Query("SELECT i FROM InquiryEntity i WHERE i.receiverId = :customerId AND " +
                "(i.spam = 'NY' OR i.spam = 'YY') AND " +
                "(i.trash = 'NN' OR i.trash = 'YN') AND " +
                "(i.deleted = 'NN' OR i.deleted = 'YN') AND " +
                "NOT EXISTS (SELECT b FROM InquiryBlockEntity b WHERE b.customerEntity.customerId = i.receiverId AND b.blockedId = i.customerEntity.customerId) ORDER BY i.sendDate DESC")
        List<InquiryEntity> findNonBlockedAndNonSpamNonTrashInquiriesByReceiverId(String customerId);

        // Trash 
        // 받은 인콰이어리 중 trash 처리한 인콰이어리들
        @Query("SELECT i FROM InquiryEntity i WHERE i.receiverId = :customerId AND " +
                "(i.trash = 'YY' OR i.trash = 'NY') AND " +
                "(i.deleted = 'YN' OR i.deleted = 'NN') AND " +
                "NOT EXISTS (SELECT b FROM InquiryBlockEntity b WHERE b.customerEntity.customerId = i.receiverId AND b.blockedId = i.customerEntity.customerId) "+
                "ORDER BY i.sendDate DESC")
        List<InquiryEntity> findInquiriesByCustomerAndNotBlocked(
                        @Param("customerId") String customerId);

        // 보낸 인콰이어리 중 trash 처리한 인콰이어리들
        @Query("SELECT i FROM InquiryEntity i WHERE i.customerEntity.customerId = :customerId AND " +
                "(i.trash = 'YN' OR i.trash = 'YY') AND " +
                "(i.deleted = 'NN' OR i.deleted = 'NY') " +
                "ORDER BY i.sendDate DESC")
        List<InquiryEntity> findInquiriesBySenderAndFilteredStatus(
                        @Param("customerId") String customerId);


}
