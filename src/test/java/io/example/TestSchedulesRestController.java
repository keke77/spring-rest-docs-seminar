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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* Created by gmind on 2015-10-06.
*/
public class TestSchedulesRestController extends TestBootConfig  {

    @Autowired
    private DoctorJpaRepository doctorJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void schedulesShowAll() throws Exception {
        this.mockMvc.perform(get("/schedules").param("page","2").param("size", "10"))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        links(
                                linkWithRel("next").optional().description("다음페이지"),
                                linkWithRel("prev").optional().description("이전페이지"),
                                linkWithRel("self").description("현재페이지")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("리스트 사이즈")),
                        responseFields(
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("<<resources-schedules-show-all-links,Schedules>> Resources"),
                                fieldWithPath("_embedded.schedules").type(JsonFieldType.OBJECT).description("<<resource-schedules-show-one, Schedule>> Resources").optional(),
                                fieldWithPath("page").type(JsonFieldType.OBJECT).description("Information On <<overview-pagination, Pagination>>"))));
    }

    @Test
    public void schedulesShowOne() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        this.mockMvc.perform(get("/schedules/{id}", schedule.getId()))
                .andExpect(status().isOk())
                .andDo(createSchedulesResultHandler(
                        linkWithRel("self").description("현재정보링크"),
                        linkWithRel("doctor").description("의사정보"),
                        linkWithRel("patient").optional().description("환자정보")));
    }

    @Test
    public void schedulesCreate() throws Exception {
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
                .andDo(this.document.snippets(
                        requestFields(
                                fieldWithPath("appointmentDay").type(JsonFieldType.STRING).description("진료일(yyyy-MM-DD)"),
                                fieldWithPath("startTime").type(JsonFieldType.STRING).description("진료시작시간(HH:mm)"),
                                fieldWithPath("endTime").type(JsonFieldType.STRING).description("진료종료시간(HH:mm)"),
                                fieldWithPath("doctorId").type(JsonFieldType.STRING).description("의사아이디")),
                        responseHeaders(
                            headerWithName("Location").description("신규 생성된 자원 주소"))))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    public void schedulesUpdate() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        Map<String, String> update = Maps.newHashMap();
        update.put("startTime", "10:00");
        update.put("endTime", "10:30");
        this.mockMvc.perform(
                patch("/schedules/{id}", schedule.getId())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(update)))
                .andDo(this.document.snippets(
                        requestFields(
                                fieldWithPath("startTime").type(JsonFieldType.STRING).description("진료시작시간(HH:mm)"),
                                fieldWithPath("endTime").type(JsonFieldType.STRING).description("진료종료시간(HH:mm)")),
                        pathParameters(parameterWithName("id").description("스케쥴아이디"))))
                .andExpect(status().isNoContent());
    }

    @Test
    public void schedulesDelete() throws Exception {
        Schedule schedule = this.scheduleJpaRepository.findAll().get(0);
        this.mockMvc.perform(
                delete("/schedules/{id}", schedule.getId())
                        .contentType(MediaTypes.HAL_JSON))
                .andDo(this.document.snippets(pathParameters(parameterWithName("id").description("스케쥴아이디"))))
                .andExpect(status().isOk());
    }

    public ResultHandler createSchedulesResultHandler(LinkDescriptor... linkDescriptors) {
        return this.document.snippets(
                links(linkDescriptors),
                pathParameters(
                        parameterWithName("id").description("스케쥴아이디")),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스케쥴아이디"),
                        fieldWithPath("appointmentDay").type(JsonFieldType.STRING).description("진료일"),
                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("진료시작시간"),
                        fieldWithPath("endTime").type(JsonFieldType.STRING).description("진료종료시간"),
                        fieldWithPath("_links").type(JsonFieldType.OBJECT).description("스케쥴정보")));
    }

}
