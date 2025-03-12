package com.cook.cookapp.common.security;


import com.cook.cookapp.apiPayload.code.exception.handler.UserHandler;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userPk)  {

        User user = userRepository.findById(Long.parseLong(userPk))
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        return new CustomUserDetail(user);	// 위에서 생성한 CustomUserDetails Class
    }
}

