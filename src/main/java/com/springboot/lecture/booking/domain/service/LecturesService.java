package com.springboot.lecture.booking.domain.service;

import com.springboot.lecture.booking.domain.entity.LectureEntity;
import com.springboot.lecture.booking.domain.repository.LectureRepository;
import com.springboot.lecture.booking.domain.usecase.LecturesUseCase;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LecturesService implements LecturesUseCase {
    private final EnrollmentHistoryService enrollmentHistoryService;
    private final LectureRepository lectureRepository;

    @Override
    @Transactional
    public LectureEntity applyLecture(long userId, long lectureId) {
        LectureEntity lecture = lectureRepository.findByIdForUpdate(lectureId).orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        // 누가 무슨 강의를 신청했는지 DB에 저장
        enrollmentHistoryService.saveEnrollmentHistoryForApply(userId, lectureId);

        lecture.increaseEnrolledCount();
        lectureRepository.save(lecture);

        return lecture;
    }

    @Override
    public List<LectureEntity> getAppliedLecturesByUser(long userId) {
        List<Long> lectureIds = enrollmentHistoryService.getAppliedLectureIdsByUser(userId);

        return lectureRepository.findAllById(lectureIds);
    }

    @Override
    public List<LectureEntity> getAvailableLecturesByUserAndDate(long userId, String date) {
        //강연 시작일자가 date인 강연 목록을 가져온다.
        List<LectureEntity> lectures = findLecturesBydate(userId, date);

        // 수강 가능한 강의만 리스트에 남긴다.
        lectures.stream()
                .filter(LectureEntity::isLectureAvailableForBooking)
                .collect(Collectors.toList());

        //user가 신청한 강연을 가져온다.
        List<Long> lectureIds = enrollmentHistoryService.getAppliedLectureIdsByUser(userId);

        //강연 목록에서 user가 신청한 강연을 제외한 목록을 리턴한다.
        lectures.removeIf(lecture -> lectureIds.contains(lecture.getLectureId()));

        return lectures;
    }

    public List<LectureEntity> findLecturesBydate(long userId, String date) {
        //강연 시작일자가 date인 강연 목록을 가져온다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate targetDate = LocalDate.parse(date, formatter);
        LocalDateTime startDateTime = targetDate.atStartOfDay();
        LocalDateTime endDateTime = targetDate.plusDays(1).atStartOfDay();

        return lectureRepository.findLecturesByLectureStartTimeBetween(startDateTime, endDateTime);
    }

}
