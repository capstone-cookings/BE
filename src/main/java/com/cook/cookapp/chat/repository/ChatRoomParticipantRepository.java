package com.cook.cookapp.chat.repository;

import com.cook.cookapp.chat.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    List<ChatRoomParticipant> findAllByUserId(Long userId);
    Optional<ChatRoomParticipant> findByUserIdAndRoomId(Long userId, Long roomId);
    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
}
