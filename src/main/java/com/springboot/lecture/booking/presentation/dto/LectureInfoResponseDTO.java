package com.springboot.lecture.booking.presentation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LectureInfoResponseDTO(
        long lectureId
        , String lectureName
        , String lecturerName
        , LocalDateTime lectureStartTime
        , LocalDateTime lectureEndTime
) {
}
