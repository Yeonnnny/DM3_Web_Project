package net.kdigital.ec21.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.BlacklistDTO;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.CustomerListModalDTO;
import net.kdigital.ec21.dto.ModelPredictDTO;
import net.kdigital.ec21.dto.ModelPredictModalDTO;
import net.kdigital.ec21.dto.ProductDTO;
import net.kdigital.ec21.dto.ReportedCustomerWithInfoDTO;
import net.kdigital.ec21.dto.check.ProductCategory;
import net.kdigital.ec21.dto.check.ProhibitReason;
import net.kdigital.ec21.dto.check.ReportCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.entity.BlacklistEntity;
import net.kdigital.ec21.entity.CustomerEntity;
import net.kdigital.ec21.entity.ProductEntity;
import net.kdigital.ec21.entity.ProhibitSimilarWordEntity;
import net.kdigital.ec21.entity.ProhibitWordEntity;
import net.kdigital.ec21.entity.ReportCustomerEntity;
import net.kdigital.ec21.repository.BlacklistRepository;
import net.kdigital.ec21.repository.CustomerRepository;
import net.kdigital.ec21.repository.ProductRepository;
import net.kdigital.ec21.repository.ProhibitSimilarWordRepository;
import net.kdigital.ec21.repository.ProhibitWordRepository;
import net.kdigital.ec21.repository.ReportCustomerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerService {
    // ======================= Repository =======================
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ReportCustomerRepository reportCustomerRepository;
    private final BlacklistRepository blacklistRepository;
    private final ProhibitSimilarWordRepository prohibitSimilarWordRepository;
    private final ProhibitWordRepository prohibitWordRepository;

    // ========================= 메인 보드 =========================

    /**
     * 당일 등록된 상품 개수, 당일 이상상품 개수, 당일 등록한 고객 수, 미처리된 신고 개수 반환하는 함수
     * 
     * @return
     */
    public Map<String, Long> getCount() {
        Map<String, Long> result = new HashMap<>();

        // 시스템상 당일 정보
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();

        // 당일 등록 상품 개수
        Long todayRegisteredProd = productRepository.countByCreateDateBetween(todayStart, todayEnd);
        result.put("todayRegisteredProd", todayRegisteredProd);

        // 당일 등록된 이상 상품 개수(당일 수정된 상품 중 이상으로 판별된 상품 수도 포함됨)
        Long todayWeirdProd = productRepository.countByDateAndLstmPredictFalse(todayStart, todayEnd);
        result.put("todayWeirdProd", todayWeirdProd);

        // 당일 등록한 회원 수
        Long todayRegisteredCustomer = customerRepository.countByCreateDateBetween(todayStart, todayEnd);
        result.put("todayRegisteredCustomer", todayRegisteredCustomer);

        // 미처리된 신고 개수
        Long unprocessedReport = reportCustomerRepository.countByManagerCheck(YesOrNo.N);
        result.put("unprocessedReport", unprocessedReport);

        return result;
    }

    // ========================= 상품관리 ===========================

    /**
     * 모든 상품 DTO 리스트로 반환하는 함수
     * 
     * @return
     */
    public List<ProductDTO> selectAll() {
        List<ProductEntity> entityList = productRepository.findAll(Sort.by(Direction.DESC, "createDate"));
        List<ProductDTO> dtoList = new ArrayList<>();
        entityList.forEach((entity) -> {
            dtoList.add(ProductDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId()));
        });
        return dtoList;
    }

    /**
     * 전달받은 카테고리와 검색어에 해당하는 상품DTO 리스트 반환 (디폴트 : total/"")
     * 
     * @param category
     * @param searchWord
     * @return
     */
    public List<ProductDTO> selectProductBySearch(String category, String searchWord) {
        List<ProductEntity> productEntities = new ArrayList<>();
        List<ProductDTO> dtoList = new ArrayList<>();

        if (category.equals("total")) {
            // 회원ID, 상품ID, 상품명에 검색어가 포함된 상품들을 최신 등록일 순으로 가져오기
            productEntities = productRepository.findByMultipleFieldsContaining(searchWord.toLowerCase(),
                    Sort.by(Direction.DESC, "createDate"));
        } else {
            // 카테고리 타입 변경 : String -> Enum
            ProductCategory targetCategory = ProductCategory.valueOf(category);
            // 전달받은 카테고리에 해당하고 회원ID, 상품ID, 상품명에 검색어가 포함된 상품들을 최신 등록일 순으로 가져오기
            productEntities = productRepository.findByCategoryAndMultipleFieldsContaining(targetCategory,
                    searchWord.toLowerCase(), Sort.by(Direction.DESC, "createDate"));
        }

        productEntities.forEach((entity) -> {
            dtoList.add(ProductDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId()));
        });

        return dtoList;
    }

    /**
     * 미처리/ 정상/ 이상 까지 필터 넣기
     * @param filter
     * @param category
     * @param searchWord
     * @return
     */
    public List<ProductDTO> selectProductBySearchAndFilter(String filter, String category, String searchWord) {
        ProductCategory targeCategory ;
        List<ProductEntity> entityList = new ArrayList<>();
        List<ProductDTO> dtoList = new ArrayList<>();
        
        // 1) 미처리된 상품 (judge == null)
        if (filter.equals("unprocessedList")) {
            // 1-1) 카테고리 total 인 경우 : judge==null & searchword
            if (category.equals("total")) {
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsNull(searchWord, Sort.by(Direction.DESC, "createDate"));
            }// 1-2) 카테고리 존재하는 경우 : judge==null & category & searchWord
            else{
                targeCategory = ProductCategory.valueOf(category);
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsNullAndCategory(searchWord, 
                        targeCategory, Sort.by(Direction.DESC, "createDate"));
            }
        } // 2) 정상 상품 (judge == Y)
        else if(filter.equals("normalList")){
            // 2-1) 카테고리 total인 경우 : judge == Y & searchWord
            if (category.equals("total")) {
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsY(searchWord, Sort.by(Direction.DESC, "createDate"));
            }// 2-2) 카테고리 존재하는 경우 : judge == Y & category & searchWord
            else{
                targeCategory = ProductCategory.valueOf(category);
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsYAndCategory(searchWord,targeCategory, Sort.by(Direction.DESC, "createDate"));
            }
        } // 3) 이상 상품 (judge == N)
        else{
            // 3-1) 카테고리 total인 경우 : judge == N & searchWord
            if (category.equals("total")) {
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsN(searchWord,
                        Sort.by(Direction.DESC, "createDate"));
            } // 3-2) 카테고리 존재하는 경우 : judge == N & category & searchWord
            else {
                targeCategory = ProductCategory.valueOf(category);
                entityList = productRepository.findByMultipleFieldsContainingAndJudgeIsNAndCategory(searchWord,
                        targeCategory, Sort.by(Direction.DESC, "createDate"));
            }
        }

        entityList.forEach((entity) -> {
            dtoList.add(ProductDTO.toDTO(entity, entity.getCustomerEntity().getCustomerId()));
        });

        return dtoList;
    
    }

    /**
     * lstm 예측값이 0(false)이고 관리자가 미처리한 상품들 중
     * 전달받은 카테고리와 검색어에 해당하는 상품들을
     * 새로운 ModelPredictDTO에 담아 리스트로 반환하는 함수
     * 
     * @param category
     * @param searchWord
     * @return
     */
    public List<ModelPredictDTO> selectModelPredictWeirdBySearch(String category, String searchWord) {
        List<ProductEntity> productEntities = new ArrayList<>();
        List<ModelPredictDTO> result = new ArrayList<>();
        if (category.equals("total")) {
            // lstm==false && judge==null && 회원ID, 상품ID, 상품명에 검색어가 포함된 상품들을 최신 등록일 순으로 가져오기
            productEntities = productRepository
                    .findByMultipleFieldsContainingOrderByCreateDateDesc(searchWord.toLowerCase());
        } else {
            // 카테고리 타입 변경 : String -> Enum
            ProductCategory targetCategory = ProductCategory.valueOf(category);
            // 전달받은 카테고리에 해당하고 회원ID, 상품ID, 상품명에 검색어가 포함된 상품들을 최신 등록일 순으로 가져오기
            productEntities = productRepository.findByCategoryAndMultipleFieldsContainingOrderByCreateDateDesc(
                    targetCategory, searchWord.toLowerCase());
        }

        productEntities.forEach((prodEntity) -> {
            // 1) productId에 해당하는 금지어유사도 결과 데이터들 가져오기 (금지어 유사 확률 높은 순)
            List<ProhibitSimilarWordEntity> entityList = prohibitSimilarWordRepository
                    .findProbaByProductEntity_ProductIdOrderBySimilarProbaDesc(prodEntity.getProductId());

            // 2) lstmPredictProba 값 환산
            // Double newProba = 1 - (prodEntity.getLstmPredictProba()/0.79)*0.5; // 지금 저장된
            // 데이터들은 백분율로 저장되어있음..
            Double newProba = 1 - (((prodEntity.getLstmPredictProba() * 0.01) / 0.79) * 0.5);

            // 1) lstmPredict==false && 금지어 유사도 결과 존재 O
            if (entityList != null && !entityList.isEmpty()) {
                // 금지 유사확률이 가장 높은 데이터 가져오기
                ProhibitSimilarWordEntity prohibitSimilarWordEntity = entityList.get(0);

                // 화면 출력을 위한 새로운 ModelPredictDTO 생성
                ModelPredictDTO dto = new ModelPredictDTO(prodEntity.getCustomerEntity().getCustomerId(),
                        prodEntity.getProductId(),
                        prodEntity.getProductName(), prodEntity.getProductDesc(),
                        newProba, prodEntity.isLstmPredict(),
                        prohibitSimilarWordEntity.getSimilarWord(), prohibitSimilarWordEntity.getSimilarProba(),
                        prohibitSimilarWordEntity.getProhibitWordEntity().getProhibitWord(),
                        prohibitSimilarWordEntity.getProhibitWordEntity().getProhibitReason());
                result.add(dto);

            }
            // 2) lstmPredict==false && 금지어 유사도 결과 존재 X
            else {
                // 화면 출력을 위한 새로운 ModelPredictDTO 생성
                ModelPredictDTO dto = new ModelPredictDTO(prodEntity.getCustomerEntity().getCustomerId(),
                        prodEntity.getProductId(),
                        prodEntity.getProductName(), prodEntity.getProductDesc(),
                        newProba, prodEntity.isLstmPredict(),
                        null, 0.0, null, null);
                result.add(dto);
            }
        });

        return result;
    }

    /**
     * 상품 ID에 해당하는 금지어 유사도 결과 리스트의 결과를 새로운 modalDTO에 담아서 리스트로 반환하는 함수
     * (상품ID, 유사단어, 금지어 리스트 단어, 금지어 사유)
     * 
     * @param productId
     * @return
     */
    public List<ModelPredictModalDTO> getProhibitSimilarWordDTOs(String productId) {
        List<ModelPredictModalDTO> modalDTOs = new ArrayList<>();

        // productId에 해당하는 금지어유사도 결과 데이터들 가져오기 (금지어 유사단어의 알파벳 오름차순, 금지어 유사도 내림차순)
        List<ProhibitSimilarWordEntity> entityList = prohibitSimilarWordRepository
                .findByProductEntity_ProductIdOrderBySimilarWordAscSimilarProbaDesc(productId);
        if (entityList == null) {
            return null;
        }
        entityList.forEach((entity) -> {
            ModelPredictModalDTO dto = new ModelPredictModalDTO(productId, entity.getSimilarWord(),
                    entity.getProhibitWordEntity().getProhibitWord(),
                    entity.getProhibitWordEntity().getProhibitReason(),
                    entity.getSimilarProba());
            modalDTOs.add(dto);
        });
        return modalDTOs;
    }

    /**
     * 금지어 리스트 DB에 새로 전달받은 유사단어와 금지어 사유를 저장하는 함수
     * 
     * @param similarWord
     * @param prohibitReason
     * @return
     */
    public Boolean insertProhibitWord(String similarWord, String prohibitReason) {
        // 추가할 단어 소문자로 변환
        String lowerSimilarWord = similarWord.toLowerCase();

        // 금지어 테이블에 이미 단어가 존재하면 DB에 접근하지 못하고 false를 가지고 돌아감
        Optional<ProhibitWordEntity> prohibitWord = prohibitWordRepository.findById(lowerSimilarWord);
        if (prohibitWord.isPresent()) {
            return false;
        }
        // 금지어 테이블에 추가 작업
        ProhibitReason reason = ProhibitReason.valueOf(prohibitReason);
        ProhibitWordEntity entity = new ProhibitWordEntity(lowerSimilarWord, reason, new ArrayList<>());
        prohibitWordRepository.save(entity);
        return true;
    }

    /**
     * 상품 ID를 전달받아 해당 상픔의 judge 값을 Y로 변경해주는 함수
     * 
     * @param productId
     * @return
     */
    @Transactional
    public Boolean updateProductJudgeNormal(String productId) {
        ProductEntity entity = productRepository.findById(productId).get();
        entity.setJudge(YesOrNo.Y);
        return true;
    }

    /**
     * 상품 ID를 전달받아 해당 상픔의 judge 값을 N으로 변경해주는 함수
     * 
     * @param productId
     * @return
     */
    @Transactional
    public Boolean updateProductJudgeWeird(String productId) {
        ProductEntity entity = productRepository.findById(productId).get();
        entity.setJudge(YesOrNo.N);
        return true;
    }

    // ======================== 회원관리 ========================

    /**
     * 정상회원(blacklist_check==N)인 CustomerDTO 최신 가입 순으로 반환
     */
    public List<CustomerDTO> selectNotBlacklist() {
        List<CustomerEntity> entityList = customerRepository.findByBlacklistCheckOrderByCreateDateDesc(YesOrNo.N);
        List<CustomerDTO> dtoList = new ArrayList<>();
        entityList.forEach((entity) -> {
            dtoList.add(CustomerDTO.toDTO(entity));
        });

        return dtoList;
    }

    /**
     * 정상회원 중 검색 카테고리, 검색어에 의한 결과 리스트를 최신 가입 순으로 반환
     * @param category
     * @param searchWord
     * @return
     */
    public List<CustomerDTO> selectNotBlacklistByCategoryAndSearch(String category, String searchWord) {

        List<CustomerEntity> entities = new ArrayList<>();
        List<CustomerDTO> result = new ArrayList<>();

        if (category.equals("total")) { // 전체
            entities = customerRepository.findBySearchTermAndNotBlacklisted(searchWord,YesOrNo.N);
        }else if(category.equals("customerId")){ // 회원ID
            entities = customerRepository.findByCustomerIdContainingAndBlacklistCheckOrderByCreateDateDesc(searchWord,YesOrNo.N);
        }else{ // 회사명
            entities = customerRepository.findByCompNameContainingAndBlacklistCheckOrderByCreateDateDesc(searchWord,YesOrNo.N);
        }
        
        entities.forEach((entity)->{
            result.add(CustomerDTO.toDTO(entity));
        });
        
        return result;
    }

    /**
     * customerId에 해당하는 회원이 판매하는 상품DTO 리스트를 반환하는 함수
     * 
     * @param customerId
     * @return
     */
    public List<CustomerListModalDTO> getCustomerProductDTOs(String customerId) {
        CustomerEntity entity = customerRepository.findById(customerId).get();
        List<ProductEntity> productEntities = entity.getProductEntity();
        // 판매하는 상품이 없는 경우
        if (productEntities == null) {
            return null;
        }
        // 판매하는 상품이 있는 경우
        List<CustomerListModalDTO> dtos = new ArrayList<>();
        productEntities.forEach((product) -> {
            CustomerListModalDTO dto = new CustomerListModalDTO(customerId, product.getProductId(),
                    product.getProductName(),
                    product.getProductDesc(), product.getOrigin(), product.getMoq(), product.getUnit(),
                    product.getPrice(),
                    product.getCategory(), product.isLstmPredict(), product.getJudge());
            dtos.add(dto);
        });
        return dtos;
    }

    /**
     * 전달받은 customerId의 blackCheck 값을 Y로 변경하고,
     * 해당 회원의 정보와 전달받은 블랙 사유 및 설명으로 블랙리스트 entity 생성 후 DB에 저장
     * 
     * @param customerId
     * @param blackReason
     * @param etcReason
     * @return
     */
    public Boolean insertToBlacklist(String customerId, String blackReason, String etcReason) {
        CustomerEntity customer = customerRepository.findById(customerId).get();
        // 1) 회원의 블랙 체크값 변경
        customer.setBlacklistCheck(YesOrNo.Y);
        // 2) 블랙 사유 타입 변경 : String->Enum
        ReportCategory blackCategory = ReportCategory.valueOf(blackReason);
        // 3) 블랙리스트 객체 생성
        BlacklistDTO blackDTO = new BlacklistDTO(customerId, customer.getCompName(),
                customer.getRemoteIp(), customer.getCountry(),
                blackCategory, etcReason);
        // 4) 블랙리스트 DB에 저장
        blacklistRepository.save(BlacklistEntity.toEntity(blackDTO));

        return true;
    }

    /**
     * 관리자가 처리하지 않은 신고당한 회원 DTO를 반환하고자 하는데 회원의 기본적인 정보를 같이 담기 위해 새로운 DTO에 담아 리스트를
     * 반환하는 함수
     * (신고당한 회원 ID, 신고회원테이블ID, 신고당한 회원의 신고당한 횟수, 구분, 이름, 회사명, 부서명, IP, 신고 카테고리, 신고
     * 사유)
     * 
     * @return
     */
    public List<ReportedCustomerWithInfoDTO> selectReportedCustomer() {
        // 관리자가 처리하지 않은 데이터(최신 신고날짜 순으로) 가져오기
        List<ReportCustomerEntity> reportCustomerEntities = reportCustomerRepository
                .findByManagerCheckOrderByReportDateDesc(YesOrNo.N);
        List<ReportedCustomerWithInfoDTO> result = new ArrayList<>();

        reportCustomerEntities.forEach((entity) -> {
            CustomerEntity customerEntity = entity.getCustomerEntity();
            result.add(new ReportedCustomerWithInfoDTO(customerEntity.getCustomerId(), entity.getReportCustomerId(),
                    customerEntity.getReportedCnt(), customerEntity.getCustomerGubun(),
                    customerEntity.getCustomerName(), customerEntity.getCompName(),
                    customerEntity.getCustomerDepartment(), customerEntity.getRemoteIp(),
                    entity.getReportCategory(), entity.getReportReason()));
        });

        return result;
    }

    /**
     * 검색 기능 추가 ver -> 전달받은 카테고리 및 검색어에 해당하는 데이터
     * 관리자가 처리하지 않은 신고당한 회원 DTO를 반환하고자 하는데 회원의 기본적인 정보를 같이 담기 위해 새로운 DTO에 담아 리스트를
     * 반환
     * (신고당한 회원 ID, 신고회원테이블ID, 신고당한 회원의 신고당한 횟수, 구분, 이름, 회사명, 부서명, IP, 신고 카테고리, 신고
     * 사유)
     * 
     * @param category
     * @param searchWord
     * @return
     */
    public List<ReportedCustomerWithInfoDTO> selectReportedCustomerBySearch(String category, String searchWord) {
        List<ReportCustomerEntity> reportCustomerEntities = new ArrayList<>();
        List<ReportedCustomerWithInfoDTO> result = new ArrayList<>();

        if (category.equals("total")) {
            // 블랙이 아니고, 관리자가 처리하지 않은 데이터 중에 전달받은 검색어에 해당하는 신고받은 회원 (최신 신고날짜 순으로) 가져오기
            reportCustomerEntities = reportCustomerRepository
                    .findBySearchWordWithManagerCheckNAndBlacklistCheckNOrderByReportDateDesc(searchWord);
        } else {
            // 블랙이 아니고, 관리자가 처리하지 않은 데이터 중에 전달받은 카테고리와 검색어에 해당하는 신고받은 회원 (최신 신고날짜 순으로) 가져오기
            ReportCategory targetCategory = ReportCategory.valueOf(category);
            reportCustomerEntities = reportCustomerRepository
                    .findByCategoryAndSearchWordWithManagerCheckNAndBlacklistCheckNOrderByReportDateDesc(targetCategory,
                            searchWord);
        }

        reportCustomerEntities.forEach((entity) -> {
            CustomerEntity customerEntity = entity.getCustomerEntity();
            result.add(new ReportedCustomerWithInfoDTO(customerEntity.getCustomerId(), entity.getReportCustomerId(),
                    customerEntity.getReportedCnt(), customerEntity.getCustomerGubun(),
                    customerEntity.getCustomerName(), customerEntity.getCompName(),
                    customerEntity.getCustomerDepartment(), customerEntity.getRemoteIp(),
                    entity.getReportCategory(), entity.getReportReason()));
        });

        return result;
    }

    /**
     * 신고회원 관리자 처리 여부 변경 (N->Y)
     */
    @Transactional
    public void reportCustomerUpdateManagerCheck(Long reportCutomerId) {
        ReportCustomerEntity entity = reportCustomerRepository.findById(reportCutomerId).get();
        entity.setManagerCheck(YesOrNo.Y);
    }

    /**
     * 신고회원을 블랙으로 처리하기 전 해당ID가 블랙리스트DB에 존재하는지 확인하는 함수
     * 
     * @param reportedId
     * @return
     */
    public Boolean checkBlack(String reportedId) {
        Optional<BlacklistEntity> black = blacklistRepository.findByCustomerId(reportedId);
        if (black.isPresent()) {
            return false;
        }
        return true;
    }

    /**
     * 신고 회원을 블랙회원으로 변경하는 함수 <br>
     * 신고회원 테이블 pk와 신고당한 회원 아이디를 입력받은 후,<br>
     * 1. 신고당한 아이디의 blacklist_check 값 변경 <br>
     * 2. 회원테이블의 회원id, compName, remoteIp, country, 신고회원 테이블의 신고카테고리, 신고 사유로 블랙리스트
     * 엔티티생성해서 테이블에 저장
     * 
     * @param reportCustomerId
     * @param reportedId
     */
    @Transactional
    public void reportedIdToBlackList(Long reportCutomerId, String reportedId) {
        // customer의 blacklistCheck값 Y로 변경
        CustomerEntity customerEntity = customerRepository.findById(reportedId).get();
        customerEntity.setBlacklistCheck(YesOrNo.Y);

        // 신고회원엔티티 가져오기 (신고사유,카테고리 가져오기 위함)
        ReportCustomerEntity reportCustomerEntity = reportCustomerRepository.findById(reportCutomerId).get();

        // 블랙리스트DTO 생성
        BlacklistDTO dto = new BlacklistDTO(reportedId, customerEntity.getCompName(), customerEntity.getRemoteIp(),
                customerEntity.getCountry(), reportCustomerEntity.getReportCategory(),
                reportCustomerEntity.getReportReason());

        // 블랙리스트 DB에 저장
        BlacklistEntity entity = blacklistRepository.save(BlacklistEntity.toEntity(dto));

    }

    /**
     * 블랙리스트 테이블에서 전달받은 ID에 해당하는 데이터 삭제 및 회원의 블랙리스트 확인 값 변경하는 함수
     * 
     * @param blacklistId
     */
    @Transactional
    public void deleteFromBalcklist(Long blacklistId) {
        BlacklistEntity blackEntity = blacklistRepository.findById(blacklistId).get();
        // 회원의 블랙리스트 확인 값 변경 (Y->N)
        CustomerEntity customerEntity = customerRepository.findById(blackEntity.getCustomerId()).get();
        customerEntity.setBlacklistCheck(YesOrNo.N);
        // 블랙리스트 DB에서 삭제
        blacklistRepository.deleteById(blacklistId);
    }

    /**
     * 블랙리스트 회원 DTO를 반환하고자 하는데 회원의 기본적인 정보를 같이 담기 위해 새로운 DTO에 담아 리스트를 반환하는 함수
     * (신고당한 회원 ID, 신고회원테이블ID, 신고당한 회원의 신고당한 횟수, 구분, 이름, 회사명, 부서명, IP, 신고 카테고리, 신고
     * 사유)
     * 
     * @return
     */
    public List<ReportedCustomerWithInfoDTO> selectblackList() {
        // 모든 블랙리스트 데이터 가져오기
        List<BlacklistEntity> blacklistEntityList = blacklistRepository.findAll();
        List<ReportedCustomerWithInfoDTO> result = new ArrayList<>();

        blacklistEntityList.forEach((entity) -> {
            // 회원 정보 가져오기 위한 CustomerEntity 가져오기
            CustomerEntity customerEntity = customerRepository.findById(entity.getCustomerId()).get();

            result.add(new ReportedCustomerWithInfoDTO(entity.getCustomerId(), entity.getBlacklistId(),
                    customerEntity.getReportedCnt(), customerEntity.getCustomerGubun(),
                    customerEntity.getCustomerName(), customerEntity.getCompName(),
                    customerEntity.getCustomerDepartment(), customerEntity.getRemoteIp(),
                    entity.getBlackType(), entity.getBlackReason()));
        });

        return result;
    }

    /**
     * 검색 기능 추가 ver -> 전달받은 카테고리 및 검색어에 해당하는 데이터
     * 블랙리스트 회원 DTO를 반환하고자 하는데 회원의 기본적인 정보를 같이 담기 위해 새로운 DTO에 담아 리스트를 반환
     * (신고당한 회원 ID, 신고회원테이블ID, 신고당한 회원의 신고당한 횟수, 구분, 이름, 회사명, 부서명, IP, 신고 카테고리, 신고
     * 사유)
     * 
     * @param category
     * @param searchWord
     * @return
     */
    public List<ReportedCustomerWithInfoDTO> selectblackListBySearch(String category, String searchWord) {
        List<BlacklistEntity> blacklistEntityList = new ArrayList<>();
        List<ReportedCustomerWithInfoDTO> result = new ArrayList<>();

        if (category.equals("total")) {
            // 블랙리스트 중 회원ID, 담당자명, 회사명에 검색어가 포함된 데이터를 inputDate 최신순으로 가져오기
            blacklistEntityList = blacklistRepository
                    .findByCustomerIdOrCompNameContainingAndOrderByInputDateDesc(searchWord.toLowerCase());
        } else {
            // 관리자가 처리하지 않은 데이터 중에 전달받은 카테고리와 검색어에 해당하는 신고받은 회원 (최신 신고날짜 순으로) 가져오기
            ReportCategory targetCategory = ReportCategory.valueOf(category);
            blacklistEntityList = blacklistRepository.findBySearchWordAndBlackTypeOrderByInputDateDesc(searchWord,
                    targetCategory);
        }

        blacklistEntityList.forEach((entity) -> {
            // 회원 정보 가져오기 위한 CustomerEntity 가져오기
            CustomerEntity customerEntity = customerRepository.findById(entity.getCustomerId()).get();

            result.add(new ReportedCustomerWithInfoDTO(entity.getCustomerId(), entity.getBlacklistId(),
                    customerEntity.getReportedCnt(), customerEntity.getCustomerGubun(),
                    customerEntity.getCustomerName(), customerEntity.getCompName(),
                    customerEntity.getCustomerDepartment(), customerEntity.getRemoteIp(),
                    entity.getBlackType(), entity.getBlackReason()));
        });

        return result;
    }

    



}
