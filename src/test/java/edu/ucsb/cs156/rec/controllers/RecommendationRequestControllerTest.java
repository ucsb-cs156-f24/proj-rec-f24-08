package edu.ucsb.cs156.rec.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTest extends ControllerTestCase {
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/recommendationrequests/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().is(200)); // logged in as user can get all
    }

    // Authorization tests for /api/recommendationrequests

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/recommendationrequests?id=7"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/recommendationrequests/post

    @Test
    public void logged_out_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
        mockMvc.perform(post("/api/recommendationrequests/post"))
                .andExpect(status().is(403)); // only admins can post
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        // arrange
        LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .professorName("Conrad")
                .professorEmail("phtcon@ucsb.edu")
                .requesterName("Amanda")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(ldt)
                .completionDate(ldt)
                .build();

        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest));

        // act
        MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(recommendationRequest);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        // arrange
        when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/recommendationrequests?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests() throws Exception {

        // arrange
        RecommendationRequest rec1 = RecommendationRequest.builder()
                .professorName("Conrad")
                .professorEmail("phtcon@ucsb.edu")
                .requesterName("Amanda")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        RecommendationRequest rec2 = RecommendationRequest.builder()
                .professorName("Singh")
                .professorEmail("ambuj@cs.ucsb.edu")
                .requesterName("Sara")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        ArrayList<RecommendationRequest> expectedRecommendationRequests = new ArrayList<>();
        expectedRecommendationRequests.addAll(Arrays.asList(rec1, rec2));

        when(recommendationRequestRepository.findAll()).thenReturn(expectedRecommendationRequests);

        // act
        MvcResult response = mockMvc.perform(get("/api/recommendationrequests/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRecommendationRequests);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recommendation_request() throws Exception {
        // arrange
        RecommendationRequest rec1 = RecommendationRequest.builder()
                .professorName("Conrad")
                .professorEmail("phtcon@ucsb.edu")
                .requesterName("Amanda")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        when(recommendationRequestRepository.save(eq(rec1))).thenReturn(rec1);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/recommendationrequests/post?professorName=Conrad&professorEmail=phtcon@ucsb.edu&requesterName=Amanda&recommendationTypes=rec type&details=details&status=status&submissionDate=2022-01-03T00:00:00&completionDate=2022-01-03T00:00:00")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).save(rec1);
        String expectedJson = mapper.writeValueAsString(rec1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_recommendation_request() throws Exception {
        // arrange
        RecommendationRequest recReq = RecommendationRequest.builder()
                .id(15L)
                .professorName("Conrad")
                .professorEmail("phtcon@ucsb.edu")
                .requesterName("Amanda")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(recReq));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/recommendationrequests?id=15")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(15L);
        verify(recommendationRequestRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("RecommendationRequest with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_recommendation_request_and_gets_right_error_message()
            throws Exception {
        // arrange

        when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/recommendationrequests?id=15")
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_recommendation_request() throws Exception {
        // arrange

        RecommendationRequest recReqOrig = RecommendationRequest.builder().id(67L)
                .professorName("Conrad")
                .professorEmail("phtconrad@ucsb.edu")
                .requesterName("Amanda")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        RecommendationRequest recReqEdited = RecommendationRequest.builder().id(67L)
                .professorName("Singh")
                .professorEmail("ambuj@cs.ucsb.edu")
                .requesterName("Sara")
                .recommendationTypes("new rec type")
                .details("new details")
                .status("new status")
                .submissionDate(LocalDateTime.parse("2022-02-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-02-03T00:00:00"))
                .build();

        String requestBody = mapper.writeValueAsString(recReqEdited);

        when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(recReqOrig));

        // act
        MvcResult response = mockMvc.perform(
                put("/api/recommendationrequests?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(67L);
        verify(recommendationRequestRepository, times(1)).save(recReqEdited); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_recommendation_request_that_does_not_exist() throws Exception {
        // arrange

        RecommendationRequest editedRecReq = RecommendationRequest.builder()
                .professorName("Hollerer")
                .professorEmail("holl@cs.ucsb.edu")
                .requesterName("Erik")
                .recommendationTypes("rec type")
                .details("details")
                .status("status")
                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                .build();

        String requestBody = mapper.writeValueAsString(editedRecReq);

        when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/recommendationrequests?id=67")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).findById(67L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("RecommendationRequest with id 67 not found", json.get("message"));

    }
}
