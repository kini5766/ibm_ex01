package com.example.demo.domain.user.service;

import com.example.demo.domain.user.repository.UserSocialAccountRepository;
import com.example.demo.security.dto.SocialTypeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSocialAccountService {
    private final UserSocialAccountRepository socialAccountRepository;

    public Boolean existsByEmail(String email) {
        return socialAccountRepository.existsByEmail(email);
    }

    public List<SocialTypeResponseDTO> mySocialList(Long memberId) {
        return socialAccountRepository.findByMemberId(memberId)
                .stream()
                .map(social -> new SocialTypeResponseDTO(social.getProviderType()))
                .toList();
    }
}
