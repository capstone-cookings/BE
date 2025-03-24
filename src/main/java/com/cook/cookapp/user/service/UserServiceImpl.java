package com.cook.cookapp.user.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.chatbot.util.TastePreferenceValidator;
import com.cook.cookapp.common.security.JwtTokenProvider;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDtoRes.UserLoginRes login(HttpServletRequest request, HttpServletResponse response, UserDtoReq.LoginReq loginDto) {

        String email = loginDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

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
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return user;
    }

    public User getUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    public UserDtoRes.UserProfileRes getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.userProfileRes(user);
    }

    public UserDtoRes.UserProfileRes getUserProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return UserConverter.userProfileRes(user);
    }

    // 취향 업데이트
    public void updateTastePreference(Long userId, UserDtoReq.TastePreferenceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 필터 검증 실행
        TastePreferenceValidator.validateTastePreference(request.getTastePreference());

        // 검증 후 저장
        user.setTastePreference(request.getTastePreference());
        userRepository.save(user);
    }

    // 취향 조회
    public String getTastePreference(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return user.getTastePreference() != null ? user.getTastePreference() : "";
    }

    public void addNickname(Long userId, String nickname){
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.setNickname(nickname);
        userRepository.save(user);
    }


}


