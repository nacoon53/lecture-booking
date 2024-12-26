package com.springboot.lecture.booking.infra.repository;

import com.springboot.lecture.booking.domain.entity.LectureEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LectureJpaRepository extends JpaRepository<LectureEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LectureEntity l WHERE l.lectureId = :lectureId")
    Optional<LectureEntity> findByIdForUpdate(@Param("lectureId") long lectureId);

    @Query("SELECT l FROM LectureEntity l WHERE l.lectureStartTime >= :startDate AND l.lectureStartTime < :endDate")
    List<LectureEntity> findLecturesByLectureStartTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}