package com.example.doan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.doan.entity.UserEntity;
import com.example.doan.repository.UserRepository;
import com.example.doan.status.GenderEnum;
import com.example.doan.status.UserType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        GenderEnum gender = GenderEnum.NAM; 
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setFullName(fullName); 
            user.setGender(GenderEnum.NAM); 
            userRepository.save(user);
        } else {
            user = UserEntity.builder()
                    .userName(email)
                    .fullName(fullName)
                    .gender(gender)
                    .email(email)
                    .passWord("")  
                    .type(UserType.USER)
                    .is_deleted((byte) 0)
                    .build();
            userRepository.save(user);
            
    
        }
        
        
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getType().name())),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
