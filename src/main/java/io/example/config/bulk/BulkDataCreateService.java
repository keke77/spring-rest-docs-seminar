package io.example.config.bulk;

import com.google.common.collect.Lists;
import io.example.doctor.Doctor;
import io.example.doctor.DoctorJpaRepository;
import io.example.patient.Patient;
import io.example.patient.PatientJpaRepository;
import io.example.schedule.Schedule;
import io.example.schedule.ScheduleJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by gmind on 2015-10-07.
 */
@Slf4j
@Service
public class BulkDataCreateService {

    @Autowired
    private DoctorJpaRepository doctorJpaRepository;

    @Autowired
    private PatientJpaRepository patientJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    public void createDoctor(int startInclusive, int endExclusive) {
        this.doctorJpaRepository.deleteAll();
        IntStream.range(startInclusive, endExclusive).forEach(x -> {
            Doctor doctor = new Doctor();
            doctor.setName("doctor_name_"+x);
            Doctor entity = this.doctorJpaRepository.save(doctor);
            log.debug(entity.toString());
        });
    }

    public void createPatient(int startInclusive, int endExclusive) {
        this.patientJpaRepository.deleteAll();
        IntStream.range(startInclusive, endExclusive).forEach(x -> {
            Patient patient = new Patient();
            patient.setName("patient_name_"+x);
            patient.setBirthDate(LocalDate.now().minusYears(x).minusMonths(x).minusDays(x));
            Patient entity = this.patientJpaRepository.save(patient);
            log.debug(entity.toString());
        });
    }

    public void createSchedule() {
        this.scheduleJpaRepository.deleteAll();

        List<LocalDateTime> scheduleCalendar = scheduleCalendar();

        List<Doctor> doctors = this.doctorJpaRepository.findAll();

        doctors.forEach(doctor -> {
            scheduleCalendar.forEach(dateTime -> {
                Schedule schedule = new Schedule();
                schedule.setDoctor(doctor);
                schedule.setAppointmentDay(dateTime.toLocalDate());
                schedule.setStartTime(dateTime.toLocalTime());
                schedule.setEndTime(dateTime.plusMinutes(30).toLocalTime());
                Schedule entity = this.scheduleJpaRepository.save(schedule);
                log.debug(entity.toString());
            });
        });
    }

    protected List<LocalDateTime> scheduleCalendar() {

        List<LocalDateTime> scheduleCalendar = Lists.newArrayList();

        LocalDateTime currentDateTime = LocalDateTime.now();

        int currentYear = currentDateTime.getYear();
        Month currentMonth = currentDateTime.getMonth();
        int currentDayOfMonth = currentDateTime.getDayOfMonth();
        int currentLastDayOfMonth = currentMonth.maxLength();

        IntStream.range(currentDayOfMonth, currentLastDayOfMonth).forEach(day -> {
            LocalDateTime startDateTime  = LocalDateTime.of(currentYear, currentMonth, day, 9, 0, 0);
            LocalDateTime finishDateTime = LocalDateTime.of(currentYear, currentMonth, day, 15, 30, 0);
            scheduleCalendar.add(startDateTime);
            while(!startDateTime.equals(finishDateTime)) {
                startDateTime = startDateTime.plusMinutes(30);
                scheduleCalendar.add(startDateTime);
            }
        });

        return scheduleCalendar;
    }

}
