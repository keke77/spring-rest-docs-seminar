package io.example.patient;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by gmind on 2015-10-05.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PatientInput implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonUnwrapped
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private LocalDate birthDay;

}
