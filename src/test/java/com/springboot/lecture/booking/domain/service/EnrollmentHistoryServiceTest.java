package com.springboot.lecture.booking.domain.service;

import com.springboot.lecture.booking.domain.code.EnrollmentStatus;
import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;
import com.springboot.lecture.booking.domain.repository.EnrollmentHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentHistoryServiceTest {

    @Mock
    private EnrollmentHistoryRepository enrollmentHistoryRepository;

    @InjectMocks
    private EnrollmentHistoryService enrollmentHistoryService;

    private long userId;
    private long lectureId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        lectureId = 101L;
    }

    @Test
    void 강의_신청_이력_저장에_성공한다() {
        // Given
        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(null); // 이미 신청한 강의가 없으므로 null 반환

        EnrollmentHistoryEntity enrollmentHistoryEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.APPLY.toString())
                .build();

        given(enrollmentHistoryRepository.save(any(EnrollmentHistoryEntity.class)))
                .willReturn(enrollmentHistoryEntity);

        // When
        EnrollmentHistoryEntity result = enrollmentHistoryService.saveEnrollmentHistoryForApply(userId, lectureId);

        // Then
        assertNotNull(result);
        assertEquals(EnrollmentStatus.APPLY.toString(), result.getStatus());
        verify(enrollmentHistoryRepository, times(1)).save(any(EnrollmentHistoryEntity.class));
    }

    @Test
    void 이미_신청한_강의에_대해_강의_신청_이력_저장_시_예외가_발생한다() {
        // Given
        EnrollmentHistoryEntity existingEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.APPLY.toString())
                .createdAt(LocalDateTime.now())
                .build();

        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(existingEntity);

        // When / Then
        assertThatThrownBy(()->enrollmentHistoryService.saveEnrollmentHistoryForApply(userId, lectureId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 신청한 강의 입니다.");
    }

    @Test
    void 강의_취소에_대한_이력_저장에_성공한다() {
        // Given
        EnrollmentHistoryEntity existingEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.APPLY.toString())
                .build();

        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(existingEntity);

        EnrollmentHistoryEntity enrollmentHistoryEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.CANCEL.toString())
                .build();

        given(enrollmentHistoryRepository.save(any(EnrollmentHistoryEntity.class)))
                .willReturn(enrollmentHistoryEntity);

        // When
        EnrollmentHistoryEntity result = enrollmentHistoryService.saveEnrollmentHistoryForCancel(userId, lectureId);

        // Then
        assertNotNull(result);
        assertEquals(EnrollmentStatus.CANCEL.toString(), result.getStatus());
        verify(enrollmentHistoryRepository, times(1)).save(any(EnrollmentHistoryEntity.class));
    }

    @Test
    void 신청하지_않은_강의에_대해_강의_취소_이력_저장_시_예외가_발생한다() {
        // Given
        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(null); // 이미 신청한 강의가 없으므로 null 반환

        // When / Then
        assertThatThrownBy(()->enrollmentHistoryService.saveEnrollmentHistoryForCancel(userId, lectureId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("현재 신청중인 강의가 아닙니다.");
    }

    @Test
    void 강의_신청_여부_확인에_성공한다() {
        // Given
        EnrollmentHistoryEntity existingEntity = EnrollmentHistoryEntity.builder()
                .lectureId(lectureId)
                .userId(userId)
                .status(EnrollmentStatus.APPLY.toString())
                .createdAt(LocalDateTime.now())
                .build();

        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(existingEntity);

        // When
        boolean result = enrollmentHistoryService.hasAlreadyApplied(userId, lectureId);

        // Then
        assertTrue(result);
    }

    @Test
    void 강의_신청_여부_확인에_실패한다() {
        // Given
        given(enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .willReturn(null); // 이미 신청한 강의가 없으므로 null 반환

        // When
        boolean result = enrollmentHistoryService.hasAlreadyApplied(userId, lectureId);

        // Then
        assertFalse(result);
    }
}