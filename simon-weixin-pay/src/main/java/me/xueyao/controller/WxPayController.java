package me.xueyao.controller;

import lombok.extern.slf4j.Slf4j;
import me.xueyao.entity.UnifiedOrderDto;
import me.xueyao.util.WxPayApi;
import me.xueyao.util.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon.Xue
 * @date 2020-04-05 21:39
 **/
@Slf4j
@RestController
@RequestMapping("/wxPay")
public class WxPayController {

    @Autowired
    private WxPayApi wxPayApi;

    /**
     * 统一下单
     * @return
     * @throws Exception
     */
    @PostMapping("/createOrder")
    public Map<String, String> createOrder() throws Exception {
        UnifiedOrderDto unifiedOrderDto = new UnifiedOrderDto();
        unifiedOrderDto.setOpenId("ol3yX5EtZNrt3fGuOmMoXORg9T-E");
        unifiedOrderDto.setDeviceInfo("WEB");
        unifiedOrderDto.setOutTradeNo("O1234567890");
        unifiedOrderDto.setSpbillCreateIp("127.0.0.1");
        unifiedOrderDto.setBody("这个是商品内容");
        unifiedOrderDto.setTotalFee(BigDecimal.valueOf(10));
        String result = wxPayApi.unifiedOrder(unifiedOrderDto);
        if (StringUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }
        log.info("prepayId = {}", result);
        return wxPayApi.getClientPrepayMap(result);
    }


    /**
     * 微信回调
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/callback")
    public String callBack(HttpServletRequest request) throws Exception {
        String result = wxPayApi.payCallBack(request);
        log.info("微信回调结果 = {}", result);
        Map<String, String> resultMap = new HashMap<>(16);
        resultMap.put("return_code", "SUCCESS");
        resultMap.put("return_msg", "OK");
        String resultXml = WxUtil.mapToXml(resultMap);
        return resultXml;
    }
}
