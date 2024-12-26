package com.springboot.lecture.booking.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

class LectureEntityTest {

    @Test
    void 최대_수용_가능_인원이_남았는지_확인한다() {
        //given
        LectureEntity lectureEntity = LectureEntity.builder()
                .enrolledCount(0)
                .build();

        //when
        boolean result = lectureEntity.isLectureAvailableForBooking();

        //then
        assertTrue(result);
    }

    @Test
    void 최대_수용_가능_인원이_있는지_확인한다() {
        //given
        LectureEntity lectureEntity = LectureEntity.builder()
                .enrolledCount(30)
                .build();

        //when
        boolean result = lectureEntity.isLectureAvailableForBooking();

        //then
        assertFalse(result);
    }
}