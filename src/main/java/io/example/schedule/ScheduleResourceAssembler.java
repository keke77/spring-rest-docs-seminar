package io.example.schedule;

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
        resource.add(linkTo(ScheduleResourceAssembler.class).slash(schedule.getId()).slash("doctor").withRel("doctor"));
        resource.add(linkTo(ScheduleResourceAssembler.class).slash(schedule.getId()).slash("patient").withRel("patient"));
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
