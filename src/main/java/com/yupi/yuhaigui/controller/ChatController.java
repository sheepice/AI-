package com.yupi.yuhaigui.controller;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import com.yupi.yuhaigui.manager.AiManager;
import com.yupi.yuhaigui.service.ChatService;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

@RestController
public class ChatController {
    @Resource
    private AiManager aiManager;

    @Resource
    private ChatService chatService;
    @GetMapping("ai_generate/sse")
    public SseEmitter steamChat(@RequestParam String message) {
        //创建SSE连接对象
        SseEmitter sseEmitter = new SseEmitter(0L);
        //得到流
        Flowable<ChatCompletionChunk> chatCompletionChunkFlowable = chatService.doChat(message);

        chatCompletionChunkFlowable
                .observeOn(Schedulers.io())
                .map(chunk -> chunk.getChoices().get(0).getMessage().getContent())
                .doOnNext(sseEmitter::send)
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;
    }
}
