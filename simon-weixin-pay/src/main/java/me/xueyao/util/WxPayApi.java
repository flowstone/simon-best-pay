package me.xueyao.util;

import lombok.extern.slf4j.Slf4j;
import me.xueyao.config.WxPayConfig;
import me.xueyao.constants.WxConstants;
import me.xueyao.entity.UnifiedOrderDto;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信支付api
 *
 * @author qxw
 * 2018年3月1日
 */
@Slf4j
@Component
public class WxPayApi {

    @Autowired
    private WxPayConfig wxPayConfig;


    /**
     * 统一下单返回预支付id
     *
     * @param
     * @param orderDto 统一下单对象
     * @return prepayId  预支付id
     * @throws Exception
     */
    public String unifiedOrder(UnifiedOrderDto orderDto) throws Exception {
        Map<String, String> map = new HashMap<>(16);
        map.put("appid", wxPayConfig.getAppId());
        //商户号
        map.put("mch_id", wxPayConfig.getMchId());
        //PC网页或公众号内支付可以传"WEB" 门店号
        map.put("device_info", orderDto.getDeviceInfo());
        //随机字符串长度要求在32位以内
        map.put("nonce_str", WxUtil.generateNonceStr());
        //商品简单描述
        map.put("body", orderDto.getBody());
        //商户订单号
        map.put("out_trade_no", orderDto.getOutTradeNo());
        //订单总金额，单位为分
        map.put("total_fee", orderDto.getTotalFee().intValue() + "");
        //APP和网页支付提交用户端ip Native支付填调用微信支付API的机器IP
        map.put("spbill_create_ip", orderDto.getSpbillCreateIp());
        //订单回调地址
        map.put("notify_url", wxPayConfig.getNotifyUrl());
        //交易类型   JSAPI 公众号支付  NATIVE 扫码支付  APP APP支付
        map.put("trade_type", WxConstants.JSAPI);
        map.put("openid", orderDto.getOpenId());

        map.put("sign", WxUtil.generateSignature(map, wxPayConfig.getMchKey()));



        String dataXML = WxUtil.mapToXml(map);
        String resultXMlStr = urlPost(wxPayConfig.getUnifiedOrderUrl(), dataXML);
        log.info("统一下单返回结果-----------------   " + resultXMlStr);
        try {
            Map<String, String> result = WxUtil.xmlToMap(resultXMlStr);
            String prepayId = result.get("prepay_id");
            if (StringUtils.isEmpty(prepayId)) {
                return null;
            } else {
                return prepayId;
            }
        } catch (Exception e) {
            throw new Exception("微信服务器超时");
        }



    }





    /**
     * 根据预支付id 生成包含所有必须参数的map对象 返回给前端JsSDK使用
     *
     * @param prepayId
     * @return
     * @throws Exception
     */
    public Map<String, String> getClientPrepayMap(String prepayId) throws Exception {
        Map<String, String> map = new HashMap<>(16);
        map.put("appId", wxPayConfig.getAppId());
        map.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("nonceStr", WxUtil.generateNonceStr());
        map.put("package", "prepay_id=" + prepayId);
        map.put("signType", "MD5");
        String sign = WxUtil.generateSignature(map, wxPayConfig.getMchKey());
        map.put("paySign", sign);
        return map;
    }


    /**
     * 微信支付回调结果参数解析  接收通知成功必须通知微信成功接收通知
     *
     * @param request
     * @return
     * @throws Exception
     */
    public String payCallBack(HttpServletRequest request) throws Exception {
        // 读取参数
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();

        return sb.toString();

    }

    @SuppressWarnings("deprecation")
    public static String urlPost(String url, String postBody) throws Exception {
        PostMethod post = new PostMethod(url);
        HttpClient httpClient = new HttpClient();
        post.getParams().setContentCharset("utf-8");
        post.setRequestBody(postBody);
        httpClient.executeMethod(post);
        return post.getResponseBodyAsString();
    }

}
