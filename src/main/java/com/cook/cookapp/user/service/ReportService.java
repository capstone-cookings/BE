package com.cook.cookapp.user.service;

import com.cook.cookapp.user.dto.req.ReportDtoReq;

public interface ReportService {
    void reportUser(Long reporterId, Long reportedUserId, ReportDtoReq.ReportRequestDto dto);
    void reportUserByPost(Long reporterId, Long postId, ReportDtoReq.ReportRequestDto dto);
}
