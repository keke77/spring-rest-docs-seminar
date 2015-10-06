package io.example.doctor;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static io.example.doctor.DoctorResourceAssembler.DoctorResource;
import static io.example.schedule.ScheduleResourceAssembler.ScheduleResource;

/**
 * Created by gmind on 2015-10-05.
 */
@RestController
@RequestMapping(value = "/doctors")
public class DoctorRestController {

    @Autowired
    private DoctorJpaRepository doctorJpaRepository;

    @Autowired
    private DoctorResourceAssembler doctorResourceAssembler;

    @Autowired
    private ScheduleResourceAssembler scheduleResourceAssembler;

    @Autowired
    private PagedResourcesAssembler pagedResourcesAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<DoctorResource> showAll(@PageableDefault Pageable pageable) {
        Page<Doctor> doctors = this.doctorJpaRepository.findAll(pageable);
        return this.pagedResourcesAssembler.toResource(doctors, doctorResourceAssembler);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Doctor> showOne(@PathVariable("id") Long id) {
        return this.doctorResourceAssembler.toResource(this.doctorJpaRepository.findOne(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.PUT)
    public HttpHeaders create(@RequestBody Doctor doctorInput) {
        HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.setLocation(linkTo(DoctorRestController.class).slash(note.getId()).toUri());
        return httpHeaders;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public void update(@PathVariable("id") Long id, @RequestBody Doctor doctorInput) {
        Doctor doctor = this.doctorJpaRepository.findOne(id);
        this.doctorJpaRepository.save(doctorInput);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void  delete(@PathVariable("id") Long id) {
        this.doctorJpaRepository.delete(id);
    }

    @RequestMapping(value = "/{id}/schedules", method = RequestMethod.GET)
    public ResourceSupport showPropertyAll(@PathVariable("id") Long id) {
        Doctor doctor = this.doctorJpaRepository.findOne(id);
        List<ScheduleResource> resources = this.scheduleResourceAssembler.toResources(doctor.getSchedules());
        return new NestedContentResource<ScheduleResource>(resources);
    }

}
