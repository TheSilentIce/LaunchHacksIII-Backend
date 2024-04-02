package launchhacks3.hackathon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TriviaQuestion {
	private String question;
	private List<String> answer;
	private String correct;
}
