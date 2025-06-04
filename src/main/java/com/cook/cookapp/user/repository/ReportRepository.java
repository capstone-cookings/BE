package com.cook.cookapp.user.repository;

import com.cook.cookapp.user.entity.Report;
import com.cook.cookapp.user.entity.Enum.ReportType;
import com.cook.cookapp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReportedUserAndReportTypeAndCreatedAtAfter(
            User reporter, User reportedUser, ReportType type, LocalDateTime after);

    void deleteAllByReportedUser(User user);
    void deleteAllByReporter(User user);
}
