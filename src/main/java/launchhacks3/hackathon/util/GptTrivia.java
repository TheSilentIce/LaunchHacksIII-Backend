package launchhacks3.hackathon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import launchhacks3.hackathon.model.TriviaQuestion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class GptTrivia {

	private static final String GPT_INSTRUCTIONS_MCQ = "Your role is to generate a multiple choice geography trivia question based on the user's prompt.";
	private static final String GPT_INSTRUCTIONS_LOCATION = "Your role is to generate a geography trivia question based on the user's prompt.";
	private static final String GPT_INSTRUCTIONS_FORMAT = "You MUST use JSON FORMAT. Do not include option letters/numbers, ONLY the VALUES. \n example: {\"question\": \"1+1?\", \"choices\": [\"2\", \"9\", \"3\"], \"correct\": \"2\"}";
	private static final String GPT_MODEL = "gpt-3.5-turbo-0125";

	@Value("${openai.api.key}")
	private String OPENAI_API_KEY;

	@Value("${openai.api.uri}")
	private String OPENAI_CHAT_URI;

	public TriviaQuestion getQuestion(String query) {
		return parseResponse(getGptResponse(query));
	}

	/**
	 * Method which creates and sends a request to the OpenAI Chat Completions API.
	 *
	 * @param query User query
	 * @return GPT response
	 */
	private String getGptResponse(String query) {
		var restTemplate = new RestTemplate();

		var header = new HttpHeaders();
		header.add("Authorization", "Bearer " + OPENAI_API_KEY);    // Set the Authorization header
		header.setContentType(MediaType.APPLICATION_JSON);    // Set the media type header

		String body = createRequestBody(query);    // Create the request body

		var request = new HttpEntity<>(body, header);

		System.out.println(request);

		ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_CHAT_URI, request, String.class);	// Chat Completions API call

		var response_ = """
				<200 OK OK,{
				  "id": "chatcmpl-99NunUgnf4iQA4nQsRrxRT0QHq0gk",
				  "object": "chat.completion",
				  "created": 1712023909,
				  "model": "gpt-3.5-turbo-0125",
				  "choices": [
				    {
				      "index": 0,
				      "message": {
				        "role": "assistant",
				        "content": "{\\n  \\"question\\": \\"Which of the following countries was NOT part of the British Empire during World War II?\\",\\n  \\"choices\\": [\\"India\\", \\"Canada\\", \\"Australia\\", \\"Italy\\"]\\n}"
				      },
				      "logprobs": null,
				      "finish_reason": "stop"
				    }
				  ],
				  "usage": {
				    "prompt_tokens": 45,
				    "completion_tokens": 40,
				    "total_tokens": 85
				  },
				  "system_fingerprint": "fp_b28b39ffa8"
				}
				""";

		System.out.println(response);

		return response.toString().replace("<200 OK OK,", "");
	}

	private TriviaQuestion parseResponse(String response) {
		var mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();

		try {
			rootNode = mapper.readTree(response);	// Read response tree
		} catch (JsonProcessingException ignored) {}

		var messageNode = rootNode.path("choices").get(0).path("message").get("content");	// Find response message content

		JsonNode triviaNode = mapper.createObjectNode();

		try {
			triviaNode = mapper.readTree(messageNode.textValue());
		} catch (JsonProcessingException ignored) {}

		System.out.println(triviaNode);
		System.out.println(triviaNode.get("question"));
		System.out.println(triviaNode.get("choices"));

		return new TriviaQuestion(triviaNode.get("question").textValue(), mapper.convertValue(triviaNode.get("choices"), List.class), triviaNode.get("correct").textValue());
	}

	/**
	 * Helper method which constructs a request body to send to the GPT.
	 * Sets the system message using the instructions and appends a list of relevant sponsor information.
	 *
	 * @param query User query
	 * @return Request body to send to GPT
	 */
	private String createRequestBody(String query) {
		var mapper = new ObjectMapper();
		var body = mapper.createObjectNode();
		var messages = mapper.createArrayNode();

		var systemMessage = mapper.createObjectNode();	// The system message containing instructions
		systemMessage.put("role", "system");
		systemMessage.put("content", GPT_INSTRUCTIONS_MCQ);

		var userMessage = mapper.createObjectNode();	// The user query
		userMessage.put("role", "user");
		userMessage.put("content", query);

		var formatMessage = mapper.createObjectNode();
		formatMessage.put("role", "system");
		formatMessage.put("content", GPT_INSTRUCTIONS_FORMAT);

		var responseFormat = mapper.createObjectNode();
		responseFormat.put("type", "json_object");

		messages.addAll(Arrays.asList(systemMessage, userMessage, formatMessage));

		body.put("model", GPT_MODEL);	// Set the GPT model to use
		body.set("response_format", responseFormat);
		body.set("messages", messages);

		return body.toString();
	}
}
