package com.cook.cookapp.user.service;

import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.post.entity.Post;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.user.dto.req.ReportDtoReq;
import com.cook.cookapp.user.entity.Report;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.ReportRepository;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void reportUser(Long reporterId, Long reportedUserId, ReportDtoReq.ReportRequestDto dto) {
        if (reporterId.equals(reportedUserId)) {
            throw new GeneralException(ErrorStatus.CANNOT_REPORT_SELF);
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        User reported = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 하루 이내에 신고한 이력이 있으면 신고 불가
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        boolean alreadyReported = reportRepository.existsByReporterAndReportedUserAndReportTypeAndCreatedAtAfter(
                reporter, reported, dto.getReportType(), oneDayAgo);

        if (alreadyReported) {
            throw new GeneralException(ErrorStatus.ALREADY_REPORTED_TODAY);
        }


        // 신고 저장
        Report report = Report.builder()
                .reportType(dto.getReportType())
                .content(dto.getContent())
                .reporter(reporter)
                .reportedUser(reported)
                .build();
        reportRepository.save(report);

    }

    @Transactional
    public void reportUserByPost(Long reporterId, Long postId, ReportDtoReq.ReportRequestDto dto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        if (reporterId.equals(post.getUser().getId())) {
            throw new GeneralException(ErrorStatus.CANNOT_REPORT_SELF);
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        User reported = userRepository.findById(post.getUser().getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 하루 이내에 신고한 이력이 있으면 신고 불가
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        boolean alreadyReported = reportRepository.existsByReporterAndReportedUserAndReportTypeAndCreatedAtAfter(
                reporter, reported, dto.getReportType(), oneDayAgo);

        if (alreadyReported) {
            throw new GeneralException(ErrorStatus.ALREADY_REPORTED_TODAY);
        }


        // 신고 저장
        Report report = Report.builder()
                .reportType(dto.getReportType())
                .content(dto.getContent())
                .reporter(reporter)
                .reportedUser(reported)
                .build();
        reportRepository.save(report);

    }
}
