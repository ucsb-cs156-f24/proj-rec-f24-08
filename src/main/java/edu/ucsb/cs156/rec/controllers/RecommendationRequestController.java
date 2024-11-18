package edu.ucsb.cs156.rec.controllers;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.UCSBDate;
import edu.ucsb.cs156.rec.errors.EntityNotFoundException;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

public class RecommendationRequestController {
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
        RecommendationRequest recommendationRequest = RecommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendationRequest;
    }


    /**
     * Create a new recommendation request
     * 
     * @param quarterYYYYQ  the quarter in the format YYYYQ
     * @param name          the name of the date
     * @param localDateTime the date
     * @return the saved ucsbdate
     */
    @Operation(summary= "Create a new date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDate postUCSBDate(
            @Parameter(name="quarterYYYYQ") @RequestParam String quarterYYYYQ,
            @Parameter(name="name") @RequestParam String name,
            @Parameter(name="localDateTime", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("localDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", localDateTime);

        UCSBDate ucsbDate = new UCSBDate();
        ucsbDate.setQuarterYYYYQ(quarterYYYYQ);
        ucsbDate.setName(name);
        ucsbDate.setLocalDateTime(localDateTime);

        UCSBDate savedUcsbDate = ucsbDateRepository.save(ucsbDate);

        return savedUcsbDate;
    }




    /**
     * Delete a UCSBDate
     * 
     * @param id the id of the date to delete
     * @return a message indicating the date was deleted
     */
    @Operation(summary= "Delete a UCSBDate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteUCSBDate(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDate ucsbDate = ucsbDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

        ucsbDateRepository.delete(ucsbDate);
        return genericMessage("UCSBDate with id %s deleted".formatted(id));
    }



    /**
     * Update a single date
     * 
     * @param id       id of the date to update
     * @param incoming the new date
     * @return the updated date object
     */
    @Operation(summary= "Update a single date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDate updateUCSBDate(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBDate incoming) {

        UCSBDate ucsbDate = ucsbDateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

        ucsbDate.setQuarterYYYYQ(incoming.getQuarterYYYYQ());
        ucsbDate.setName(incoming.getName());
        ucsbDate.setLocalDateTime(incoming.getLocalDateTime());

        ucsbDateRepository.save(ucsbDate);

        return ucsbDate;
    }
}

