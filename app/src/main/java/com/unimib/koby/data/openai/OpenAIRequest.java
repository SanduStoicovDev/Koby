package com.unimib.koby.data.openai;
import java.util.List;

public class OpenAIRequest {
    public String model = "gpt-4o";
    public List<Message> messages;

    public static class Message {
        public String role;
        public String content;
        public Message(String role,String content){this.role=role;this.content=content;}
    }
}