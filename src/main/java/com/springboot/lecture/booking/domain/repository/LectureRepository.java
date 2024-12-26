package com.springboot.lecture.booking.domain.repository;

import com.springboot.lecture.booking.domain.entity.LectureEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureRepository {
    List<LectureEntity> findLecturesByLectureStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<LectureEntity> findAllById(Iterable<Long> ids);
}