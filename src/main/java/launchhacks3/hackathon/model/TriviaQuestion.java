package launchhacks3.hackathon.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "trivia")
public class TriviaQuestion {
	@Id
	private String id;
	private String question;
	private List<String> choices;
	private String correct;
}
