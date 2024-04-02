package launchhacks3.hackathon.repository;

import launchhacks3.hackathon.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
	User findByUsername(String username);
	List<User> findAllByOrderByScoreDesc();
}
