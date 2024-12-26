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


    public List<Long> getAppliedLectureIdsByUser(long userId) {
        String status = EnrollmentStatus.APPLY.toString();

        List<EnrollmentHistoryEntity> histories = enrollmentHistoryRepository.getEnrollmentListByUser(userId, status);
        return histories.stream()
                .map(EnrollmentHistoryEntity::getLectureId)
                .collect(Collectors.toList());
    }
}
