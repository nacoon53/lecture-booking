package com.springboot.lecture.booking.domain.repository;

import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;

import java.util.List;

public interface EnrollmentHistoryRepository {
    List<EnrollmentHistoryEntity> getEnrollmentListByUser(long userId, String status);
}
