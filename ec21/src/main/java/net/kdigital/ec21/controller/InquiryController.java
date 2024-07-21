package net.kdigital.ec21.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.kdigital.ec21.dto.InquiryBlockedCustomerDTO;
import net.kdigital.ec21.dto.InquiryDTO;
import net.kdigital.ec21.dto.InquiryModalDTO;
import net.kdigital.ec21.dto.InquiryPlusCustomerIdDTO;
import net.kdigital.ec21.dto.ReportCustomerDTO;
import net.kdigital.ec21.dto.check.ReportCategory;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.service.CustomerService;
import net.kdigital.ec21.service.InquiryService;
import net.kdigital.ec21.service.ProductService;


@Controller
@Slf4j
@RequiredArgsConstructor
public class InquiryController {
    private final CustomerService customerService;
    private final ProductService productService;
    private final InquiryService inquiryService;

    //============================ Received Page ============================
    /**
     * inbox 페이지 요청 -> Received 인콰이어리 리스트 화면
     * receiverId == customerId 조건에 해당하는 인콰이어리들을 model에 담아 화면 요청
     * @return
     */
    @GetMapping("/main/inbox")
    public String inbox(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            Model model) {
        List<InquiryDTO> dtos = inquiryService.getReceivedInquiry(customerId);
        model.addAttribute("customerId", customerId);
        model.addAttribute("inquiryList", dtos);
        return "main/inbox";
    }

    /**
     * receiver 의 인콰이어리 저장 or 저장해제 요청
     * @param inquiryId
     * @param how
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/receiver/updateSaved")
    public Boolean updateReceiverSaved(@RequestParam(name = "inquiryId") String inquiryId,
            @RequestParam(name = "how") String how) {
        if (how.equals("savedNo")) {
            return inquiryService.receiverSavedNo(inquiryId);
        }
        return inquiryService.receiverSavedYes(inquiryId);
    }


    /**
     * receiver의 인콰이어리 스팸처리 요청
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/receiver/toSpam")
    public Boolean toSpam(@RequestParam(name = "inquiryId") String inquiryId) {
        return inquiryService.receiverToSpam(inquiryId);
    }
    
    /**
     * reciever의 인콰이어리 trash처리 요청
     * @param inquiryId
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/receiver/toTrash")
    public Boolean toTrash(@RequestParam(name = "inquiryId") String inquiryId) {
        return inquiryService.receiverToTrash(inquiryId);
    }
    
    //============= 인콰이어리 모달창 =================

    /**
     * inquiry모달창에 필요한 데이터를 새로운 DTO에 담아 모달창에 데이터를 꽂기 위한 요청
     * @param inquiryId
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/getInquiryModalDTO", method = RequestMethod.GET)
    @ResponseBody
    public InquiryModalDTO getInquiryModalDTO(@RequestParam(name = "inquiryId") String inquiryId,
    @RequestParam(name="customerId", defaultValue="")String customerId) {
        // 받은 인콰이어리의 경우 읽음 표시 
        inquiryService.updateCheckedYes(inquiryId, customerId);
        // 모달창에 들어갈 정보 가져오기
        InquiryModalDTO dto = inquiryService.getInquiryModalDTO(inquiryId);
        return dto;
    }

    @Value("${spring.servlet.multipart.location}")
    String uploadPath;
    /**
     * inquiryId 해당하는 inquiry의 업로드 파일 다운로드 요청
     * 
     * @param productId
     * 
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/downloadFile")
    public String download(@RequestParam(name = "inquiryId") String inquiryId, HttpServletResponse response) {
        log.info("=============지금 다운로드 컨트롤러야 인콰이어리 번호 : {}",inquiryId);
        InquiryDTO inquiryDTO = inquiryService.getInquiry(inquiryId);

        String originalFileName = inquiryDTO.getOriginalFileName();
        String savedFileName = inquiryDTO.getSavedFileName();

        try {
            String tempName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString()); // 파일명에 한글이 섞여있을
                                                                                                      // 때를 대비하기 위함
            response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
            // 위 코드가 없을 경우 브라우저가 실행 가능한 파일(이미지 파일이 대표적인 예임)인 경우 브라우저 자체에서 오픈함
            // 즉, 위 코드는 브라우저 자체에서 실행되도록 하지 않고 다운로드 받게 하기 위한 코드임
        } catch (UnsupportedEncodingException e) { // encoding 하지 못할 경우 에러 처리
            e.printStackTrace();
        }

        String fullPath = uploadPath + "/" + savedFileName;

        // 스트림 설정 (실제 다운로드)
        FileInputStream filein = null;
        ServletOutputStream fileout = null; // 원격지의 장소에 데이터를 쏠 떄

        // local에 있는 파일을 메모리로 끌어와야 함
        try {
            filein = new FileInputStream(fullPath); // 하드디스크->메모리에 올림 (서버입장에서의 로컬이기 떄문에 input 작업임)
            fileout = response.getOutputStream(); // 웹에서 원격지의 데이터를 쏴주는 것. 로컬에서 벗어난 다른 쪽으로 데이터를 쏘는 역할

            FileCopyUtils.copy(filein, fileout); // copy(원본, 내보낼 객체) : 원본을 읽어서 내보냄

            fileout.close(); // 연 순서의 반대로 닫아야 함
            filein.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    


    //========================= Sent Page ============================
    /**
     *  보낸 인콰이어리
     * @param customerId
     * @param model
     * @return
     */ 
    @GetMapping("/main/inboxSent")
    public String inboxSent(
        @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
        Model model ){
            
        List<InquiryDTO> dtos = inquiryService.getSentInquiry(customerId);
        model.addAttribute("customerId", customerId);
        model.addAttribute("inquiryList", dtos);
        
        return "main/inboxSent";
    }

