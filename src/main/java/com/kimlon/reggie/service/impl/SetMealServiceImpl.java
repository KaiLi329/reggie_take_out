package com.kimlon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kimlon.reggie.common.CustomException;
import com.kimlon.reggie.dto.SetmealDto;
import com.kimlon.reggie.entity.Setmeal;
import com.kimlon.reggie.entity.SetmealDish;
import com.kimlon.reggie.mapper.SetMealMapper;
import com.kimlon.reggie.service.SetMealDishService;
import com.kimlon.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐，同时保存套餐及菜品
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息,setmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());

        // 保存套餐菜品信息,setmeal_dish
        setMealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐及关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);

        // 如果不能删除，抛出业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 如果可以删除，则先删除套餐表中的数据
        this.removeByIds(ids);

        // 删除关联表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setMealDishService.remove(queryWrapper1);
    }
}
