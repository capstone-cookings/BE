package com.cook.cookapp.user.repository;

import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
    @Override
    <S extends User> List<S> findAll(Example<S> example);
    Recipe save(Recipe recipe);
}
