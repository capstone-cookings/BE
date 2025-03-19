package com.cook.cookapp.user.service;

import com.cook.cookapp.common.security.CustomUserDetail;
import com.cook.cookapp.common.security.CustomUserDetailsService;
import com.cook.cookapp.common.security.JwtTokenProvider;
import com.cook.cookapp.recipe.dto.res.RecipeResponseDto;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.entity.RecipeIngredient;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import com.cook.cookapp.user.converter.UserConverter;
import com.cook.cookapp.user.dto.req.UserDtoReq;
import com.cook.cookapp.user.dto.res.KakaoUserInfoResponseDto;
import com.cook.cookapp.user.dto.res.UserDtoRes;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeRepository recipeRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDtoRes.UserLoginRes login(HttpServletRequest request, HttpServletResponse response, UserDtoReq.LoginReq loginDto) {

        String email = loginDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾지 못했습니다."));

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        log.info("login refresh token : {}", refreshToken);

        return UserConverter.signInRes(user, accessToken, refreshToken, user.getNickname());
    }
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        if (accessToken != null) {
            jwtTokenProvider.invalidateToken(accessToken); // 액세스 토큰을 블랙리스트에 추가
        }

        // 리프레시 토큰 삭제 (AccessToken이 만료된 경우 대비)
        String refreshToken = jwtTokenProvider.resolveRefreshToken();
        if (refreshToken != null) {
            Long userId = jwtTokenProvider.getUserIdInToken(refreshToken); // RefreshToken을 활용해 UserId 가져오기
            jwtTokenProvider.deleteRefreshToken(userId);
        }
    }

    public User kakaoSignup(KakaoUserInfoResponseDto userInfo) {
        //이미 회원가입한 이메일이 있다면 user 리턴
        //회원가입된게 없다면 회원가입 및 유저프로필 생성 후 유저 리턴
        return userRepository.findByEmail(userInfo.getKakaoAccount().getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(userInfo.getKakaoAccount().getEmail())
                            .name(userInfo.getKakaoAccount().getProfile().getNickname())
                            .build();
                    userRepository.save(newUser);

                    return newUser;
                });
    }

    public UserDtoRes.UserLoginRes kakaoLogin(HttpServletRequest request, HttpServletResponse response, User user) {

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        log.info("login refresh token : {}", refreshToken);

        return UserConverter.signInRes(user, accessToken, refreshToken, user.getNickname());
    }

    public boolean duplicateNickname(String nickname){
        boolean exists = userRepository.existsByNickname(nickname);
        return exists;
    }

    public User getUserByNickname(String nickname){
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public User getUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public RecipeResponseDto storeRecipe(Long userId, UserDtoReq.RecipeReq requestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Recipe recipe = Recipe.builder()
                .title(requestDto.getRecipe().getTitle())
                .instructions(requestDto.getRecipe().getInstructions())
                .user(user)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        return RecipeResponseDto.builder()
                .title(savedRecipe.getTitle())
                .instructions(savedRecipe.getInstructions())
                .build();
    }
}


