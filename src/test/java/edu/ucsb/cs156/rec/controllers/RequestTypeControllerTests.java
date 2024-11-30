package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RequestTypeController.class)
@Import(TestConfig.class)
public class RequestTypeControllerTests extends ControllerTestCase {

        @MockBean
        RequestTypeRepository requestTypeRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/requesttypes/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/requesttypes/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Authorization tests for /api/requesttypes/put

        @Test
        public void logged_out_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/requesttypes/put"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/requesttypes/put"))
                                .andExpect(status().is(403)); // only admins can edit/put
        }

        // Authorization tests for /api/requesttypes/delete
        @Test
        public void logged_out_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/requesttypes/delete"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/requesttypes/delete"))
                                .andExpect(status().is(403)); // only admins can delete
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                RequestType requestType = RequestType.builder()
                                .requestType("Internship")
                                .build();

                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.of(requestType));

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(requestType);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange
                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RequestType with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_requesttypes() throws Exception {

                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestType2 = RequestType.builder()
                                .requestType("Research Lab")
                                .build();

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestType2));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Research")
                                .build();

                when(requestTypeRepository.save(eq(requestType1))).thenReturn(requestType1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Research")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).save(requestType1);
                String expectedJson = mapper.writeValueAsString(requestType1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_cannot_post_a_duplicate_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();
                
                RequestType requestType2 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestType2));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Internship")
                                                .with(csrf()))
                                .andExpect(status().is(400)).andReturn();


                verify(requestTypeRepository, times(1)).findAll();
                Map<String, Object> json = responseToJson(response);
                assertEquals("IllegalArgumentException", json.get("type"));
                assertEquals("Duplicate request type: Internship", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_non_duplicate_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();
                
                RequestType requestType2 = RequestType.builder()
                                .requestType("Research")
                                .build();


                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                when(requestTypeRepository.save(eq(requestType2))).thenReturn(requestType2);
                

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Research")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();


                verify(requestTypeRepository, times(1)).findAll();
                verify(requestTypeRepository, times(1)).save(requestType2);
                String expectedJson = mapper.writeValueAsString(requestType2);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Fellowship")
                                .build();

                when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.of(requestType1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/requesttypes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(15L);
                verify(requestTypeRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("Request type with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_requesttype_and_gets_right_error_message()
                        throws Exception {
                
                // arrange
                when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/requesttypes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_requesttype() throws Exception {
                
                // arrange
                RequestType requestTypeOrig = RequestType.builder()
                                .requestType("Pre School")
                                .build();

                RequestType requestTypeEdited = RequestType.builder()
                                .requestType("Grad School")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(67L);
                verify(requestTypeRepository, times(1)).save(requestTypeEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_a_duplicate_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestTypeOrig = RequestType.builder()
                                .requestType("Grad School")
                                .build();

                RequestType requestTypeEdited = RequestType.builder()
                                .id(67)
                                .requestType("Internship")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestTypeOrig));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().is(400)).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findAll();
                Map<String, Object> json = responseToJson(response);
                assertEquals("IllegalArgumentException", json.get("type"));
                assertEquals("Duplicate request type: RequestType(id=67, requestType=Internship)", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_a_non_duplicate_requesttype() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestTypeOrig = RequestType.builder()
                                .id(67)
                                .requestType("Grad School")
                                .build();

                RequestType requestTypeEdited = RequestType.builder()
                                .id(67)
                                .requestType("Research")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestTypeOrig));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().is(200)).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(67L);
                verify(requestTypeRepository, times(1)).save(requestTypeEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void requesttype_can_duplicate_itself() throws Exception {
                
                // arrange
                RequestType requestTypeOrig = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestTypeEdited = RequestType.builder()
                                .requestType("Internship")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(67L);
                verify(requestTypeRepository, times(1)).save(requestTypeEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_cannot_put_an_empty_requesttype() throws Exception {
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestTypeOrig = RequestType.builder()
                                .requestType("Grad School")
                                .build();

                RequestType requestTypeEdited = RequestType.builder()
                                .id(67)
                                .requestType("")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestTypeOrig));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().is(400)).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findAll();
                Map<String, Object> json = responseToJson(response);
                assertEquals("IllegalArgumentException", json.get("type"));
                assertEquals("Request type cannot be empty", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void non_duplicates_do_not_throw_exception() throws Exception {
                
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .requestType("PhD")
                                .build();

                RequestType requestType2 = RequestType.builder()
                                .requestType("Masters")
                                .build();

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestType2));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findAll();
                String responseString = response.getResponse().getContentAsString();
                assertEquals("[{\"id\":0,\"requestType\":\"PhD\"},{\"id\":0,\"requestType\":\"Masters\"}]", responseString);

        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_requesttype_that_does_not_exist() throws Exception {
                
                // arrange
                RequestType requestTypeEdited = RequestType.builder()
                                .requestType("PhD")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 67 not found", json.get("message"));
        }

}