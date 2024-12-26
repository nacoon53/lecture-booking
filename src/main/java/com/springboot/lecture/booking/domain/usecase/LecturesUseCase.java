package com.springboot.lecture.booking.domain.usecase;

import com.springboot.lecture.booking.domain.entity.LectureEntity;

import java.util.List;

public interface LecturesUseCase {
    List<LectureEntity> getAppliedLecturesByUser(long userId);
    List<LectureEntity> getAvailableLecturesByUserAndDate(long userId, String date);
}
