package edu.ucsb.cs156.rec.repositories;

import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The RecommendationRequestRepository is a repository for RecommendationRequest entities.
 */

@Repository
public interface RecommendationRequestRepository extends CrudRepository<RecommendationRequest, Long> {
    Optional<RecommendationRequest> findByIdAndRequester(long id, User requester_id);
}