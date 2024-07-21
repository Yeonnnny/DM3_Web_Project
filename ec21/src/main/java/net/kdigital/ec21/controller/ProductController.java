package net.kdigital.ec21.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.InquiryDTO;
import net.kdigital.ec21.dto.ProductDTO;
import net.kdigital.ec21.service.CustomerService;
import net.kdigital.ec21.service.InquiryService;
import net.kdigital.ec21.service.ProductService;



@Controller
@Slf4j
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CustomerService customerService;
    private final InquiryService inquiryService;

    // ====================================== 상품 등록 ========================================
    /**
     * main/myproducts에서 상품 등록 페이지 productsWrite 요청 (회원ID를 받아서 model에 담아 보냄)
     * @return
     */
    @GetMapping("main/productsWrite")
    public String productsWrite(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId, Model model) {
        model.addAttribute("customerId", customerId);
        return "main/productsWrite";
    }

    /**
     * 상품 등록 페이지에서 전달된 ProductDTO를 받아 DB에 저장 후 마이페이지 상품 리스트 화면 요청
     * @param entity
     * @return
     */
    @PostMapping("main/productsInsert")
    public String productsInsert(@ModelAttribute ProductDTO productDTO, RedirectAttributes attr) {
        // python Server -> DB 저장까지
        productService.insertProduct(productDTO);

        // 회원ID가 판매하는 상품 목록
        List<ProductDTO> productList = productService.getCustomerProducts(productDTO.getCustomerId());

        attr.addFlashAttribute("customerId", productDTO.getCustomerId());
        attr.addFlashAttribute("productList", productList);
        
        return "redirect:/main/myproducts";
    }    


    // =============================== 상품 상세 페이지 ===============================
    // 상품 등록한 사람 : edit버튼 (GET:/main/productsUpdate), delete버튼 (GET:/main/productsDelete)
    /**
     * 전달받은 상품 아이디에 해당하는 상품DTO와 해당 상품과 동일한 카테고리에 속한 상품들 최대 5개를
     * model에 담아 상품 상세 정보 페이지로 보냄
     * 
     * @param productId
     * @param category
     * @param searchWord
     * @param currentPage : main/myproducts or main/list 둘 중 하나임(productsDetail을 요청할 수 있는 페이지)
     * @param model
     * @return
     */
    @GetMapping("main/productsDetail")
    public String productsDetail(@RequestParam(name = "productId", defaultValue = "CO00006-20240409") String productId,
                                @RequestParam(name = "category", defaultValue = "total") String category,
                                @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                                @RequestParam(name = "currentPage", defaultValue = "myproducts")String currentPage, Model model) {
        
        // productId에 해당하는 Product의 hitCount 증가
        productService.updateHitCount(productId);
        
        // productId에 해당하는 Product 가져오기
        ProductDTO dto = productService.getProduct(productId);
        CustomerDTO customerDTO = customerService.getCustomer(dto.getCustomerId());
        
        // productId에 해당하는 Product와 동일한 카테고리의 상품들 최대 5개 가져오기
        List<ProductDTO> dtoList = productService.getSameCategoryProducts(dto.getCategory(), productId);
        

        model.addAttribute("product", dto);
        model.addAttribute("list", dtoList);
        model.addAttribute("category", category);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("customer",customerDTO);

        
        return "main/productsDetail";
    }

    /**
     * 상세 페이지에서 인콰이어리 모달창에서 전송된 정보 받아서 DB에 저장
     */
    @PostMapping("/productDetail/sendInquiry")
    public String getMethodName(@ModelAttribute InquiryDTO inquiryDTO, RedirectAttributes attr) {
        log.info("인콰이어리 폼 받았어");
        log.info(inquiryDTO.getReceiverId());

        inquiryService.insertinquiry(inquiryDTO);

        attr.addAttribute("productId", inquiryDTO.getProductId());
        return "redirect:/main/productsDetail";
    }
    



    
    // ====================================== 상품 수정 ========================================

    /**
     * 상품 수정 페이지 요청 (회원ID에 해당하는 회원DTO를 받아서 model에 담아 보냄)
     *  
     * @return
     */
    @GetMapping("main/productsUpdate")
    public String productsUpdate(@RequestParam(name = "productId", defaultValue = "CO00006-20240409") String productId,
            Model model) {
        // productId에 해당하는 Product 가져오기
        ProductDTO dto = productService.getProduct(productId);
        model.addAttribute("product", dto);
        // return "main/productsWrite";
        return "main/productsUpdate";
    }
    /**
     * 상품 수정 처리 요청 (회원ID에 해당하는 회원DTO를 받아서 model에 담아 보냄)
     * 
     * @return
     */
    @PostMapping("main/productsUpdateProc")
    public String productsUpdate(@ModelAttribute ProductDTO productDTO, Model model) {
        // 수정될 값이 담긴 DTO 받아서 해당 productId의 Product의 값 다시 세팅
        // ProductDTO dto = productService.updateProduct(productDTO);
        ProductDTO dto = productService.updateProduct_judge(productDTO); // 모델ver

        // productId에 해당하는 Product와 동일한 카테고리의 상품들 최대 5개 가져오기
        List<ProductDTO> dtoList = productService.getSameCategoryProducts(dto.getCategory(), dto.getProductId());

        // product를 판매하는 customer 정보
        CustomerDTO customerDTO = customerService.getCustomer(productDTO.getCustomerId());

        model.addAttribute("product", dto);
        model.addAttribute("list", dtoList);
        model.addAttribute("customer", customerDTO);
        
        return "main/productsDetail";
    }
    
    // ====================================== 상품 삭제 ========================================

    /**
     * ajax : 상품 상세 페이지에서 상품 삭제(삭제하고자 하는 productId 받아서 해당 product의 productDelete 값 Y로 변경) 요청 후 
     * boolean 값 반환
     * 
     * @return
     */
    @GetMapping("main/productsDelete")
    @ResponseBody
    public Boolean productsDelete(@RequestParam(name = "productId", defaultValue = "CO00006-20240409") String productId) {
        // 상품 삭제 여부 값 변경 (N->Y)
        return productService.updateDeleteCheck(productId);
    }
    
    /**
     * 상세 페이지에서 상품 삭제 후 myproducts(mypage) 페이지 요청
     * @param customerId
     * @param category
     * @param searchWord
     * @param attr
     * @return
     */
    @GetMapping("/main/myproducts/afterDelete")
    public String myProductsAfterDelete(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            @RequestParam(name = "category", defaultValue = "total") String category,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes attr) {

        // 회원ID에 해당하는 회원이 판매하고 있는 상품 리스트
        List<ProductDTO> productList = productService.getCustomerProducts(customerId);
        attr.addFlashAttribute("productList", productList);

        attr.addFlashAttribute("customerId", customerId);
        attr.addFlashAttribute("category", category);
        attr.addFlashAttribute("searchWord", searchWord);

        return "redirect:/main/myproducts";
    }

    /**
     * 상세 페이지에서 상품 삭제 후 상품리스트(list) 페이지 요청
     * @param category
     * @param searchWord
     * @param attr
     * @return
     */
    @GetMapping("main/list/afterDelete")
    public String listAfterDelete(@RequestParam(name = "category", defaultValue = "total") String category,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, RedirectAttributes attr) {
        log.info("=========== 카테고리 : {}",category);
        log.info("=========== 검색어 : {}",searchWord);
        List<ProductDTO> dtoList = productService.getProductList(category, searchWord);

        attr.addFlashAttribute("list", dtoList);
        attr.addFlashAttribute("category", category);
        attr.addFlashAttribute("searchWord", searchWord);
        
        return "redirect:/main/list";
    }

    //================================= 메인화면 상품 목록(카테고리별) ===================================
    /**
     * 전달받은 상품 카테고리와 검색어에 해당하는 상품 리스트를 model에 담아 상품 목록 페이지로 보냄
     * 
     * @param category
     * @param model
     * @return 
     */
    @GetMapping("main/list")
    public String list(@RequestParam(name = "category", defaultValue = "total") String category,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {
        log.info("=========== 카테고리 : {}",category);
        log.info("=========== 검색어 : {}",searchWord);
        // Flash Attribute에서 상품 리스트 가져오기
        if (!model.containsAttribute("productList")) {
            // Flash Attribute가 없는 경우, 기본 로직으로 상품 리스트를 가져옴
            List<ProductDTO> productList = productService.getProductList(category, searchWord);
            model.addAttribute("list", productList);
        }
        model.addAttribute("category", category);
        model.addAttribute("searchWord", searchWord);
        
        return "main/list";
    }

    
    
    //========================= 첨부파일 다운로드 ======================

    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    /**
     * productId에 해당하는 Product의 업로드 이미지 다운로드 요청
     * @param productId
     * @return
     */
    @GetMapping("main/downloadImg")
    public String download(@RequestParam(name = "productId") String productId, HttpServletResponse response) {

        ProductDTO productDTO = productService.getProduct(productId);

        String originalFileName = productDTO.getOriginalFileName();
        String savedFileName = productDTO.getSavedFileName();

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

}
