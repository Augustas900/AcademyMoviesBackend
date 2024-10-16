package lt.ca.javau10;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, ObjectId> {
    List<Review> findByImdbId(String imdbId);
}
