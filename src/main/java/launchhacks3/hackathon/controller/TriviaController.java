package launchhacks3.hackathon.controller;

import launchhacks3.hackathon.model.TriviaQuestion;
import launchhacks3.hackathon.util.GptTrivia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "v1/trivia/")
public class TriviaController {
	@Autowired private GptTrivia gptTrivia;

	@GetMapping("/mcq/{query}")
	public TriviaQuestion getQuestion(@PathVariable String query) {
		return gptTrivia.getQuestion(query);
	}
}
