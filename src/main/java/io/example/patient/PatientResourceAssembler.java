package io.example.patient;

import io.example.doctor.DoctorRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static io.example.patient.PatientResourceAssembler.PatientResource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by gmind on 2015-10-05.
 */
@Component
public class PatientResourceAssembler extends ResourceAssemblerSupport<Patient, PatientResource> {

    public PatientResourceAssembler() {
        super(PatientRestController.class, PatientResource.class);
    }

    @Override
    public PatientResource toResource(Patient patient) {
        PatientResource resource = createResourceWithId(patient.getId(), patient);
        resource.add(linkTo(DoctorRestController.class).slash(patient.getId()).slash("schedules").withRel("patient_schedules"));
        return resource;
    }

    @Override
    protected PatientResource instantiateResource(Patient patient) {
        return new PatientResource(patient);
    }

    public static class PatientResource extends Resource<Patient> {
        public PatientResource(Patient patient) {
            super(patient);
        }

    }
}
