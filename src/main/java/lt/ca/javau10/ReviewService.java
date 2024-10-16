package lt.ca.javau10;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.DuplicateKeyException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional
    public Review createReview(String reviewBody, String imdbId) {
        // Create new review object
        Review review = new Review(reviewBody, imdbId, LocalDateTime.now(), LocalDateTime.now());

        // Insert the review into the reviews collection
        try {
            reviewRepository.insert(review);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Review with the same ID already exists.");
        }

        // Update the movie document to link the review
        Update update = new Update().push("reviewIds").value(review.getId());
        var updateResult = mongoTemplate.update(Movie.class)
            .matching(Criteria.where("imdbId").is(imdbId))
            .apply(update)
            .first();

        if (updateResult.getMatchedCount() == 0) {
            throw new IllegalArgumentException("No movie found with the given IMDb ID: " + imdbId);
        }

        return review;
    }

    // Method to fetch reviews by movieId
    public List<Review> getReviewsByMovieId(String imdbId) {
        // Find all reviews linked to the specified movie
        return reviewRepository.findByImdbId(imdbId);
    }
}
