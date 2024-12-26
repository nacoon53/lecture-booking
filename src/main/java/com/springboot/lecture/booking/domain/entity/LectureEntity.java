package com.springboot.lecture.booking.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="lecture")
public class LectureEntity {
    private final int MAX_ENROLL_COUNT = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lectureId;

    private String lectureName; // 강연명
    private String lecturerName; // 강연자명
    private LocalDateTime lectureStartTime;
    private LocalDateTime lectureEndTime;
    @Builder.Default
    private int enrolledCount = 0;
    @CreatedDate
    private LocalDateTime createdAt;

    public void increaseEnrolledCount() {
        // 현재 신청하려는 강의의 수용 인원이 남았는지 확인
        if(!this.isLectureAvailableForBooking()) {
            throw new RuntimeException("수강 신청 가능 인원이 꽉 찼습니다.");
        }

        enrolledCount++;
    }

    public void decreaseEnrolledCount() {

        // 현재 신청하려는 강의의 수용 인원이 남았는지 확인
        if(enrolledCount <= 0) {
            throw new RuntimeException("강의 수용 인원에 문제가 발생했습니다.");
        }

        enrolledCount--;

    }

    public boolean isLectureAvailableForBooking() {
        if(MAX_ENROLL_COUNT > enrolledCount) {
            return true;
        }
        return false;
    }
}
