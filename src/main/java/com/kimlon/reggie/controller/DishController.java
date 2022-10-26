package com.kimlon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kimlon.reggie.common.R;
import com.kimlon.reggie.dto.DishDto;
import com.kimlon.reggie.entity.Category;
import com.kimlon.reggie.entity.Dish;
import com.kimlon.reggie.entity.DishFlavor;
import com.kimlon.reggie.service.CategoryService;
import com.kimlon.reggie.service.DishFlavorService;
import com.kimlon.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页器对象
        Page<Dish> pageInfo = new Page< >(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 构造条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, dishLambdaQueryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            // 根据Id查询分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品及其信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询相应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//
//        // 构造查询条件对象
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 根据条件查询相应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        // 构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            // 根据Id查询分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);

            // 当前菜品id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> query = new LambdaQueryWrapper<>();
            query.eq(DishFlavor::getDishId, id);
            List<DishFlavor> flavors = dishFlavorService.list(query);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
