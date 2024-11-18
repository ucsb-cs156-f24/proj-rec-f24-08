package edu.ucsb.cs156.rec.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "RecommendationRequests")
@RequestMapping("/api/recommendationrequests")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController {

    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * Get a single recommendation request by id
     * 
     * @param id the id of the recommendation request
     * @return a recommendation request
     */
    @Operation(summary = "Get a single recommendation request")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name = "id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }

    /**
     * Create a new recommendation request
     * 
     * @param professorName       the name of the professor
     * @param professorEmail      the email of the professor
     * @param requesterName       the name of the requester
     * @param recommendationTypes the types of recommendations
     * @param details             the details of the request
     * @param status              the status of the request
     * @param submissionDate      the submission date of the request
     * @param completionDate      the completion date of the request
     * @return the saved recommendation request
     */

    @Operation(summary = "Create a new recommendation request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequest(
            @Parameter(name = "professorName") @RequestParam String professorName,
            @Parameter(name = "professorEmail") @RequestParam String professorEmail,
            @Parameter(name = "requesterName") @RequestParam String requesterName,
            @Parameter(name = "recommendationTypes") @RequestParam String recommendationTypes,
            @Parameter(name = "details") @RequestParam String details,
            @Parameter(name = "status") @RequestParam String status,
            @Parameter(name = "submissionDate", description = "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("submissionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime submissionDate,
            @Parameter(name = "completionDate", description = "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("completionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime completionDate)
            throws JsonProcessingException {
        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", submissionDate);
        log.info("localDateTime={}", completionDate);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setProfessorName(professorName);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setRequesterName(requesterName);
        recommendationRequest.setRecommendationTypes(recommendationTypes);
        recommendationRequest.setDetails(details);
        recommendationRequest.setStatus(status);
        recommendationRequest.setSubmissionDate(submissionDate);
        recommendationRequest.setCompletionDate(completionDate);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return savedRecommendationRequest;
    }

    /**
     * Delete a RecommendationRequest
     * 
     * @param id the id of the RecommendationRequest to delete
     * @return a message indicating that the RecommendationRequest was deleted
     */
    @Operation(summary = "Delete a RecommendationRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendationRequest(
            @Parameter(name = "id") @RequestParam Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequestRepository.delete(recommendationRequest);
        return genericMessage("Recommendation Request with id %s deleted".formatted(id));
    }

    /**
     * Update a single Recommendation Request
     * 
     * @param id       the id of the Recommendation Request to update
     * @param incoming the updated Recommendation Request
     * @return the updated Recommendation Request object
     */
    @Operation(summary = "Update a single date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequest updateRecommendationRequest(
            @Parameter(name = "id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRequest.setProfessorName(incoming.getProfessorName());
        recommendationRequest.setProfessorEmail(incoming.getProfessorEmail());
        recommendationRequest.setRequesterName(incoming.getRequesterName());
        recommendationRequest.setRecommendationTypes(incoming.getRecommendationTypes());
        recommendationRequest.setDetails(incoming.getDetails());
        recommendationRequest.setStatus(incoming.getStatus());
        recommendationRequest.setSubmissionDate(incoming.getSubmissionDate());
        recommendationRequest.setCompletionDate(incoming.getCompletionDate());

        recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest;
    }

    /**
     * This method returns a list of all recommendation requests
     * 
     * @return a list of all recommendation requests
     */
    @Operation(summary = "List all recommendation requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> getAll() {
        return recommendationRequestRepository.findAll();
    }
}
