package me.xueyao.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Simon.Xue
 * @date 2019-10-23 17:46
 **/
@Data
@Accessors(chain = true)
public class UnifiedOrderDto implements Serializable {

    /**
     * PC网页或公众号内支付可以传"WEB" 门店号
     */
    private String deviceInfo;
    /**
     * 商品简单描述
     */
    private String body;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单总金额，单位为分
     */
    private BigDecimal totalFee;

    /**
     * 用户ip
     */
    private String spbillCreateIp;

    /**
     * 用户openId
     */
    private String openId;


}
