package com.springboot.lecture.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LectureBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LectureBookingApplication.class, args);
    }

}
