package com.kimlon.reggie.controller;

import com.kimlon.reggie.common.R;
import com.kimlon.reggie.entity.Orders;
import com.kimlon.reggie.service.OrdersService;
import com.kimlon.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;


    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info(orders.toString());
        ordersService.submit(orders);
        return R.success("下单成功");
    }
}
