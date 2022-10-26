package com.kimlon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kimlon.reggie.common.BaseContext;
import com.kimlon.reggie.common.R;
import com.kimlon.reggie.entity.ShoppingCart;
import com.kimlon.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());

        // 设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 查询菜品是否已存在于购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one != null) {
            // 如果已存在，则叠加数量
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            // 如果不存在，则添加到购物车
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);

    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());
        Long currentId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            // 菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one.getNumber() > 1) {
            // 如果大于1个菜品
            Integer number = one.getNumber();
            one.setNumber(number - 1);
            shoppingCartService.updateById(one);
        } else {
            // 只有一个
            shoppingCartService.removeById(one);
        }


        return R.success("成功");
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