    /**
     * 보낸 메일함에서 saved 처리 요청
     * @param inquiryId
     * @param how
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/sender/updateSaved")
    public Boolean updateSenderSaved(@RequestParam(name = "inquiryId") String inquiryId,
            @RequestParam(name = "how") String how) {
        if (how.equals("savedNo")) {
            return inquiryService.senderSavedNo(inquiryId);
        }
        return inquiryService.senderSavedYes(inquiryId);
    }

    /**
     * 
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/sender/toTrash")
    public Boolean sender(@RequestParam(name = "inquiryId") String inquiryId) {
        return inquiryService.senderToTrash(inquiryId);
    }
    

    //========================= Saved Page ============================

    /**
     * 
     * @param customerId
     * @param model
     * @return
     */
    @GetMapping("/main/inboxSaved")
    public String inboxSaved(
        @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            Model model ){

        // List<InquiryDTO> dtos = inquiryService.getSavedInquiry(customerId);
        // model.addAttribute("inquiryList", dtos);

        model.addAttribute("customerId", customerId);
        
        return "main/inboxSaved";
    }

    /**
     * customerId에 해당하는 회원이 saved한 인콰이어리 리스트들을 새로운 인콰이어리 DTO 리스트로 반환
     * @param customerId
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/saved/getList", method=RequestMethod.GET)
    public String getSavedList(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId, 
                                    @RequestParam(name = "inquiryId", defaultValue = "no") String inquiryId,
                                    @RequestParam(name = "change", defaultValue = "no") String change,
                                    Model model) {
        // 인콰이어리 아이디가 넘어온 경우, 저장/스팸/휴지통 중에 선택해 (N->Y)
        if(!inquiryId.equals("no")){
            if (change.equals("saved")) {
                inquiryService.updateSavedNo(inquiryId, customerId);
            }else{
                inquiryService.savedToTrash(inquiryId,customerId);
            }
        }
        // List<InquiryDTO> dtos = inquiryService.getSavedInquiry(customerId);
        List<InquiryPlusCustomerIdDTO> dtos = inquiryService.getSavedInquiryPlusCustomerId(customerId);
        model.addAttribute("inquiryList", dtos);

        return "/main/inboxSaved::#result";
    }
    
    
    // ========================= Spam ============================
    
    /**
     * 스팸 인콰이어리 화면 요청
     * @param customerId
     * @param model
     * @return
     */
    @GetMapping("/main/inboxSpam")
    public String inboxSpam(
        @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
        Model model ){
            
            model.addAttribute("customerId", customerId);
            
            return "main/inboxSpam";
    }

