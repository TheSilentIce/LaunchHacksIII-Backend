package launchhacks3.hackathon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import launchhacks3.hackathon.model.TriviaQuestion;
import launchhacks3.hackathon.model.User;
import launchhacks3.hackathon.repository.TriviaRepository;
import launchhacks3.hackathon.repository.UserRepository;
import launchhacks3.hackathon.util.GptClient;
import launchhacks3.hackathon.util.GptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TriviaService {
	private static final String INSTRUCTIONS_MCQ = "Your role is to generate a multiple choice geography trivia question based on the user's prompt.";
	private static final String MCQ_FORMAT = "You MUST use JSON FORMAT. Do not include option letters/numbers, ONLY the VALUES. \n example: {\"question\": \"1+1?\", \"choices\": [\"2\", \"9\", \"3\"], \"correct\": \"2\"}";

	private static final String INSTRUCTIONS_LOCATION = "Your role is to generate a trivia question to test someone's knowledge of geography.";
	private static final String MAP_FORMAT = "You MUST use JSON FORMAT. Do NOT include OPTIONS, only the question and correct answer. The correct answer should only be a city. Be sure to also use cities who are not necessarily well known. \n example: {\"question\": \"where is the capital of India?\", \"correct\": \"New Delhi\"}";

	private static final String INSTRUCTIONS_QUESTIONS = "Your role is to generate geography related trivia questions.";
	private static final String QUESTIONS_FORMAT = "You MUST use JSON FORMAT. Generate the number of questions the user requests. Only include \"question\", \"choices\", and \"correct\" fields";

	private static final String GPT_MODEL = "gpt-3.5-turbo-0125";

	@Value("${openai.api.key}")
	private String apiKey;

	@Value("${openai.api.uri}")
	private String apiUri;

	@Autowired private TriviaRepository triviaRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private MongoTemplate mongoTemplate;

	public void addUser(String username, String password, int score) {
		User user = new User(username,password, score);
		userRepository.save(user);
	}

	public User getUser(String username) {
		return userRepository.findByUsername(username);
	}

	public List<User> getLeaderboard() {
		return userRepository.findAllByOrderByScoreDesc();
	}

	public void updateLeaderboard(String username, int score) {
		var user = userRepository.findByUsername(username);

		if (user != null) {
			userRepository.delete(user);
		} else {
			user.setScore(score);
//			user = new User(username, score);
		}

		user.setScore(score);

		userRepository.save(user);
	}

	public TriviaQuestion getTriviaMultipleChoiceQuestion(String query) {
		var gptClient = new GptClient(apiKey, apiUri, GptUtils.JSON_FORMAT, GPT_MODEL);

		gptClient.addMessage(GptUtils.SYSTEM_ROLE, INSTRUCTIONS_MCQ);
		gptClient.addMessage(GptUtils.SYSTEM_ROLE, MCQ_FORMAT);
		gptClient.addMessage(GptUtils.USER_ROLE, query);

		var gptMessage = gptClient.completeConversation();

		var mapper = new ObjectMapper();
		var question = new TriviaQuestion();

		try {
			question = mapper.readValue(gptMessage, TriviaQuestion.class);
		} catch (JsonProcessingException ignored) {}

		return triviaRepository.save(question);
	}

	public TriviaQuestion getTriviaMapQuestion(String query) {
		var gptClient = new GptClient(apiKey, apiUri, GptUtils.JSON_FORMAT, GPT_MODEL);

		gptClient.addMessage(GptUtils.SYSTEM_ROLE, INSTRUCTIONS_LOCATION);
		gptClient.addMessage(GptUtils.SYSTEM_ROLE, MAP_FORMAT);
		gptClient.addMessage(GptUtils.USER_ROLE, "theme: " + query);

		var gptMessage = gptClient.completeConversation();

		var mapper = new ObjectMapper();
		var question = new TriviaQuestion();

		try {
			question = mapper.readValue(gptMessage, TriviaQuestion.class);
		} catch (JsonProcessingException ignored) {}

		return triviaRepository.save(question);
	}

	public List<TriviaQuestion> getNewQuestions(int amount) {
		var gptClient = new GptClient(apiKey, apiUri, GptUtils.JSON_FORMAT, GPT_MODEL);

		gptClient.addMessage(GptUtils.SYSTEM_ROLE, INSTRUCTIONS_QUESTIONS);
		gptClient.addMessage(GptUtils.SYSTEM_ROLE, QUESTIONS_FORMAT);
		gptClient.addMessage(GptUtils.USER_ROLE, "amount: " + amount);

		var gptResponse = gptClient.completeConversation();
		var gptResponse_ = """
				{
						"questions": [
								{
										"question": "Which is the longest river in the world?",
										"choices": ["Amazon River", "Nile River", "Yangtze River", "Mississippi River"],
										"correct": "Nile River"
								},
								{
										"question": "Which country is known as the 'Land of the Rising Sun'?",
										"choices": ["China", "India", "Japan", "South Korea"],
										"correct": "Japan"
								}
						]
				}""";

		var mapper = new ObjectMapper();
		JsonNode questionsNode;
		var questions = new ArrayList<TriviaQuestion>();

		try {
			questionsNode = mapper.readTree(gptResponse).get("questions");

			for (var node : questionsNode) {
				var question = mapper.readValue(node.toString(), TriviaQuestion.class);
				questions.add(question);
				triviaRepository.save(question);
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return questions;
	}
}
