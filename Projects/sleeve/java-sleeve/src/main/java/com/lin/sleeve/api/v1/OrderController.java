package com.lin.sleeve.api.v1;

import com.lin.sleeve.bo.PagerCounter;
import com.lin.sleeve.core.LocalUser;
import com.lin.sleeve.core.interceptors.ScopeLevel;
import com.lin.sleeve.dto.OrderDTO;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.logic.OrderChecker;
import com.lin.sleeve.model.Order;
import com.lin.sleeve.service.OrderService;
import com.lin.sleeve.util.CommonUtil;
import com.lin.sleeve.vo.OrderIdVO;
import com.lin.sleeve.vo.OrderPureVO;
import com.lin.sleeve.vo.OrderSimplifyVO;
import com.lin.sleeve.vo.PagingDozer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 14:27
 */
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/order")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Value("${sleeve.order.pay-time-limit}")
    private Long payTimeLimit;

    /**
     * 下单
     */
    @PostMapping("")
    @ScopeLevel
    public OrderIdVO placeOrder(@Validated @RequestBody OrderDTO orderDTO) {
        //step 1：信息校验
        Long uid = LocalUser.getUser().getId();
        OrderChecker orderChecker = orderService.isOK(uid, orderDTO);
        //step 2：下单【即写入数据库】
        Long orderId = orderService.placeOrder(uid, orderDTO, orderChecker);
        //step3：返回结果
        return new OrderIdVO(orderId);
    }

    /**
     * 获取待支付的订单
     */
    @GetMapping("/status/unpaid")
    @ScopeLevel
    public PagingDozer getUnpaidSimplifyList(
            @RequestParam(name = "start", defaultValue = "0") Integer start,
            @RequestParam(name = "count", defaultValue = "10") Integer count
    ) {

        PagerCounter pagerCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Order> unpaid = orderService.getUnpaid(pagerCounter.getPage(), pagerCounter.getCount());

        PagingDozer<Order, OrderSimplifyVO> pagingDozer = new PagingDozer<>(unpaid, OrderSimplifyVO.class);
        pagingDozer.getItems().forEach(o -> ((OrderSimplifyVO) o).setPeriod(payTimeLimit));

        return pagingDozer;
    }

    /**
     * 获取特定状态的订单，支持：ALL, PAID, DELIVERED, FINISHED 状态。
     */
    @GetMapping("/by/status/{status}")
    @ScopeLevel
    public PagingDozer getByStatus(
            @RequestParam(name = "start", defaultValue = "0") Integer start,
            @RequestParam(name = "count", defaultValue = "10") Integer count,
            @PathVariable("status") Integer status
    ) {

        PagerCounter pagerCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Order> byStatus = orderService.getByStatus(status, pagerCounter.getPage(), pagerCounter.getCount());

        PagingDozer<Order, OrderSimplifyVO> pagingDozer = new PagingDozer<>(byStatus, OrderSimplifyVO.class);
        pagingDozer.getItems().forEach(o -> ((OrderSimplifyVO) o).setPeriod(payTimeLimit));

        return pagingDozer;
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{id}")
    @ScopeLevel
    public OrderPureVO getOrderDetail(@PathVariable("id") Long id) {
        Order order = orderService.getOrderDetail(id).orElseThrow(() -> new NotFoundException(ExceptionCodes.C_50009));
        return new OrderPureVO(order, payTimeLimit);
    }

}