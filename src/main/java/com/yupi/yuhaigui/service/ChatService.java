package com.yupi.yuhaigui.service;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;

public interface ChatService {
    /**
     * 和AI对话
     * @param message 用户自己输入的消息
     * @return
     */
    Flowable<ChatCompletionChunk> doChat(String message);
}
