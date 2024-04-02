package launchhacks3.hackathon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import launchhacks3.hackathon.model.util.Conversation;

public class GptUtils {
	public static final String USER_ROLE = "user";
	public static final String SYSTEM_ROLE = "system";

	public static final String TEXT_FORMAT = "text";
	public static final String JSON_FORMAT = "json_object";

	public static String createRequestBody(Conversation conversation, String responseFormat, String gptModel) {
		var mapper = new ObjectMapper();
		var body = mapper.createObjectNode();
		var messages = mapper.createArrayNode();

		for (var message : conversation.getMessages()) {
			var messageNode = mapper.createObjectNode();
			messageNode.put("role", message.getRole());
			messageNode.put("content", message.getContent());
			messages.add(messageNode);
		}

		body.set("messages", messages);

		var responseFormatNode = mapper.createObjectNode();
		responseFormatNode.put("type", responseFormat);

		body.set("response_format", responseFormatNode);

		body.put("model", gptModel);

		return body.toString();
	}

	public static String parseGptResponse(String response) {
		var mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();

		try {
			rootNode = mapper.readTree(response);	// Read response tree
		} catch (JsonProcessingException e) {
			System.err.println("Malformed Response"); // TODO throw actual exception
		}

		var messageNode = rootNode.path("choices").get(0).path("message").get("content");	// Find response message content

		return messageNode.textValue();
	}

}
