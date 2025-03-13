package com.yupi.yuhaigui.manager;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import io.reactivex.Flowable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiManager {
    @Resource
    private ArkService arkService;

    /**
     * 聊天（只允许传递系统预设和用户预设）
     * @param systemPrompt
     * @param userPrompt
     * @return
     */
    public Flowable<ChatCompletionChunk> doStreamRequest(String systemPrompt, String userPrompt) {
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM).
                content(systemPrompt)
                .build();
        final ChatMessage streamUserMessage = ChatMessage
                .builder()
                .role(ChatMessageRole.USER)
                .content(userPrompt)
                .build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
                .model("ep-20250312143919-m7n9j")
                .messages(streamMessages)
                .build();

        return arkService.streamChatCompletion(streamChatCompletionRequest);
    }

    /**
     * 更通用的方法，运行用户传入任意条消息列表
     * @param streamMessagesList
     * @return
     */
    public Flowable<ChatCompletionChunk> doStreamRequest(List<ChatMessage> streamMessagesList) {
        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
                .model("ep-20250312143919-m7n9j")
                .messages(streamMessagesList)
                .build();
        return arkService.streamChatCompletion(streamChatCompletionRequest);
    }
}