    /**
     * customerId에 해당하는 회원이 spam처리한 인콰이어리 리스트 반환
     * @param customerId
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/spam/getList", method=RequestMethod.GET)
    public String getSpamList(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId, 
                                    @RequestParam(name = "inquiryId", defaultValue = "no") String inquiryId,
                                    @RequestParam(name = "change", defaultValue = "no") String change,
                                    @RequestParam(name = "senderId", defaultValue = "") String senderId,
                                    Model model) {
        // 인콰이어리 아이디가 넘어온 경우, 스팸 해제(Y->N)/블락(회원블락)/휴지통(Y->N)
        if(!inquiryId.equals("no")){
            if (change.equals("unspam")) {
                inquiryService.updateSpamNo(inquiryId);
            }else if(change.equals("block")){
                inquiryService.senderToBlock(customerId,senderId);
            }else{
                inquiryService.savedToTrash(inquiryId,customerId);
            }
        }
        List<InquiryDTO> dtos = inquiryService.getSpamInquiry(customerId);
        model.addAttribute("inquiryList", dtos);
        
        return "/main/inboxSpam::#result";
    }



    // ========================= Block ============================
    
    /**
     * 차단한 회원
     * @param customerId
     * @param model
     * @return
     */
    @GetMapping("/main/inboxBlock")
    public String inboxBlock(
        @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            Model model ){

            model.addAttribute("customerId", customerId);        

        return "main/inboxBlock";
    }

    /**
     * customerId에 해당하는 회원이 Block처리한 회원의 정보를 담은 새로운 DTO 리스트 반환
     * 
     * @param customerId
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/block/getList", method = RequestMethod.GET)
    public String getBlockList(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            @RequestParam(name = "inquiryBlockId", defaultValue = "no") String inquiryBlockId,
            Model model) {
        // 인콰이어리 차단 아이디가 넘어온 경우, 해당 블락 데이터 삭제
        if (!inquiryBlockId.equals("no")) {
            inquiryService.deleteBlocked(inquiryBlockId);
        }

        List<InquiryBlockedCustomerDTO> dtos = inquiryService.getBlockedCustomerDTO(customerId);

        model.addAttribute("blockList", dtos);

        return "/main/inboxBlock::#result";
    }

    /**
     * 신고회원테이블에 전달받은 내용 저장
     * @param reportCustomerDTO
     * @return
     */
    @ResponseBody
    @GetMapping("/inbox/report")
    public Boolean insertReportCustomer(@RequestParam(name="reportedId")String reportedId,
                @RequestParam(name = "reportCategory")String reportCategory, 
                @RequestParam(name = "reportReason")String reportReason,
                @RequestParam(name = "managerCheck")String managerCheck) {
                ReportCustomerDTO dto = new ReportCustomerDTO(null, reportedId,ReportCategory.valueOf(reportCategory),reportReason,null,YesOrNo.valueOf(managerCheck));
        return inquiryService.insertReportCustomer(dto);
    }
    




    // ========================= Trash ============================
    
    /**
     * 쓰레기통 화면 요청
     * @param customerId
     * @param model
     * @return
     */
    @GetMapping("/main/inboxTrash")
    public String inboxTrash(
        @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            Model model ){

                model.addAttribute("customerId", customerId);
        
        return "main/inboxTrash";
    }


    /**
     * customerId에 해당하는 회원이 trash 처리한 인콰이어리 리스트 반환
     * 
     * @param customerId
     * @param model
     * @return
     */
    @RequestMapping(value = "/inbox/trash/getList", method = RequestMethod.GET)
    public String getTrashList(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            @RequestParam(name = "inquiryId", defaultValue = "no") String inquiryId,
            @RequestParam(name = "change", defaultValue = "no") String change,
            Model model) {
        // 인콰이어리 아이디가 넘어온 경우, trash 해제(Y->N)/deleted(N->Y)
        if (!inquiryId.equals("no")) {
            if (change.equals("untrash")) {
                inquiryService.updateTrashNo(inquiryId); 
            }else {
                inquiryService.trashToDeleted(inquiryId);
            }
        }
        List<InquiryPlusCustomerIdDTO> dtos = inquiryService.getTrashInquiry(customerId);
        model.addAttribute("inquiryList", dtos);

        return "/main/inboxTrash::#result";
    }
}
