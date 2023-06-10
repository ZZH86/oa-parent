package com.ch.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ch.model.process.ProcessRecord;

/**
 * @Author hui cao
 * @ClassName: OaProcessRecoedService
 * @Description:
 * @Date: 2023/5/14 14:54
 * @Version: v1.0
 */

public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description);
}
