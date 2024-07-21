package net.kdigital.ec21.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.kdigital.ec21.dto.CustomerDTO;
import net.kdigital.ec21.dto.LoginUserDetails;
import net.kdigital.ec21.entity.CustomerEntity;
import net.kdigital.ec21.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService{
    
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String customerId) throws UsernameNotFoundException {
        // customerId 검증 로직 필요 테이블에 접근해서 데이터 가져옴
        // 사용자가 로그린하면 SecurityConfig가 username을 전달함
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                                                        .orElseThrow(()->{
                                                            throw new UsernameNotFoundException("Error : 존재하지 않는 아이디");
                                                        });
        CustomerDTO customerDTO = CustomerDTO.toDTO(customerEntity);
        // 반환을 UserDtails로 반환해야 하므로 CustomerDTO를 UserDetails로 바꿔야 함

        return new LoginUserDetails(customerDTO);
    }

}
