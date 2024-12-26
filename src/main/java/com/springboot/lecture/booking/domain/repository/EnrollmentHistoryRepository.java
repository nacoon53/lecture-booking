package com.springboot.lecture.booking.domain.repository;

import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;

import java.util.List;

public interface EnrollmentHistoryRepository {
    EnrollmentHistoryEntity findTopByUserIdAndLectureIdOrderByCreatedAtDesc(long userId, Long lectureId);
    List<EnrollmentHistoryEntity> getEnrollmentListByUser(long userId, String status);
    EnrollmentHistoryEntity save(EnrollmentHistoryEntity enrollmentHistoryEntity);
}
