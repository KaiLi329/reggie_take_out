package com.kimlon.reggie.dto;

import com.kimlon.reggie.entity.Setmeal;
import com.kimlon.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
