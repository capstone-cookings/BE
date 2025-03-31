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

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // 일반 로그인 처리: 이메일로 유저 찾고 토큰 발급
    public UserDtoRes.UserLoginRes login(HttpServletRequest request, HttpServletResponse response, UserDtoReq.LoginReq loginDto) {
        String email = loginDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        log.info("[로그인 완료] 발급된 리프레시 토큰: {}", refreshToken);

        return UserConverter.signInRes(user, accessToken, refreshToken, user.getNickname());
    }

    // 로그아웃 처리: 액세스 토큰 블랙리스트 추가 및 리프레시 토큰 삭제
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken();
        jwtTokenProvider.handleLogout(accessToken, refreshToken);
    }

    // 카카오 로그인 시 신규 회원가입 또는 기존 회원 조회
    public User kakaoSignup(KakaoUserInfoResponseDto userInfo) {
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

    // 카카오 로그인 처리 후 토큰 발급
    public UserDtoRes.UserLoginRes kakaoLogin(HttpServletRequest request, HttpServletResponse response, User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        log.info("[카카오 로그인] 발급된 리프레시 토큰: {}", refreshToken);

        return UserConverter.signInRes(user, accessToken, refreshToken, user.getNickname());
    }

    // 닉네임 중복 검사
    public boolean duplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 닉네임으로 유저 조회
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // ID로 유저 조회
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    // 사용자 프로필 조회 (ID 기준)
    public UserDtoRes.UserProfileRes getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.userProfileRes(user);
    }

    // 사용자 프로필 조회 (닉네임 기준)
    public UserDtoRes.UserProfileRes getUserProfileByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.userProfileRes(user);
    }

    // 음식 취향 업데이트 (입력값 검증 포함)
    public void updateTastePreference(Long userId, UserDtoReq.TastePreferenceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        TastePreferenceValidator.validateTastePreference(request.getTastePreference());
        user.setTastePreference(request.getTastePreference());
        userRepository.save(user);
    }

    // 저장된 음식 취향 가져오기
    public String getTastePreference(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        return user.getTastePreference() != null ? user.getTastePreference() : "";
    }

    // 닉네임 추가 및 저장
    public void addNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.setNickname(nickname);
    }

    public void addLocation(Long userId, UserDtoReq.UserLocationReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.setDistrict(request.getDistrict());
        user.setNeighborhood(request.getNeighborhood());
    }

    public UserDtoRes.UserLocationRes getLocation(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return UserDtoRes.UserLocationRes.builder()
                .district(user.getDistrict())
                .neighborhood(user.getNeighborhood())
                .build();
    }

}
