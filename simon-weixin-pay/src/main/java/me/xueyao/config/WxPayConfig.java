package me.xueyao.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Simon.Xue
 * @date 2020-04-05 20:41
 **/
@Data
@Configuration
public class WxPayConfig {

    @Value("${wx.appId}")
    private String appId;

    @Value("${wx.secret}")
    private String secret;

    @Value("${wx.unifiedOrderUrl}")
    private String unifiedOrderUrl;

    @Value("${wx.mchId}")
    private String mchId;

    @Value("${wx.mchKey}")
    private String mchKey;


    @Value("${wx.notifyUrl}")
    private String notifyUrl;
}
