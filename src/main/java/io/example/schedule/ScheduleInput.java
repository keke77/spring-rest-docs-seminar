package io.example.schedule;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by gmind on 2015-10-05.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ScheduleInput implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonUnwrapped
    private Long id;

    @NonNull
    private LocalDate day;

    @NonNull
    private LocalTime startTime;

    @NonNull
    private LocalTime endTime;

    @NonNull
    private Integer slotNo;

    @NonNull
    private Long doctorId;

    private Long patientId;

}
