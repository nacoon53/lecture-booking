package com.springboot.lecture.booking.infra.repository.impl;

import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;
import com.springboot.lecture.booking.domain.repository.EnrollmentHistoryRepository;
import com.springboot.lecture.booking.infra.repository.EnrollmentHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaEnrollmentHistoryRepositoryImpl implements EnrollmentHistoryRepository {
    private final EnrollmentHistoryJpaRepository enrollmentHistoryJpaRepository;

    @Override
    public EnrollmentHistoryEntity findTopByUserIdAndLectureIdOrderByCreatedAtDesc(long userId, Long lectureId) {
        return enrollmentHistoryJpaRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(userId, lectureId);
    }

    @Override
    public List<EnrollmentHistoryEntity> getEnrollmentListByUser(long userId, String status){
        return enrollmentHistoryJpaRepository.getEnrollmentListByUser(userId, status);
    }

    @Override
    public EnrollmentHistoryEntity save(EnrollmentHistoryEntity enrollmentHistoryEntity) {
        return enrollmentHistoryJpaRepository.save(enrollmentHistoryEntity);
    }


}