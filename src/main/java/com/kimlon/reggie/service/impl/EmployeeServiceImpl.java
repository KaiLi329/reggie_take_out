package com.kimlon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kimlon.reggie.entity.Employee;
import com.kimlon.reggie.mapper.EmployeeMapper;
import com.kimlon.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
