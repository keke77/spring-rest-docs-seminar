package io.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.example.patient.Patient;
import io.example.patient.PatientJpaRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gmind on 2015-10-06.
 */
public class TestPatientRestController extends TestBootConfig {

    @Autowired
    private PatientJpaRepository patientJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void patientShowAll() throws Exception {
        this.mockMvc.perform(get("/patients?page=2&size=10"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        links(
                                linkWithRel("next").optional().description("--"),
                                linkWithRel("prev").optional().description("--"),
                                linkWithRel("self").description("---")),
                        responseFields(
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("<<resources-patients-show-all-links,Doctors>> Resources"),
                                fieldWithPath("_embedded.patients").type(JsonFieldType.OBJECT).description("<<resource-patients-show-one, Doctor>> Resources"),
                                fieldWithPath("page").type(JsonFieldType.OBJECT).description("Information On <<overview-pagination, Pagination>>"))));
    }

    @Test
    public void patientShowOne() throws Exception {
        Patient patient = this.patientJpaRepository.findAll().get(0);
        this.mockMvc.perform(get("/patients/"+patient.getId()))
                .andExpect(status().isOk())
                .andDo(createPatientResultHandler(
                        linkWithRel("self").description("---"),
                        linkWithRel("patient_schedules").description("---")));
    }

    @Test
    public void patientCreate() throws Exception {
        Map<String, String> create = Maps.newHashMap();
        create.put("name", "patient_name_create");
        create.put("birthDate", "2015-10-01");
        this.mockMvc.perform(
                put("/patients")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(create)))
                .andExpect(header().string("Location", notNullValue()))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void patientUpdate() throws Exception {
        Patient patient = this.patientJpaRepository.findAll().get(0);
        Map<String, String> update = Maps.newHashMap();
        update.put("name", patient.getName()+"_update");
        update.put("birthDate", "2015-10-02");
        this.mockMvc.perform(
                patch("/patients/"+patient.getId())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void patientDelete() throws Exception {
        Patient patient = this.patientJpaRepository.findAll().get(0);
        this.mockMvc.perform(
                delete("/patients/" + patient.getId())
                        .contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isOk());
    }

    public ResultHandler createPatientResultHandler(LinkDescriptor... linkDescriptors) {
        return this.document.snippets(
                links(linkDescriptors),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("환자아이디"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("환자명"),
                        fieldWithPath("birthDate").type(JsonFieldType.OBJECT).description("환자생년월일"),
                        fieldWithPath("_links").type(JsonFieldType.OBJECT).description("환자정보")));
    }
}
