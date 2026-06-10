package com.example.finalwork.api;

import java.util.ArrayList;
import java.util.List;

public class DeepSeekRequest {
    public String model = "deepseek-chat";
    public boolean stream = true;
    public List<Message> messages = new ArrayList<>();

    /** 无历史，单次请求 */
    public DeepSeekRequest(String userText) {
        messages.add(new Message("system",
                "你是 CampusAI 智慧校园学习助手，擅长学习规划、作业答疑、课程知识解释和简历优化。请用简洁清晰的中文回答。"));
        messages.add(new Message("user", userText));
    }

    /** 带对话历史 */
    public DeepSeekRequest(List<Message> history, String currentUserText) {
        messages.add(new Message("system",
                "你是 CampusAI 智慧校园学习助手，擅长学习规划、作业答疑、课程知识解释和简历优化。请用简洁清晰的中文回答。"));
        messages.addAll(history);
        messages.add(new Message("user", currentUserText));
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
