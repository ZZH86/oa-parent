package com.ch.wechat.service;

/**
 * @Author hui cao
 * @ClassName: MessageService
 * @Description:
 * @Date: 2023/6/1 10:42
 * @Version: v1.0
 */
public interface MessageService {

    /**
     * 推送待审批人员
     * @param processId
     * @param userId
     * @param taskId
     */
    void pushPendingMessage(Long processId, Long userId, String taskId);

    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status);
}
