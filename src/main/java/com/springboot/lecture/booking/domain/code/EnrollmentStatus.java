package com.springboot.lecture.booking.domain.code;

import com.springboot.lecture.booking.domain.entity.EnrollmentHistoryEntity;

public enum EnrollmentStatus {
    APPLY("APPLY"),
    CANCEL("CANCEL");

    private String str;

    EnrollmentStatus(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }
}