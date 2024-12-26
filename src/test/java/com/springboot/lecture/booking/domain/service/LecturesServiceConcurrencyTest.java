package com.springboot.lecture.booking.domain.service;

import com.springboot.lecture.booking.domain.code.EnrollmentStatus;
import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;
import com.springboot.lecture.booking.domain.entity.LectureEntity;
import com.springboot.lecture.booking.domain.repository.EnrollmentHistoryRepository;
import com.springboot.lecture.booking.domain.repository.LectureRepository;
import com.springboot.lecture.booking.infra.repository.EnrollmentHistoryJpaRepository;
import com.springboot.lecture.booking.infra.repository.LectureJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@SpringBootTest
class LecturesServiceConcurrencyTest {

    @Autowired
    private LecturesService lecturesService;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private EnrollmentHistoryRepository enrollmentHistoryRepository;

    @Autowired
    private EnrollmentHistoryJpaRepository enrollmentHistoryJpaRepository;

    private long userId;
    private long lectureId;

    LectureEntity savedLecture = null;

    @BeforeEach
    void setUp() {
        userId = 1L;

        savedLecture = LectureEntity.builder()
                .lectureName("테스트 강연1")
                .lecturerName("강호동")
                .lectureStartTime(LocalDateTime.of(2024, 12, 25, 12, 0, 0))
                .lectureEndTime(LocalDateTime.of(2024, 12, 25, 13, 0, 0))
                .build();

        lectureJpaRepository.save(savedLecture);
        lectureId = savedLecture.getLectureId();
    }
    
    @Test
    void 동시에_동일한_특강에_40명이_신청할_때_선착순_30명만_성공하고_나머지는_실패한다() throws InterruptedException {
        //given
        int threadCount = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            long tempUserId = (long)i;
            executorService.execute(() -> {
                try {
                    lecturesService.applyLecture(tempUserId, lectureId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        //then
        LectureEntity lecture = lectureJpaRepository.findById(lectureId).orElseThrow(RuntimeException::new);

        assertThat(lecture).isNotNull();
        assertThat(lecture.getEnrolledCount()).isEqualTo(30);

        // 사용자 별 수강신청 내용 확인
        int appliedCount = 0;
        for (long i = 0; i < threadCount; i++) {
            EnrollmentHistoryEntity enrollmentHistory = enrollmentHistoryRepository.findTopByUserIdAndLectureIdOrderByCreatedAtDesc(i, lectureId);

            if(!ObjectUtils.isEmpty(enrollmentHistory) && EnrollmentStatus.APPLY.toString().equals(enrollmentHistory.getStatus())) {
                appliedCount++;
            }
        }
        assertThat(appliedCount).isEqualTo(30);
    }
}