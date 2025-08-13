package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByOwnerAndCardStatus(User owner, CardStatus cardStatus, Pageable pageable);

    Page<Card> findAllByOwner(User owner, Pageable pageable);

    Page<Card> findAllByCardStatus(CardStatus cardStatus, Pageable pageable);
}
