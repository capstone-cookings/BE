package com.cook.cookapp.user.repository;

import com.cook.cookapp.user.entity.ProfileImage;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    ProfileImage findByUser(User user);
}
