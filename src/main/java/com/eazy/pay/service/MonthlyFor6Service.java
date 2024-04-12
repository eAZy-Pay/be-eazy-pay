package com.eazy.pay.service;

import com.eazy.pay.dto.MonthlyFor6ResponseDTO;
import com.eazy.pay.mapper.MonthlyFor6ResponseMapper;
import com.eazy.pay.model.MonthlyFor6;
import com.eazy.pay.dao.MonthlyFor6Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonthlyFor6Service {

    @Autowired
    private MonthlyFor6Repository monthlyFor6Repository;


    public List<MonthlyFor6ResponseDTO> getMonthlyFor6ByUserId(Long userId) {
        List<MonthlyFor6ResponseDTO> monthlyFor6ResponseDTOList = monthlyFor6Repository.findByUserUid(userId).stream()
                .filter(monthlyFor6 -> monthlyFor6.getUseAmount() > 0)
                .sorted(Comparator.comparingInt(MonthlyFor6::getUseAmount).reversed())
                .map(MonthlyFor6ResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

        return monthlyFor6ResponseDTOList;
    }
}
