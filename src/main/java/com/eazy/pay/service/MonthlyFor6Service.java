package com.eazy.pay.service;

import com.eazy.pay.dao.MonthlyFor6StatisticRepository;
import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.mapper.MonthlyFor6ResponseMapper;
import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.model.MonthlyFor6Statistic;
import com.eazy.pay.dao.MonthlyFor6Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;

@Service
public class MonthlyFor6Service {

    @Autowired
    private MonthlyFor6Repository monthlyFor6Repository;

    @Autowired
    private MonthlyFor6StatisticRepository monthlyFor6StatisticRepository;


    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByAgeAndDate(Integer age, Date date) {

        age = age / 5 * 5;
        if(age != 0) {
            age = min(age, 20);
        }
        age = max(age, 85);

        return monthlyFor6StatisticRepository.findByAgeAndDate(age, date).stream()
                .filter(MonthlyFor6Statistic -> MonthlyFor6Statistic.getUseAmount() > 0)
                .sorted(Comparator.comparingInt(MonthlyFor6Statistic::getUseAmount).reversed())
                .map(MonthlyFor6ResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserIdAndDate(Long userId, Date date) {

        return monthlyFor6Repository.findByUserIdAndDate(userId, date).stream()
                .filter(monthlyFor6 -> monthlyFor6.getUseAmount() > 0)
                .sorted(Comparator.comparingInt(MonthlyFor6::getUseAmount).reversed())
                .map(MonthlyFor6ResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
}
