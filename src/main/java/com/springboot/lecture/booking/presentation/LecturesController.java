package com.springboot.lecture.booking.presentation;

import com.springboot.lecture.booking.domain.service.LecturesService;
import com.springboot.lecture.booking.domain.entity.LectureEntity;
import com.springboot.lecture.booking.domain.usecase.LecturesUseCase;
import com.springboot.lecture.booking.presentation.dto.LectureApplyRequestDTO;
import com.springboot.lecture.booking.presentation.dto.LectureInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/lectures")
@RestController
public class LecturesController {
    private final LecturesUseCase lecturesUseCase;

    long userId = 1L; //이미 로그인 되어 있다 가정함.

    /* (사용자별) 특강 신청이 완료된 목록과 날짜별로 특강 신청 가능한 목록 조회 API */
    @GetMapping
    public List<LectureInfoResponseDTO> getLecturesForUserByStatusAndDate(
            @RequestParam("status") String status
            , @RequestParam(value="date", required = false) String date) {

        if("applied".equals(status)) {
            List<LectureEntity> lectures = lecturesUseCase.getAppliedLecturesByUser(userId);
            return lectures.stream()
                    .map(lecture -> LectureInfoResponseDTO.builder()
                            .lectureId(lecture.getLectureId())
                            .lectureName(lecture.getLectureName())
                            .lecturerName(lecture.getLecturerName())
                            .lectureStartTime(lecture.getLectureStartTime())
                            .lectureEndTime(lecture.getLectureEndTime())
                            .build())
                    .collect(Collectors.toList());
        }

        if("available".equals(status)) {
            List<LectureEntity> lectures = lecturesUseCase.getAvailableLecturesByUserAndDate(userId, date);
            return lectures.stream()
                    .map(lecture -> LectureInfoResponseDTO.builder()
                            .lectureId(lecture.getLectureId())
                            .lectureName(lecture.getLectureName())
                            .lecturerName(lecture.getLecturerName())
                            .lectureStartTime(lecture.getLectureStartTime())
                            .lectureEndTime(lecture.getLectureEndTime())
                            .build())
                    .collect(Collectors.toList());
        }

        throw new RuntimeException("잘못된 접근입니다.");
    }

    @PostMapping("/apply")
    public String applyLectureByLectureId(@RequestBody LectureApplyRequestDTO requestDTO) {
        lecturesUseCase.applyLecture(userId, requestDTO.lectureId());

        return "ok";
    }

    @PostMapping("/cancel")
    public String cancelLectureByLectureId(@RequestBody LectureApplyRequestDTO requestDTO) {
        lecturesUseCase.cancelLecture(userId, requestDTO.lectureId());

        return "ok";
    }
}
