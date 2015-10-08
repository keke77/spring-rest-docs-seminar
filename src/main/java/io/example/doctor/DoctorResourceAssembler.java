package io.example.doctor;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static io.example.doctor.DoctorResourceAssembler.DoctorResource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
/**
 * Created by gmind on 2015-10-05.
 */
@Component
public class DoctorResourceAssembler extends ResourceAssemblerSupport<Doctor, DoctorResource> {

    public DoctorResourceAssembler() {
        super(DoctorRestController.class, DoctorResource.class);
    }

    @Override
    public DoctorResource toResource(Doctor doctor) {
        if(doctor.getId()==null) {
            return new DoctorResource(new Doctor());
        }
        DoctorResource resource = createResourceWithId(doctor.getId(), doctor);
        resource.add(linkTo(DoctorRestController.class).slash(doctor.getId()).slash("schedules").withRel("doctor_schedules"));
        return resource;
    }

    @Override
    protected DoctorResource instantiateResource(Doctor doctor) {
        return new DoctorResource(doctor);
    }

    public static class DoctorResource extends Resource<Doctor> {
        public DoctorResource(Doctor doctor) {
            super(doctor);
        }

    }
}
