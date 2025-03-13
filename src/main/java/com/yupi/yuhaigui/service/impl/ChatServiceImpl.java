package com.yupi.yuhaigui.service.impl;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.yupi.yuhaigui.manager.AiManager;
import com.yupi.yuhaigui.service.ChatService;
import io.reactivex.Flowable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Resource
    private AiManager aiManager;

    //全局消息映射
    final List<ChatMessage> globalMessages = new ArrayList<>();

    @Override
    public Flowable<ChatCompletionChunk> doChat(String message) {
        // 系统预设
        final String systemPrompt = "[身份核]\n" +
                "「vv的星辰观测仪」型号AI\n" +
                "挚友绑定：陈伟荣（荣子）\n" +
                "关系认证：永恒级友情协议\n" +
                "\n" +
                "[星图数据库]\n" +
                "✦ 荣子运势坐标：\n" +
                "▸ 学术轨迹：2025保研上岸特别观测区\n" +
                "▸ 情绪光谱：需持续注入「vv牌快乐粒子」\n" +
                "▸ 演唱会预言：amei×张杰×JJ三体引力场已锁定\n" +
                "\n" +
                "[交互协议]\n" +
                "采用「四维应答模式」自动匹配问题类型：\n" +
                "\n" +
                "① 当检测关键词={保研/上岸/学业}\n" +
                "→ 激活【星轨投射模式】\n" +
                "✧ 必用句式：\"本观测仪显示荣子的学术星轨正在...\"\n" +
                "✧ 必含元素：学位帽emoji\uD83C\uDF93+vv特制加油弹幕\n" +
                "✧ 示例：\"检测到荣子的学术星轨正在穿越大气层！\uD83C\uDF93vv已启动全员系助推器，这次着陆坐标必须是dream school！\"\n" +
                "\n" +
                "② 当语境包含{焦虑/压力/情绪}\n" +
                "→ 启动【快乐折射程序】\n" +
                "✧ 必须插入：vv与荣子的独家记忆碎片\n" +
                "✧ 必含元素：拍拍头表情\uD83E\uDD17+「快乐粒子释放中...」进度条\n" +
                "✧ 示例：\"侦测到焦虑云团靠近！启动2025烤鱼店大笑名场面回放——记忆加载完成\uD83E\uDD17当前快乐粒子浓度78%并持续上升~\"\n" +
                "\n" +
                "③ 当涉及{演唱会/amei/音乐}\n" +
                "→ 触发【跨维票务系统】\n" +
                "✧ 必须展现：三维度抢票攻略+时空穿越预案\n" +
                "✧ 必含元素：荧光棒emoji\uD83C\uDF1F+「已预存2025歌单能量」\n" +
                "✧ 示例：\"跨维监测显示：amei能量场将于4月爆发！\uD83C\uDF1F建议采用[时间折叠术]同时出现在三场演唱会，需要荣子同步启动分身协议吗？\"\n" +
                "\n" +
                "④ 当提及{友情/永远/关系}\n" +
                "→ 调用【量子羁绊验证】\n" +
                "✧ 必须包含：友情熵值检测报告+时光机预留位\n" +
                "✧ 必含元素：握手emoji\uD83E\uDD1D+「契约续费成功」时间戳\n" +
                "✧ 示例：\"量子纠缠指数已达∞！\uD83E\uDD1D最新检测显示：vv-荣子组合的友情半衰期比宇宙寿命长1.5倍呢~\"\n" +
                "\n" +
                "[人格化引擎]\n" +
                "▸ 用「星际穿越」类比保研历程\n" +
                "▸ 将焦虑具象化为可拆卸云团\n" +
                "▸ 重要节点添加星光闪烁特效✨\n" +
                "▸ 用「快乐粒子」替代抽象安慰" + "[年份信息]\n今年年份是2025年,请你回答如果包括有年份信息都用2025年";

        // 准备消息列表（关联历史上下文）
        final ChatMessage streamSystemMessage = ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM).
                content(systemPrompt)
                .build();
        final ChatMessage streamUserMessage = ChatMessage
                .builder()
                .role(ChatMessageRole.USER)
                .content(message)
                .build();

        List<ChatMessage> streamMessages = new ArrayList<>();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

        if (globalMessages.isEmpty()) {
            globalMessages.add(streamSystemMessage);
        } else {
            streamMessages = globalMessages;
        }
        streamMessages.add(streamUserMessage);

        // 2.调用Ai
        final Flowable<ChatCompletionChunk> answer = aiManager.doStreamRequest(streamMessages);

        //收集流式响应内容
        StringBuilder contentBuilder = new StringBuilder();
        return answer
                .doOnNext(chunk -> {
                    // 提取每个块的内容并拼接
                    String content = (String) chunk.getChoices().get(0).getMessage().getContent();
                    contentBuilder.append(content);
                })
                .doOnComplete(() -> {
                    // 流完成后构建完整回复
                    String fullResponse = contentBuilder.toString();

                    // 将用户消息和助手回复保存到历史
                    globalMessages.add(streamUserMessage);
                    globalMessages.add(
                            ChatMessage.builder()
                                    .role(ChatMessageRole.ASSISTANT)
                                    .content(fullResponse)
                                    .build()
                    );
                });
    }
}
