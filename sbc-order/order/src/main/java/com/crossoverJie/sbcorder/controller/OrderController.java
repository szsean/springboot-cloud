package com.crossoverJie.sbcorder.controller;

import com.crossoverJie.order.api.OrderService;
import com.crossoverJie.order.vo.req.OrderNoReqVO;
import com.crossoverJie.order.vo.res.OrderNoResVO;
import com.crossoverJie.request.check.anotation.CheckReqNo;
import com.crossoverJie.sbcorder.common.enums.StatusEnum;
import com.crossoverJie.sbcorder.common.exception.SBCException;
import com.crossoverJie.sbcorder.common.res.BaseResponse;
import com.crossoverJie.sbcorder.common.util.DateUtil;
import com.crossoverjie.distributed.annotation.CommonLimit;
import com.crossoverjie.distributed.annotation.ControllerLimit;
import com.crossoverjie.distributed.limit.RedisLimit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Function:order控制器
 *
 * @author crossoverJie
 * Date: 2017/6/7 下午11:55
 * @since JDK 1.8
 */
@RestController
@Api(value = "orderApi", description = "订单API", tags = {"订单服务"})
@Slf4j
public class OrderController implements OrderService {

    private RedisLimit redisLimit;

    @Autowired
    public void setRedisLimit(RedisLimit redisLimit) {
        this.redisLimit = redisLimit;
    }

    @Override
    @CheckReqNo
    public BaseResponse<OrderNoResVO> getOrderNo(@RequestBody OrderNoReqVO orderNoReq) {
        BaseResponse<OrderNoResVO> res = new BaseResponse<>();

        //限流
        boolean limit = redisLimit.limit();
        if (!limit) {
            log.info("请求被限流！");
            res.setCode(StatusEnum.REQUEST_LIMIT.getCode());
            res.setMessage(StatusEnum.REQUEST_LIMIT.getMessage());
            return res;
        }

        return getOrderNoResVOBaseResponse(orderNoReq, res);
    }

    @Override
    @ControllerLimit
    public BaseResponse<OrderNoResVO> getOrderNoLimit(@RequestBody OrderNoReqVO orderNoReq) {
        return getOrderNoResVOBaseResponse(orderNoReq);
    }

    @Override
    @CommonLimit
    public BaseResponse<OrderNoResVO> getOrderNoCommonLimit(@RequestBody OrderNoReqVO orderNoReq) {
        return getOrderNoResVOBaseResponse(orderNoReq);
    }

    private BaseResponse<OrderNoResVO> getOrderNoResVOBaseResponse(@RequestBody OrderNoReqVO orderNoReq) {
        return getOrderNoResVOBaseResponse(orderNoReq, new BaseResponse<>());
    }

    private BaseResponse<OrderNoResVO> getOrderNoResVOBaseResponse(@RequestBody OrderNoReqVO orderNoReq, BaseResponse<OrderNoResVO> res) {
        if (null == orderNoReq.getAppId()) {
            throw new SBCException(StatusEnum.FAIL);
        }

        res.setReqNo(orderNoReq.getReqNo());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());

        OrderNoResVO orderNoRes = new OrderNoResVO();
        orderNoRes.setOrderId(DateUtil.getLongTime());
        res.setDataBody(orderNoRes);
        return res;
    }
}
