package com.springboot.lecture.booking.domain.service;

import com.springboot.lecture.booking.domain.entity.LectureEntity;
import com.springboot.lecture.booking.domain.repository.LectureRepository;
import com.springboot.lecture.booking.infra.repository.LectureJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LecturesServiceTest {

    @Mock
    LectureRepository lectureRepository;

    @Mock
    EnrollmentHistoryService enrollmentHistoryService;

    @InjectMocks
    LecturesService lecturesService;


    @Test
    public void 사용자가_수강_가능_인원이_남은_강의에_신청했을_때_신청에_성공한다() {
        //given
        long userId = 1L;
        long lectureId = 1L;

        LectureEntity lecture = LectureEntity.builder()
                .lectureId(1L)
                .lectureName("테스트 강연1")
                .lecturerName("강호동")
                .lectureStartTime(LocalDateTime.of(2024, 12, 25, 12, 0, 0))
                .lectureEndTime(LocalDateTime.of(2024, 12, 25, 13, 0, 0))
                .build();


        given(lectureRepository.findByIdForUpdate(anyLong())).willReturn(Optional.ofNullable(lecture));


        //when
        LectureEntity result = lecturesService.applyLecture(userId, lectureId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEnrolledCount()).isEqualTo(1);

        verify(lectureRepository).save(any());
        verify(enrollmentHistoryService).saveEnrollmentHistoryForApply(anyLong(), anyLong());
    }

    @Test
    public void 사용자가_수강_가능_인원이_꽉찬_강의에_신청했을_때_신청에_실패한다() {
        //given
        long userId = 1L;
        long lectureId = 1L;

        LectureEntity lecture = LectureEntity.builder()
                .lectureId(1L)
                .lectureName("테스트 강연1")
                .lecturerName("강호동")
                .lectureStartTime(LocalDateTime.of(2024, 12, 25, 12, 0, 0))
                .lectureEndTime(LocalDateTime.of(2024, 12, 25, 13, 0, 0))
                .enrolledCount(30)
                .build();


        given(lectureRepository.findByIdForUpdate(anyLong())).willReturn(Optional.ofNullable(lecture));


        //when, then
        assertThatThrownBy(() -> lecturesService.applyLecture(userId, lectureId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("수강 신청 가능 인원이 꽉 찼습니다.");
    }

    @Test
    public void 사용자가_이미_신청한_강의에_신청했을_때_신청에_실패한다() {
        //given
        long userId = 1L;
        long lectureId = 1L;

        LectureEntity lecture = LectureEntity.builder()
                .lectureId(1L)
                .lectureName("테스트 강연1")
                .lecturerName("강호동")
                .lectureStartTime(LocalDateTime.of(2024, 12, 25, 12, 0, 0))
                .lectureEndTime(LocalDateTime.of(2024, 12, 25, 13, 0, 0))
                .enrolledCount(30)
                .build();


        given(lectureRepository.findByIdForUpdate(anyLong())).willReturn(Optional.ofNullable(lecture));


        //when, then
        assertThatThrownBy(() -> lecturesService.applyLecture(userId, lectureId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("수강 신청 가능 인원이 꽉 찼습니다.");
    }

    @Test
    public void 사용자가_신청한_강의를_취소한다() {
        //given
        long userId = 1L;
        long lectureId = 1L;

        LectureEntity lecture = LectureEntity.builder()
                .lectureId(1L)
                .lectureName("테스트 강연1")
                .lecturerName("강호동")
                .lectureStartTime(LocalDateTime.of(2024, 12, 25, 12, 0, 0))
                .lectureEndTime(LocalDateTime.of(2024, 12, 25, 13, 0, 0))
                .enrolledCount(1)
                .build();


        given(lectureRepository.findByIdForUpdate(anyLong())).willReturn(Optional.ofNullable(lecture));


        //when
        LectureEntity result = lecturesService.cancelLecture(userId, lectureId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEnrolledCount()).isEqualTo(0);

        verify(lectureRepository).save(any());
        verify(enrollmentHistoryService).saveEnrollmentHistoryForCancel(anyLong(), anyLong());
    }

}