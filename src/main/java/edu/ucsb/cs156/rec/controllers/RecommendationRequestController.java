package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.models.CurrentUser;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RecommendationRequest")
@RequestMapping("/api/recommendationrequest")
@RestController
public class RecommendationRequestController extends ApiController {
    @Autowired
    RecommendationRequestRepository recommendationRequestRepository;

    /**
     * This method returns a list of all recommendation requests requested by current student.
     * @return a list of all recommendation requests requested by the current user
     */
    @Operation(summary = "List all recommendation requests requested by current user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/requester/all")
    public Iterable<RecommendationRequest> allRequesterRecommendationRequests(
    ) {
        // toyed with having this only be ROLE_STUDENT but I think even professors should be able to submit requests so they can see which ones they have submitted too
        User currentUser = getCurrentUser().getUser();
        Iterable<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAllByRequesterId(currentUser.getId());
        return recommendationRequests;
    }

    /**
     * This method returns a list of all recommendation requests intended for current user who is a professor.
     * @return a list of all recommendation requests intended for the current user who is a professor
     */
    @Operation(summary = "List all recommendation requests for professor")
    @PreAuthorize("hasRole('ROLE_PROFESSOR')")
    @GetMapping("/professor/all")
    public Iterable<RecommendationRequest> allProfessorRecommendationRequests(
    ) {
        User currentUser = getCurrentUser().getUser();
        Iterable<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAllByRequesterId(currentUser.getId());
        return recommendationRequests;
    }

    /**
     * This method returns a single recommendationrequests.
     * @param id id of the recommendationrequests to get
     * @return a single recommendationrequests
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
     * This method creates a new recommendationrequests. Accessible only to users with the role "ROLE_ADMIN" until our roles can be configured.
     * @param professorName professor name of request
     * @param professorEmail professor email of request
     * @param recommendationTypes recommendation types of request
     * @param details details of request
     * @param submissionDate submission date of request
     * @return the save recommendationrequests (with it's id field set by the database)
     */
    @Operation(summary = "Create a new recommendationrequests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendationRequests(
            @Parameter(name = "professorName") @RequestParam String professorName,
            @Parameter(name = "professorEmail") @RequestParam String professorEmail,
            @Parameter(name = "recommendationTypes") @RequestParam String recommendationTypes,
            @Parameter(name = "details") @RequestParam String details,
            @Parameter(name = "submissionDate") @RequestParam LocalDateTime submissionDate)
            {
        //get current date right now and set status to pending
        CurrentUser currentUser = getCurrentUser();
        RecommendationRequest recommendationRequest = new RecommendationRequest();

        recommendationRequest.setProfessorName(professorName);
        recommendationRequest.setProfessorEmail(professorEmail);
        recommendationRequest.setRequesterName(currentUser.getUser().getFullName());
        recommendationRequest.setUser(currentUser.getUser());
        recommendationRequest.setRecommendationTypes(recommendationTypes);
        recommendationRequest.setDetails(details);
        recommendationRequest.setStatus("PENDING");
        // ideally would like this to just directly call LocalDateTime.now() cannot properly mock test this behavior with full coverage
        recommendationRequest.setSubmissionDate(submissionDate);

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        return savedRecommendationRequest;
    }
}
