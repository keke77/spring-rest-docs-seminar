package io.example.schedule;

import io.example.doctor.Doctor;
import io.example.patient.Patient;
import io.example.common.AbstractPersistable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by gmind on 2015-10-05.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"doctor","patient"})
@ToString(callSuper = true, exclude = {"doctor","patient"})
@Entity
public class Schedule extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public Schedule(Long id) {
        this.setId(id);
    }

    @Column(nullable = false)
    private LocalDate day;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer slotNo;

    @ManyToOne(optional = false)
    private Doctor doctor;

    @ManyToOne
    private Patient patient;

}
