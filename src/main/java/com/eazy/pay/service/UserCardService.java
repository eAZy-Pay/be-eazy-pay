package com.eazy.pay.service;

import com.eazy.pay.model.Card;
import com.eazy.pay.model.User;
import com.eazy.pay.model.UserCard;
import com.eazy.pay.dao.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCardService {

    @Autowired
    private UserCardRepository userCardRepository;

    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
    }

    // UserId로 자신의 보유 카드 모두가져오기
    public List<UserCard> getUserCardsByUserId(Long userId) {return userCardRepository.findByUserId(userId);}
    // 자신의 보유 카드 4개까지만 가져오기
    public List<UserCard> getUserCardsByUserIdWithLimit4(Long userId){return  userCardRepository.findByUserIdLimit4(userId);}
}
