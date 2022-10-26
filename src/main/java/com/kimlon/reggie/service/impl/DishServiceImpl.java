package com.kimlon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.kimlon.reggie.dto.DishDto;
import com.kimlon.reggie.entity.Dish;
import com.kimlon.reggie.entity.DishFlavor;
import com.kimlon.reggie.mapper.DishMapper;
import com.kimlon.reggie.service.DishFlavorService;
import com.kimlon.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据，操作两张表：dish、dish_flavor
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基本信息
        this.save(dishDto);

        Long id = dishDto.getId(); // 对应菜品id

        // 保存菜品口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id获取菜品及其口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        // 查询菜品基本信息，dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询口味信息，dish_flavor查询
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品，及其口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据---dish_flavor的delete
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        // 添加当前提交过来的口味数据---dish_flavor表的Insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
           item.setDishId(dishDto.getId());
           return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
