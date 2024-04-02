package launchhacks3.hackathon.util;

import launchhacks3.hackathon.model.util.Conversation;
import launchhacks3.hackathon.model.util.Message;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GptClient {
	private final String apiKey;
	private final String apiUri;
	private final Conversation conversation;
	private final String responseFormat;
	private final String model;


	public GptClient(String apiKey, String apiUri, String responseFormat, String model) {
		this.apiKey = apiKey;
		this.apiUri = apiUri;
		this.responseFormat = responseFormat;
		this.model = model;

		this.conversation = new Conversation();
	}

	public void addMessage(String role, String content) {
		var message = new Message(role, content);
		conversation.addMessage(message);
	}

	public String completeConversation() {
		var restTemplate = new RestTemplate();

		var header = new HttpHeaders();
		header.add("Authorization", "Bearer " + this.apiKey);    // Set the Authorization header
		header.setContentType(MediaType.APPLICATION_JSON);    // Set the media type header

		String body = GptUtils.createRequestBody(this.conversation, this.responseFormat, this.model);

		var request = new HttpEntity<>(body, header);

		ResponseEntity<String> response = restTemplate.postForEntity(apiUri, request, String.class);	// Chat Completions API call

		System.out.println(response);

		return GptUtils.parseGptResponse(response.getBody());
	}
}
