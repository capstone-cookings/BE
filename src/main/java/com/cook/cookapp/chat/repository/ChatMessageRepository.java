package com.cook.cookapp.chat.repository;

import com.cook.cookapp.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 가장 최근 메시지 1건
    Optional<ChatMessage> findTopByRoomIdOrderBySentAtDesc(Long roomId);

    // 내가 안 읽은 메시지 수
    int countByRoomIdAndSenderIdNotAndIsReadFalse(Long roomId, Long userId);
    
    // 해당 채팅방의 전체 메시지를 시간순으로 조회
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(Long roomId);

    // lastMessageId를 기준으로 이전 메시지를 가져오는 쿼리 (스크롤 구현을 위함)
    @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :roomId AND (:lastMessageId IS NULL OR m.id < :lastMessageId) ORDER BY m.id DESC")
    List<ChatMessage> findMessagesBeforeId(
            @Param("roomId") Long roomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

    // 메시지 ID만 조회 (roomId 기준)
    @Query("select m.id from ChatMessage m where m.roomId = :roomId")
    List<Long> findIdsByRoomId(@Param("roomId") Long roomId);
}

