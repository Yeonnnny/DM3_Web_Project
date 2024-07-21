package net.kdigital.ec21.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.InquiryBlockDTO;
import net.kdigital.ec21.dto.InquiryBlockedCustomerDTO;
import net.kdigital.ec21.dto.InquiryDTO;
import net.kdigital.ec21.dto.InquiryModalDTO;
import net.kdigital.ec21.dto.InquiryPlusCustomerIdDTO;
import net.kdigital.ec21.dto.ReportCustomerDTO;
import net.kdigital.ec21.dto.check.InquiryEnum;
import net.kdigital.ec21.entity.CustomerEntity;
import net.kdigital.ec21.entity.InquiryBlockEntity;
import net.kdigital.ec21.entity.InquiryEntity;
import net.kdigital.ec21.entity.ProductEntity;
import net.kdigital.ec21.entity.ReportCustomerEntity;
import net.kdigital.ec21.repository.CustomerRepository;
import net.kdigital.ec21.repository.InquiryBlockRepository;
import net.kdigital.ec21.repository.InquiryRepository;
import net.kdigital.ec21.repository.ProductRepository;
import net.kdigital.ec21.repository.ReportCustomerRepository;
import net.kdigital.ec21.util.FileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryBlockRepository inquiryBlockRepository;
    private final ReportCustomerRepository reportCustomerRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    // ============================= main/index ==============================
    
    /**
     * 전달받은 사용자ID가 받은 인콰이어리 중에 읽지 않은 인콰이어리 개수 반환하는 함수
     * @param customerId
     * @return
     */
    public int getInboxNotReadCount(String customerId) {
        int count =  inquiryRepository.countByCustomerIdAndChecked(customerId);
        return count;
    }

    
    // ============================= 인콰이어리 등록 ==============================

    // 인콰이어리 업로드 파일 저장할 위치
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    /**
     * 인콰이어리를 DTO로 받아서 인콰이어리 DB에 저장
     * 
     * @param inquiryDTO
     */
    public void insertinquiry(InquiryDTO inquiryDTO) {

        inquiryDTO.setSaved(InquiryEnum.NN);
        inquiryDTO.setTrash(InquiryEnum.NN);
        inquiryDTO.setSpam(InquiryEnum.NN);
        inquiryDTO.setDeleted(InquiryEnum.NN);
        inquiryDTO.setChecked(InquiryEnum.NN);

        // 대표 이미지 저장을 위한 경로
        String originalFileName = null;
        String savedFileName = null;

        // 첨부파일이 있으면 파일명 세팅 실시
        if (!inquiryDTO.getUploadFile().isEmpty()) {
            originalFileName = inquiryDTO.getUploadFile().getOriginalFilename();
            savedFileName = FileService.saveFile(inquiryDTO.getUploadFile(), uploadPath);

            inquiryDTO.setOriginalFileName(originalFileName);
            inquiryDTO.setSavedFileName(savedFileName); // entity로 변환 전 dto의 savedFileName 변경해주기
        }

        CustomerEntity customerEntity = customerRepository.findById(inquiryDTO.getSenderId()).get();
        ProductEntity productEntity = productRepository.findById(inquiryDTO.getProductId()).get();

        inquiryRepository.save(InquiryEntity.toEntity(inquiryDTO, customerEntity, productEntity));
    }

    
    //===================================== 수신확인 ===============================
    /**
     * 전달받은 inquiryId의 recieverId와 customerId가 일치하는 경우, reciever의 checked 값을 변경하는 함수
     * @param inquiryId
     * @param customerId
     */
    @Transactional
    public void updateCheckedYes(String inquiryId, String customerId){
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            
            if (!entity.getReceiverId().equals(customerId)) {
                return; // 로그인한 사용자와 recieverId와 일치하지 않는 경우 return
            }else{
                // reciever의 checked 상태 변경 (NN->NY || YN->YY)
                if (entity.getChecked() == InquiryEnum.NN) {
                    entity.setChecked(InquiryEnum.NY);
                } else if (entity.getChecked() == InquiryEnum.YN) {
                    entity.setChecked(InquiryEnum.YY);
                } 
            }
        }


    }


    //===================================== Received Page ===============================
    
    /**
     * receiverId가 customerId와 일치하는 모든 인콰이어리들을 리스트로 반환하는 함수
     * (조건 : span==N, trash ==N, senderId != blackId)
     * @param customerId
     * @return
     */
    public List<InquiryDTO> getReceivedInquiry(String customerId) {
        List<InquiryEntity> inquiryEntities = inquiryRepository.findNonBlockedInquiries(customerId);
        List<InquiryDTO> dtos = new ArrayList<>();

        inquiryEntities.forEach((entity)->{
            dtos.add(InquiryDTO.toDTO(entity,entity.getCustomerEntity().getCustomerId(), entity.getProductEntity().getProductId()));
        });
        
        return dtos;
    }


    /**
     * inquiryId에 해당하는 인콰이어리 정보들과 productId에 해당하는 상품 정보들 중
     * 인콰이어리 모달창에 필요한 데이터들로 새로운 DTO를 만들어 반환하는 함수
     * @param inquiryId
     * @return
     */
	public InquiryModalDTO getInquiryModalDTO(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        // 인콰이어리 정보 가져오기
        InquiryEntity inquiryEntity = inquiryRepository.findById(id).get();
        // 상품 정보 가져오기
        ProductEntity productEntity = productRepository.findById(inquiryEntity.getProductEntity().getProductId()).get();
        // 새로운 인콰이어리 모달 DTO 생성
        InquiryModalDTO dto = new InquiryModalDTO(id, inquiryEntity.getCustomerEntity().getCustomerId(), 
                                    inquiryEntity.getReceiverId(), productEntity.getProductName(), 
                                    productEntity.getCategory(), inquiryEntity.getQuantity(), 
                                    productEntity.getUnit(), inquiryEntity.getInquiryTitle(), 
                                    inquiryEntity.getInquiryContent(), inquiryEntity.getOriginalFileName(), 
                                    inquiryEntity.getSavedFileName());
        return dto;
	}


    /**
     * 전달받은 인콰이어리ID에 해당하는 인콰이어리DTO 반환하는 함수
     * @param id
     * @return
     */
    public InquiryDTO getInquiry(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        InquiryEntity entity = inquiryRepository.findById(id).get();
        return InquiryDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId(), entity.getProductEntity().getProductId());
    }


    /************************
     * Receiver의 saved 관련
    *************************/

    /**
     * 저장 X : 전달받은 inquiryId에 해당하는 인콰이어리의 receiver의 saved 값을 NN혹은 YN으로 변경
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean receiverSavedNo(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();

        // saved 상태 변경
        if (entity.getSaved() == InquiryEnum.NY) {
            entity.setSaved(InquiryEnum.NN);
        } else if (entity.getSaved() == InquiryEnum.YY) {
            entity.setSaved(InquiryEnum.YN);
        }

        return true;
    }

    /**
     * 저장 O : 전달받은 inquiryId에 해당하는 인콰이어리의 receiver의 saved 값을 NY혹은 YY로 변경
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean receiverSavedYes(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();

        // saved 상태 변경
        if (entity.getSaved() == InquiryEnum.NN) {
            entity.setSaved(InquiryEnum.NY);
        } else if(entity.getSaved() == InquiryEnum.YN){
            entity.setSaved(InquiryEnum.YY);
        }

        return true;
    }
    
    /************************
     * Receiver의 spam 관련
     *************************/
    
    /**
     * 입력받은 인콰이어리 ID를 Long형으로 변경한 후 spam 값을 N->Y로 변경하는 함수
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean receiverToSpam(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional <InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get(); 
        if (entity.getSpam()==InquiryEnum.NN) {
            entity.setSpam(InquiryEnum.NY);
        }else{
            entity.setSpam(InquiryEnum.YY);
        }
        return true;
    }

    /************************
     * Receiver의 trash 관련
     *************************/

    /**
     * 입력받은 인콰이어리 ID를 Long형으로 변경한 후 trash 값을 N->Y로 변경하는 함수
     * 
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean receiverToTrash(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();
        if (entity.getTrash() == InquiryEnum.NN) {
            entity.setTrash(InquiryEnum.NY);
        } else {
            entity.setTrash(InquiryEnum.YY);
        }
        return true;
    }

    // ================================= Sent Page ===================================

    /**
     * senderId가 전달받은 customerId와 일치하는 인콰이어리 반환 (trash==NY or trash==NN)
     * @param customerId
     * @return
     */
    public List<InquiryDTO> getSentInquiry(String customerId) {
        List<InquiryEntity> inquiryEntities = inquiryRepository.findInquiriesByCustomerIdAndTrashStatus(customerId);
        List<InquiryDTO> dtos = new ArrayList<>();

        inquiryEntities.forEach((entity)->{
            dtos.add(InquiryDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId(), entity.getProductEntity().getProductId()));
        });
        
        return dtos;
    }

    /************************
     * Sender의 saved 관련
     *************************/

    /**
     * 전달받은 인콰이어리ID에 해당하는 인콰이어리의 sender의 saved 값을 N으로 변경하는 함수
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean senderSavedNo(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();

        // saved 상태 변경
        if (entity.getSaved() == InquiryEnum.YN) {
            entity.setSaved(InquiryEnum.NN);
        } else if (entity.getSaved() == InquiryEnum.YY) {
            entity.setSaved(InquiryEnum.NY);
        }

        return true;
    }

    /**
     * 전달받은 인콰이어리ID에 해당하는 인콰이어리의 sender의 saved 값을 N으로 변경하는 함수
     * @param inquiryId
     * @return
     */
    @Transactional
    public Boolean senderSavedYes(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();

        // saved 상태 변경
        if (entity.getSaved() == InquiryEnum.NN) {
            entity.setSaved(InquiryEnum.YN);
        } else if (entity.getSaved() == InquiryEnum.NY) {
            entity.setSaved(InquiryEnum.YY);
        }

        return true;
    }


    /************************
     * Sender의 trash 관련
     *************************/
    
    /**
      * 전달받은 인콰이어리Id를 Long형으로 바꾸고 sender의 trash값 Y로 변환하는 함수
      * @param inquiryId
      * @return
      */
    @Transactional
    public Boolean senderToTrash(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);

        if (!inquiryEntity.isPresent()) {
            return false;
        }
        InquiryEntity entity = inquiryEntity.get();
        if (entity.getTrash() == InquiryEnum.NN) {
            entity.setTrash(InquiryEnum.YN);
        } else if(entity.getTrash() == InquiryEnum.NY){
            entity.setTrash(InquiryEnum.YY);
        }
        return true;
    }

    //=========================== Saved Page ==========================


    /**
     * 인콰이어리의 receiverId 또는 senderId와 전달받은 회원ID가 일치하는 
     * 모든 인콰이어리들 중에 saved값이 Y인 인콰이어리들 반환하는 함수  
     * @param customerId
     * @return
     */
    public List<InquiryDTO> getSavedInquiry(String customerId) {
        // 두 쿼리 결과를 각각 조회
        List<InquiryEntity> inquiriesByReceiver = inquiryRepository.findValidInquiries(customerId);
        List<InquiryEntity> inquiriesBySender = inquiryRepository.findInquiriesBySender(customerId);

        // 두 리스트를 스트림으로 합치고 sendDate로 내림차순 정렬
        List<InquiryEntity> resultEntities = Stream.concat(inquiriesByReceiver.stream(), inquiriesBySender.stream())
                .sorted((i1, i2) -> i2.getSendDate().compareTo(i1.getSendDate()))
                .collect(Collectors.toList());

        List<InquiryDTO> resultDtos = new ArrayList<>();
        resultEntities.forEach((entity)->{
            resultDtos.add(InquiryDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId(),entity.getProductEntity().getProductId()));
        });
        
        return resultDtos;
    }

    /**
     * 화면에 필요한 정보만 뿌려주기 위한 새로운 DTO에 정보 담아 반환
     * 
     * @param customerId
     * @return
     */
    public List<InquiryPlusCustomerIdDTO> getSavedInquiryPlusCustomerId(String customerId) {
        // 두 쿼리 결과를 각각 조회
        List<InquiryEntity> inquiriesByReceiver = inquiryRepository.findValidInquiries(customerId);
        List<InquiryEntity> inquiriesBySender = inquiryRepository.findInquiriesBySender(customerId);

        // 두 리스트를 스트림으로 합치고 sendDate로 내림차순 정렬
        List<InquiryEntity> resultEntities = Stream.concat(inquiriesByReceiver.stream(), inquiriesBySender.stream())
                .sorted((i1, i2) -> i2.getSendDate().compareTo(i1.getSendDate()))
                .collect(Collectors.toList());

        List<InquiryPlusCustomerIdDTO> dtos = new ArrayList<>();

        resultEntities.forEach((entity) -> {
            dtos.add(new InquiryPlusCustomerIdDTO(entity.getInquiryId(), entity.getCustomerEntity().getCustomerId(),
                    entity.getReceiverId(), entity.getInquiryTitle(), entity.getSendDate(), entity.getSaved(),
                    entity.getTrash(), entity.getSpam(),entity.getDeleted(), customerId));
        });

        return dtos;
    }

    /**
     * saved 화면에서 요청된 saved 해제 요청에 따라 Y->N으로 변경하는 함수
     */
    @Transactional
    public void updateSavedNo(String inquiryId, String customerId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            if (entity.getSaved() == InquiryEnum.NY) {
                entity.setSaved(InquiryEnum.NN);
            }else if (entity.getSaved() == InquiryEnum.YN) {
                entity.setSaved(InquiryEnum.NN);
            }else {
                if (customerId.equals(entity.getReceiverId())) {
                    entity.setSaved(InquiryEnum.YN);
                }else{
                    entity.setSaved(InquiryEnum.NY);
                }
            }
        }
    }

    /**
     * saved 화면에서 요청된 spam 작업. 이미 리스트에는 trash, spam에 해당하지 않는 데이터들이기 떄문에 값만 변경하면 될 듯
     */
    @Transactional
    public void savedToSpam(String inquiryId, String customerId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            if (entity.getSpam() == InquiryEnum.NY) {
                entity.setSpam(InquiryEnum.YY);
            } else if (entity.getSpam() == InquiryEnum.YN) {
                entity.setSpam(InquiryEnum.YY);
            } else {
                if (customerId.equals(entity.getReceiverId())) {
                    entity.setSpam(InquiryEnum.NY);
                    return;
                } else {
                    entity.setSpam(InquiryEnum.YN);
                }
            }
        }
    }

    /**
     * saved 화면에서 요청된 trash 작업. 이미 리스트에는 trash, spam에 해당하지 않는 데이터들이기 때문에 값만 변경하면 될 듯
     * @param inquiryId
     * @param customerId
     */
    @Transactional
    public void savedToTrash(String inquiryId, String customerId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            if (entity.getTrash() == InquiryEnum.NY) {
                entity.setTrash(InquiryEnum.YY);
            } else if (entity.getTrash() == InquiryEnum.YN) {
                entity.setTrash(InquiryEnum.YY);
            } else {
                if (customerId.equals(entity.getReceiverId())) {
                    entity.setTrash(InquiryEnum.NY);
                } else {
                    entity.setTrash(InquiryEnum.YN);
                }
            }
        }
    }


    //============================= Spam Page ============================

    /**
     * customerId가 받은 인콰이어리들 중에 spam으로 처리한 인콰이어리 리스트로 반환하는 함수
     * @param customerId
     * @return
     */
    public List<InquiryDTO> getSpamInquiry(String customerId) {
        List<InquiryEntity> inquiryEntities = inquiryRepository.findNonBlockedAndNonSpamNonTrashInquiriesByReceiverId(customerId);

        List<InquiryDTO> dtos = new ArrayList<>();
        inquiryEntities.forEach((entity)->{
            dtos.add(InquiryDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId(), entity.getProductEntity().getProductId()));
        });

        return dtos;
    }

    

    /**
     * 입력받은 인콰이어리ID에 해당하는 인콰이어리의 receiver의 spam값을 Y->N으로 변경
     * @param inquiryId
     * @param customerId
     */
    @Transactional
    public void updateSpamNo(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            if (entity.getSpam() == InquiryEnum.YY) {
                entity.setSpam(InquiryEnum.YN);
            }else{ // NY->NN
                entity.setSpam(InquiryEnum.NN);
            }
        }
    }

    /**
     * 전달받은 customerId와 senderId를 inquiryBlock DB에 저장하는 함수
     * @param customerId
     * @param senderId
     */
    @Transactional
    public void senderToBlock(String customerId, String senderId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId).get();
        
        // senderId에 해당하는 고객 정보가 존재하는 경우만 인콰이어리 신고회원 DB에 저장
        if (customerRepository.existsById(senderId)) {
            InquiryBlockDTO inquiryBlockDTO = new InquiryBlockDTO(null, customerId, senderId);
            inquiryBlockRepository.save(InquiryBlockEntity.toEntity(inquiryBlockDTO, customerEntity));
        }
    }

    // =============================== Trash Page ===========================

    /**
     * trash 값이 Y인 인콰이어리들을 Trash Page에 필요한 정보를 담은 새로운 DTO 리스트로 반환하는 함수 
     * @param customerId
     * @return
     */
    public List<InquiryPlusCustomerIdDTO> getTrashInquiry(String customerId) {
        // 두 쿼리 결과를 각각 조회
        List<InquiryEntity> inquiriesByReceiver = inquiryRepository.findInquiriesByCustomerAndNotBlocked(customerId);
        List<InquiryEntity> inquiriesBySender = inquiryRepository.findInquiriesBySenderAndFilteredStatus(customerId);

        // 두 리스트를 스트림으로 합치고 sendDate로 내림차순 정렬
        List<InquiryEntity> resultEntities = Stream.concat(inquiriesByReceiver.stream(), inquiriesBySender.stream())
                .sorted((i1, i2) -> i2.getSendDate().compareTo(i1.getSendDate()))
                .collect(Collectors.toList());

        List<InquiryPlusCustomerIdDTO> dtos = new ArrayList<>();

        resultEntities.forEach((entity) -> {
            dtos.add(new InquiryPlusCustomerIdDTO(entity.getInquiryId(), entity.getCustomerEntity().getCustomerId(),
                    entity.getReceiverId(), entity.getInquiryTitle(), entity.getSendDate(), entity.getSaved(),
                    entity.getTrash(), entity.getSpam(), entity.getDeleted(), customerId));
        });

        return dtos;

    }


    /**
     * trash함에 있는 인콰이어리를 trash에서 제거하고 초기값으로 돌리는 함수 (받은 메일함으로)
     * @param inquiryId
     */
    @Transactional
    public void updateTrashNo(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            // trash 값 변경
            if (entity.getTrash()== InquiryEnum.YY) {
                entity.setTrash(InquiryEnum.YN);
            }else{
                entity.setTrash(InquiryEnum.NN);
            }
            // saved 초기화
            if (entity.getSaved()== InquiryEnum.YY) {
                entity.setSaved(InquiryEnum.YN);
            }else{
                entity.setSaved(InquiryEnum.NN);
            }
            // spam 초기화
            if (entity.getSpam()== InquiryEnum.YY) {
                entity.setSpam(InquiryEnum.YN);
            }else{
                entity.setSpam(InquiryEnum.NN);
            }       
        }
    }


    /**
     * 전달받은 인콰이어리에 해당하는 deleted값을 N->Y로 변경하는 함수
     * @param inquiryId
     */
    @Transactional
    public void trashToDeleted(String inquiryId) {
        Long id = Long.parseLong(inquiryId);
        Optional<InquiryEntity> inquiryEntity = inquiryRepository.findById(id);
        if (inquiryEntity.isPresent()) {
            InquiryEntity entity = inquiryEntity.get();
            if (entity.getDeleted() == InquiryEnum.NN) {
                entity.setDeleted(InquiryEnum.NY);
            } else {
                entity.setDeleted(InquiryEnum.YY);
            }
        }
    }



    //=============================Block Page ===========================
    
    /**
     * 전달받은 customerId와 InquiryBlock 테이블의 customerId가 일치한 데이터들 중에
     * 신고받은 blockedId의 정보를 담은 새로운 DTO 리스트 반환하는 함수
     * @param customerId
     * @return
     */
    public List<InquiryBlockedCustomerDTO> getBlockedCustomerDTO(String customerId) {
        List<InquiryBlockEntity> blockEntities = inquiryBlockRepository.findByCustomerEntity_CustomerId(customerId);

        List<InquiryBlockedCustomerDTO> resultDTOs = new ArrayList<>();

        blockEntities.forEach((entity)->{
            CustomerEntity blockedCustomer = customerRepository.findById(entity.getBlockedId()).get();
            resultDTOs.add(new InquiryBlockedCustomerDTO(entity.getInquiryBlockId(), customerId, entity.getBlockedId(), 
                                                        blockedCustomer.getCustomerGubun(), blockedCustomer.getCompName(), 
                                                        blockedCustomer.getRemoteIp(), blockedCustomer.getCountry()));
        });

        return resultDTOs;
    }

    /**
     * 인콰이어리신고 테이블에서 전달받은 인콰이어리 신고 아이디에 해당하는 데이터 삭제하는 함수
     * @param inquiryBlockId
     */
    public void deleteBlocked(String inquiryBlockId) {
        Long id = Long.parseLong(inquiryBlockId);
        inquiryBlockRepository.deleteById(id);
    }


    /**
     * 신고회원 테이블에 정보 저장하고 신고받은 회원의 신고횟수 +1
     */
    @Transactional
    public Boolean insertReportCustomer(ReportCustomerDTO reportCustomerDTO) {

        Optional<CustomerEntity> customerEntity = customerRepository.findById(reportCustomerDTO.getReportedId());
        if (!customerEntity.isPresent()) {
            return false;
        }
        CustomerEntity reportedCustomer = customerEntity.get();
        
        reportCustomerRepository.save(ReportCustomerEntity.toEntity(reportCustomerDTO, reportedCustomer));
        
        // 신고횟수 +1
        reportedCustomer.setReportedCnt(reportedCustomer.getReportedCnt()+1);
        
        return true;
    }
    
    

}
