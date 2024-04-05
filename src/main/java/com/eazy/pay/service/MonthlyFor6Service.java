package com.eazy.pay.service;

import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.dao.MonthlyFor6Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonthlyFor6Service {

    @Autowired
    private MonthlyFor6Repository monthlyFor6Repository;

    public List<MonthlyFor6> getAllMonthlyFor6() {
        return monthlyFor6Repository.findAll();
    }
}
