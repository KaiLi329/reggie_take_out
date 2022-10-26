package com.kimlon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kimlon.reggie.dto.SetmealDto;
import com.kimlon.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
