package com.ch.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author hui cao
 * @ClassName: WechatAccountConfig
 * @Description:
 * @Date: 2023/5/27 18:27
 * @Version: v1.0
 */

@Data
@Component
//读取配置文件的内容
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {
    private String mpAppId;

    private String mpAppSecret;
}
