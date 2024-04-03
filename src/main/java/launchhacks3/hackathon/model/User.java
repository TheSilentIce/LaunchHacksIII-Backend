package launchhacks3.hackathon.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leaderboard")

@Getter
@Setter
public class User {
	@Id
	private String id;
	private String username;
	private Integer score;

	public User (String username, int score) {
		this.username = username;
		this.score = score;

		this.id = String.valueOf(new ObjectId());
	}
}
