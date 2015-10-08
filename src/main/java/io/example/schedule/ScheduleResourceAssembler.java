package io.example.schedule;

import io.example.doctor.DoctorRestController;
import io.example.patient.PatientRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static io.example.schedule.ScheduleResourceAssembler.ScheduleResource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by gmind on 2015-10-05.
 */
@Component
public class ScheduleResourceAssembler extends ResourceAssemblerSupport<Schedule, ScheduleResource> {

    public ScheduleResourceAssembler() {
        super(ScheduleRestController.class, ScheduleResource.class);
    }

    @Override
    public ScheduleResource toResource(Schedule schedule) {
        ScheduleResource resource = createResourceWithId(schedule.getId(), schedule);
        if(schedule.getDoctor()!=null) {
            resource.add(linkTo(DoctorRestController.class).slash(schedule.getDoctor().getId()).withRel("doctor"));
        }
        if(schedule.getPatient()!=null) {
            resource.add(linkTo(PatientRestController.class).slash(schedule.getPatient().getId()).withRel("patient"));
        }
        return resource;
    }

    @Override
    protected ScheduleResource instantiateResource(Schedule schedule) {
        return new ScheduleResource(schedule);
    }

    public static class ScheduleResource extends Resource<Schedule> {
        public ScheduleResource(Schedule schedule) {
            super(schedule);
        }

    }

}
