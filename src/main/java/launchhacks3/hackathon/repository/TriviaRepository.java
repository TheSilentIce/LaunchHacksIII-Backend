package launchhacks3.hackathon.repository;

import launchhacks3.hackathon.model.TriviaQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaRepository extends MongoRepository<TriviaQuestion, String> {
}
