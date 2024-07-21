package net.kdigital.ec21.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.ProductDTO;
import net.kdigital.ec21.dto.check.YesOrNo;
import net.kdigital.ec21.service.CustomerService;
import net.kdigital.ec21.service.ProductService;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final ProductService productService;

    // ================== 회원가입, 로그인, 로그아웃 ====================
    /**
     * register(회원가입) 페이지 요청
     * 
     * @return
     */
    @GetMapping("main/register")
    public String register(@RequestParam(name = "category", defaultValue = "total") String category,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {
        
        model.addAttribute("category", category);
        model.addAttribute("searchWord", searchWord);
        
        return "main/register";
    }

    /**
     * 전달받은 id가 DB에 존재하는지 확인
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("main/register/confirmId")
    public Boolean confirmId(@RequestParam(name = "customerId") String customerId) {
        return customerService.isExistId(customerId);
    }
    

    /**
     * 회원가입 처리 요청
     * 
     * @param customerDTO
     * @return
     */
    @PostMapping("main/registerProc")
    public String registerProc(@ModelAttribute CustomerDTO customerDTO) {
        // default 세팅
        customerDTO.setEnabled(YesOrNo.N);
        customerDTO.setBlacklistCheck(YesOrNo.N);
        customerDTO.setRoles("ROLE_USER");

        customerService.registerProc(customerDTO);
        return "redirect:/";
    }

    /**
     * login 페이지 요청
     * 
     * @return
     */
    @GetMapping("main/login")
    public String login(@RequestParam(value = "error", required =false) String error,
        @RequestParam(value = "errMessage", required = false) String errMessage, HttpServletRequest request, Model model) {

        model.addAttribute("error", error);
        model.addAttribute("errMessage", errMessage);

        // 이전 페이지로 되돌아가기 위한 Referer 헤더값을 세션의 prevPage attribute로 저장
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        
        return "main/login";
    }


    /**
     * 블랙회원이 로그인한 경우
     * @return
     */
    @GetMapping("blacklist")
    public String blacklist() {
        return "blacklist";
    }
    

    /**
     * 탈퇴한 회원이 로그인한 경우
     * @return
     */
    @GetMapping("withdrawalAccount")
    public String withdrawalAccount() {
        return "withdrawalAccount";
    }
    

    // 로그인 처리 & 로그아웃은 Security에서..!!!
    

    // ================= 회원 마이페이지 =================

    /**
     * myproducts(mypage) 페이지 요청
     * main 폴더에 있는 마이페이지 버튼 클릭 시 전달받은 회원ID가 판매하는 상품 목록 화면 요청
     * 
     * @return
     */
    @GetMapping("main/myproducts")
    public String myProducts(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            @RequestParam(name = "category", defaultValue = "total") String category,
            @RequestParam(name = "searchWord", defaultValue = "") String searchWord, Model model) {

        // Flash Attribute를 통해 전달받은 상품 목록이 없는 경우 
        if (!model.containsAttribute("productList")) {
            // 회원ID에 해당하는 회원이 판매하고 있는 상품 리스트
            List<ProductDTO> productList = productService.getCustomerProducts(customerId);
            model.addAttribute("productList", productList);
        }

        model.addAttribute("customerId", customerId);
        model.addAttribute("category", category);
        model.addAttribute("searchWord", searchWord);
        
        return "main/myproducts";
    }


    /**
     * [마이페이지 > 나의 상품 목록 화면에서 delete]
     * 전달 받은 상품ID에 해당하는 상품의 삭제 요청 및
     * 다시 마이페이지의 자신이 판매하는 상품목록 화면 재요청 처리 (상품페이지에서 delete 버튼 클릭시는 다른 요청의 형태로 만들 것임)
     * 
     * @param productId
     * @return
     */
    @GetMapping("main/myproducts/delete")
    public String myProductsDelete(
            @RequestParam(name = "productId", defaultValue = "CO00006-20240409") String productId,
            @RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            RedirectAttributes attributes) {

        // 상품ID의 productDelete값 변경 (N->Y)
        productService.updateDeleteCheck(productId);

        // 회원ID가 판매하는 상품 목록
        List<ProductDTO> productList = productService.getCustomerProducts(customerId);
        // 리다이렉트 시 Flash Attributes에 상품 목록 저장
        attributes.addFlashAttribute("productList", productList);

        attributes.addAttribute("customerId", customerId);

        return "redirect:/main/myproducts";
    }

    /** 
     * myinfo(mypage) 페이지 요청
     * 마이페이지에서 Info 버튼 클릭 시 전달받은 or 로그인한 회원ID의 회원 정보가 채워진 상태의 화면 요청
     * 
     * @return
     */
    @GetMapping("main/myinfo")
    public String myinfo(@RequestParam(name = "customerId", defaultValue = "jooyoungyoon") String customerId,
            Model model) {
        if (!model.containsAttribute("customerDTO")){
            CustomerDTO customerDTO = customerService.getCustomer(customerId);
            model.addAttribute("customer", customerDTO);
        }
        return "main/myInfo";
    }


    /**
     * 수정된 customerDTO를 전달받아 해당 customer의 정보를 수정하는 요청
     * @param dto
     * @param attr
     * @return
     */
    @PostMapping("main/myinfo/updateCustomer")
    public String updateCustomer(@ModelAttribute CustomerDTO dto, RedirectAttributes attr) {
        CustomerDTO customerDTO =  customerService.updateCustomer(dto);
        attr.addFlashAttribute("customer", customerDTO);
        return "redirect:/main/myinfo";
    }
    


}
