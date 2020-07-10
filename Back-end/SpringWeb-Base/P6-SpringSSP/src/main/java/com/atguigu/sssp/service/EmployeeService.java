package com.atguigu.sssp.service;

import com.atguigu.sssp.entity.Employee;
import com.atguigu.sssp.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Employee get(Integer id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Employee employee) {
        if (employee.getId() == null) {
            employee.setCreateTime(new Date());
        }
        employeeRepository.saveAndFlush(employee);
    }

    @Transactional(readOnly = true)
    public Employee getByLastName(String lastName) {
        return employeeRepository.getByLastName(lastName);
    }

    @Transactional(readOnly = true)
    public Page<Employee> getPage(int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize);
        return employeeRepository.findAll(pageable);
    }

}
