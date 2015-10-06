package io.example.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static io.example.schedule.ScheduleResourceAssembler.ScheduleResource;

/**
 * Created by gmind on 2015-10-05.
 */
@RestController
@RequestMapping(value = "/schedules")
public class ScheduleRestController {

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private ScheduleResourceAssembler scheduleResourceAssembler;

    @Autowired
    private PagedResourcesAssembler pagedResourcesAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public PagedResources<ScheduleResource> showAll(@PageableDefault Pageable pageable) {
        Page<Schedule> schedules = this.scheduleJpaRepository.findAll(pageable);
        return this.pagedResourcesAssembler.toResource(schedules, scheduleResourceAssembler);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource<Schedule> showOne(@PathVariable("id") Long id) {
        return this.scheduleResourceAssembler.toResource(this.scheduleJpaRepository.findOne(id));
    }

}
