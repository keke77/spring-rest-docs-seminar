package io.example.patient;

import io.example.schedule.ScheduleResourceAssembler;
import io.example.common.NestedContentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.example.patient.PatientResourceAssembler.PatientResource;
import static io.example.schedule.ScheduleResourceAssembler.ScheduleResource;

/**
 * Created by gmind on 2015-10-05.
 */
@RestController
@RequestMapping(value = "/patients")
public class PatientRestController {

    @Autowired
    private PatientJpaRepository patientJpaRepository;

    @Autowired
    private PatientResourceAssembler patientResourceAssembler;

    @Autowired
    private ScheduleResourceAssembler scheduleResourceAssembler;

    @Autowired
    private PagedResourcesAssembler pagedResourcesAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<PatientResource> showAll(@PageableDefault Pageable pageable) {
        Page<Patient> patients = this.patientJpaRepository.findAll(pageable);
        return this.pagedResourcesAssembler.toResource(patients, patientResourceAssembler);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Patient> showOne(@PathVariable("id") Long id) {
        return this.patientResourceAssembler.toResource(this.patientJpaRepository.findOne(id));
    }

    @RequestMapping(value = "/{id}/schedules", method = RequestMethod.GET)
    public ResourceSupport showPropertyAll(@PathVariable("id") Long id) {
        Patient patient = this.patientJpaRepository.findOne(id);
        List<ScheduleResource> resources = this.scheduleResourceAssembler.toResources(patient.getSchedules());
        return new NestedContentResource<ScheduleResource>(resources);
    }

}
