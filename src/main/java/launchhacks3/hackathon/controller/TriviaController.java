package launchhacks3.hackathon.controller;

import launchhacks3.hackathon.model.TriviaQuestion;
import launchhacks3.hackathon.model.User;
import launchhacks3.hackathon.service.TriviaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "v1/trivia/")
@CrossOrigin
public class TriviaController {
	@Autowired private TriviaService service;

	@GetMapping("/mcq/{query}")
	public TriviaQuestion getMultipleChoiceQuestion(@PathVariable String query) {
		return service.getTriviaMultipleChoiceQuestion(query);
	}

	@GetMapping("/map/{query}")
	public TriviaQuestion getMapQuestion(@PathVariable String query) {
		return service.getTriviaMapQuestion(query);
	}

	@GetMapping("/questions/new/{amount}")
	public List<TriviaQuestion> getNewQuestions(@PathVariable int amount) {
		return service.getNewQuestions(amount);
	}
	@GetMapping("/leaderboard/all")
	public List<User> getLeaderboard() {
		return service.getLeaderboard();
	}

	@PostMapping("/leaderboard/new/{username}:{score}")
	public void updateLeaderboard(@PathVariable String username, @PathVariable int score) {
		service.updateLeaderboard(username, score);
	}
}
