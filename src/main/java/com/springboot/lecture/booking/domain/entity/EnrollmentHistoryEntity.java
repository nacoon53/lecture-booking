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
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Enrollment_History")
public class EnrollmentHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long historyId;

    private long lectureId;
    private long userId;

    private String status;

    @CreatedDate
    private LocalDateTime createdAt;

    public long getLectureId() {
        return lectureId;
    }

    public String getStatus() {
        return status;
    }
}
