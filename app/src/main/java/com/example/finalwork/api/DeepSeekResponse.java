package com.example.finalwork.api;

import java.util.List;

public class DeepSeekResponse {
    public List<Choice> choices;

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String role;
        public String content;
    }

    public String getFirstContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).message != null) {
            return choices.get(0).message.content;
        }
        return "AI 暂时没有返回内容。";
    }
}
