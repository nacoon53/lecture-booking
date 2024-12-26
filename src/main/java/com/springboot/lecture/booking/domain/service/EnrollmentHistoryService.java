package com.springboot.lecture.booking.domain.service;

import com.springboot.lecture.booking.domain.code.EnrollmentStatus;
import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;
import com.springboot.lecture.booking.domain.repository.EnrollmentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EnrollmentHistoryService {
    private final EnrollmentHistoryRepository enrollmentHistoryRepository;

    public EnrollmentHistoryEntity saveEnrollmentHistoryForApply(long userId, long lectureId) {
        if(hasAlreadyApplied(userId, lectureId)) {
            throw new RuntimeException("이미 신청한 강의 입니다.");
        }

        EnrollmentHistoryEntity enrollmentHistoryEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.APPLY.toString())
                .build();

        return enrollmentHistoryRepository.save(enrollmentHistoryEntity);
    }

    public EnrollmentHistoryEntity saveEnrollmentHistoryForCancel(long userId, long lectureId) {
        if(hasNotAlreadyApplied(userId, lectureId)) {
            throw new RuntimeException("현재 신청중인 강의가 아닙니다.");
        }

        EnrollmentHistoryEntity enrollmentHistoryEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.CANCEL.toString())
                .build();

        return enrollmentHistoryRepository.save(enrollmentHistoryEntity);
    }

    public boolean hasAlreadyApplied(long userId, long lectureId) {
        EnrollmentHistoryEntity entity = enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(userId, lectureId);

        if(ObjectUtils.isEmpty(entity) || EnrollmentStatus.CANCEL.toString().equals(entity.getStatus())) {
            return false;
        }

        return true;
    }

    public boolean hasNotAlreadyApplied(long userId, long lectureId) {
        return !hasAlreadyApplied(userId, lectureId);
    }

    public List<Long> getAppliedLectureIdsByUser(long userId) {
        String status = EnrollmentStatus.APPLY.toString();

        List<EnrollmentHistoryEntity> histories = enrollmentHistoryRepository.getEnrollmentListByUser(userId, status);
        return histories.stream()
                .map(EnrollmentHistoryEntity::getLectureId)
                .collect(Collectors.toList());
    }
}
