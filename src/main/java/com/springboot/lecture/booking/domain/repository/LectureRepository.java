package com.springboot.lecture.booking.domain.repository;

import com.springboot.lecture.booking.domain.entity.LectureEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {
    Optional<LectureEntity> findByIdForUpdate(long lectureId);
    List<LectureEntity> findLecturesByLectureStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    LectureEntity save(LectureEntity lecture);
    List<LectureEntity> findAllById(Iterable<Long> ids);
}