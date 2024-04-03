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
	private String password;
	private Integer score;


	public User (String username, String password, int score) {
		this.username = username;
		this.score = score;
		this.password = password;

		this.id = String.valueOf(new ObjectId());
	}
}
