package io.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.example.doctor.Doctor;
import io.example.doctor.DoctorJpaRepository;
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
public class TestDoctorRestController extends TestBootConfig {

    @Autowired
    private DoctorJpaRepository doctorJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void doctorShowAll() throws Exception {
        this.mockMvc.perform(get("/doctors?page=2&size=10"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        links(
                                linkWithRel("next").optional().description("--"),
                                linkWithRel("prev").optional().description("--"),
                                linkWithRel("self").description("---")),
                        responseFields(
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("<<resources-doctors-show-all-links,Doctors>> Resources"),
                                fieldWithPath("_embedded.doctors").type(JsonFieldType.OBJECT).description("<<resource-doctors-show-one, Doctor>> Resources"),
                                fieldWithPath("page").type(JsonFieldType.OBJECT).description("Information On <<overview-pagination, Pagination>>"))));
    }

    @Test
    public void doctorShowOne() throws Exception {
        Doctor doctor = this.doctorJpaRepository.findAll().get(0);
        this.mockMvc.perform(get("/doctors/"+doctor.getId()))
                .andExpect(status().isOk())
                .andDo(createDoctorResultHandler(
                        linkWithRel("self").description("---"),
                        linkWithRel("doctor_schedules").description("---")));
    }

    @Test
    public void doctorCreate() throws Exception {
        Map<String, String> create = Maps.newHashMap();
        create.put("name", "doctor_name_create");
        this.mockMvc.perform(
                put("/doctors")
                    .contentType(MediaTypes.HAL_JSON)
                    .content(this.objectMapper.writeValueAsString(create)))
                .andExpect(header().string("Location", notNullValue()))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void doctorUpdate() throws Exception {
        Doctor doctor = this.doctorJpaRepository.findAll().get(0);
        Map<String, String> update = Maps.newHashMap();
        update.put("name", doctor.getName()+"_update");
        this.mockMvc.perform(
                patch("/doctors/"+doctor.getId())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void doctorDelete() throws Exception {
        Doctor doctor = this.doctorJpaRepository.findAll().get(0);
        this.mockMvc.perform(
                delete("/doctors/" + doctor.getId())
                        .contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isOk());
    }

    public ResultHandler createDoctorResultHandler(LinkDescriptor... linkDescriptors) {
        return this.document.snippets(
                links(linkDescriptors),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("의사아이디"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("의사명"),
                    fieldWithPath("_links").type(JsonFieldType.OBJECT).description("의사정보")));
    }

}
