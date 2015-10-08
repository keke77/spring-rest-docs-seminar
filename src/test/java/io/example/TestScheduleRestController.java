package io.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.example.doctor.Doctor;
import io.example.doctor.DoctorJpaRepository;
import io.example.schedule.Schedule;
import io.example.schedule.ScheduleJpaRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
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
public class TestScheduleRestController extends TestBootConfig  {

    @Autowired
    private DoctorJpaRepository doctorJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void scheduleShowAll() throws Exception {
        this.mockMvc.perform(get("/schedules?page=2&size=10"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        links(
                                linkWithRel("next").optional().description("--"),
                                linkWithRel("prev").optional().description("--"),
                                linkWithRel("self").description("---")),
                        responseFields(
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("<<resources-schedules-show-all-links,schedules>> Resources"),
                                fieldWithPath("_embedded.schedules").type(JsonFieldType.OBJECT).description("<<resource-schedules-show-one, Schedule>> Resources"),
                                fieldWithPath("page").type(JsonFieldType.OBJECT).description("Information On <<overview-pagination, Pagination>>"))));
    }

    @Test
    public void scheduleShowOne() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        this.mockMvc.perform(get("/schedules/"+schedule.getId()))
                .andExpect(status().isOk())
                .andDo(createScheduleResultHandler(
                        linkWithRel("self").description("---"),
                        linkWithRel("doctor").description("---"),
                        linkWithRel("patient").description("---")));
    }

    @Test
    public void scheduleCreate() throws Exception {
        Doctor doctor = this.doctorJpaRepository.findAll().get(0);
        Map<String, String> create = Maps.newHashMap();
        create.put("appointmentDay", "9991-12-31");
        create.put("startTime", "09:00");
        create.put("endTime", "09:30");
        create.put("doctorId", Long.toString(doctor.getId()));
        this.mockMvc.perform(
                put("/schedules")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(create)))
                .andExpect(header().string("Location", notNullValue()))
                .andDo(this.document.snippets(responseHeaders(
                        headerWithName("Location").description("신규 생성된 자원 주소"))))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void scheduleUpdate() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        Map<String, String> update = Maps.newHashMap();
        update.put("startTime", "10:00");
        update.put("endTime", "10:30");
        this.mockMvc.perform(
                patch("/schedules/"+schedule.getId())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void scheduleDelete() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        this.mockMvc.perform(
                delete("/schedules/" + schedule.getId())
                        .contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isOk());
    }

    public ResultHandler createScheduleResultHandler(LinkDescriptor... linkDescriptors) {
        return this.document.snippets(
                links(linkDescriptors),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스케쥴아이디"),
                        fieldWithPath("appointmentDay").type(JsonFieldType.STRING).description("진료일"),
                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("진료시작시간"),
                        fieldWithPath("endTime").type(JsonFieldType.STRING).description("진료종료시간"),
                        fieldWithPath("doctor").type(JsonFieldType.OBJECT).description("의사정보"),
                        fieldWithPath("patient").type(JsonFieldType.OBJECT).description("환자정보"),
                        fieldWithPath("_links").type(JsonFieldType.OBJECT).description("스케쥴정보")));
    }

}
