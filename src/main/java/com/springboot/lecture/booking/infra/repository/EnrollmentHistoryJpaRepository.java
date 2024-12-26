package com.springboot.lecture.booking.infra.repository;

import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentHistoryJpaRepository extends JpaRepository<EnrollmentHistoryEntity, Long> {
    EnrollmentHistoryEntity findTopByUserIdAndLectureIdOrderByCreatedAtDesc(long userId, Long lectureId);

    @Query(""" 
            SELECT e FROM EnrollmentHistoryEntity e
            WHERE e.userId = :userId
            AND e.createdAt = (
                SELECT MAX(sub.createdAt)
                FROM EnrollmentHistoryEntity sub
                WHERE sub.userId = e.userId AND sub.lectureId = e.lectureId)
            AND e.status = :status
            ORDER BY e.createdAt desc
           """)
    List<EnrollmentHistoryEntity> getEnrollmentListByUser(@Param("userId") long userId, @Param("status") String status);

}