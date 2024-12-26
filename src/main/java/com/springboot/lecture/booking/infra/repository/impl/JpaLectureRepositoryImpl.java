package com.springboot.lecture.booking.infra.repository.impl;

import com.springboot.lecture.booking.domain.entity.LectureEntity;
import com.springboot.lecture.booking.domain.repository.LectureRepository;
import com.springboot.lecture.booking.infra.repository.LectureJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaLectureRepositoryImpl implements LectureRepository {
    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public Optional<LectureEntity> findByIdForUpdate(long lectureId){
        return lectureJpaRepository.findByIdForUpdate(lectureId);
    }

    @Override
    public List<LectureEntity> findLecturesByLectureStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate){
        return lectureJpaRepository.findLecturesByLectureStartTimeBetween(startDate, endDate);
    }

    @Override
    public List<LectureEntity> findAllById(Iterable<Long> ids) {
        return lectureJpaRepository.findAllById(ids);
    }

    @Override
    public LectureEntity save(LectureEntity lectureEntity) {
        return lectureJpaRepository.save(lectureEntity);
    }

}