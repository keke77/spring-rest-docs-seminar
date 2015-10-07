package io.example.patient;

import io.example.schedule.Schedule;
import io.example.common.AbstractPersistable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by gmind on 2015-10-05.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "schedules")
@ToString(callSuper = true, exclude = "schedules")
@Entity
public class Patient extends AbstractPersistable<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public Patient(Long id) {
        this.setId(id);
    }

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDay;

    @OneToMany(mappedBy = "patient")
    private List<Schedule> schedules;
}
