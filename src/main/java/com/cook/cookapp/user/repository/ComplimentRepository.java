package com.cook.cookapp.user.repository;

import com.cook.cookapp.user.entity.Compliment;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplimentRepository extends JpaRepository<Compliment, Long> {
    Optional<Compliment> findByComplimenterId(Long complimenterId);
}
