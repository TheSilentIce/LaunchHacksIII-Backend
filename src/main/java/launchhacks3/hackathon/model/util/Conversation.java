package launchhacks3.hackathon.model.util;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Conversation {
	private final LinkedList<Message> messages;

	public Conversation() {
		this.messages = new LinkedList<>();
	}

	public void addMessage(Message message) {
		messages.add(message);
	}
}
