package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.TransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferServiceImpl implements TransferService {

    @Value("${app.security.max-transfer-amount:250000}")
    private BigDecimal maxTransferAmount;

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransferMapper transferMapper;

    @Override
    @Transactional
    public TransferDto createTransfer(CreateTransferRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден")
        );
        Card fromCard = cardRepository.findById(request.getFromCardId()).orElseThrow(
                () -> new CardNotFoundException("Карта отправителя не найдена")
        );

        Card toCard = cardRepository.findById(request.getToCardId()).orElseThrow(
                () -> new CardNotFoundException("Карта получателя не найдена")
        );

        if (!fromCard.getOwner().equals(currentUser) || !toCard.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Переводы доступны только между своими картами");
        }

        if (request.getAmount().compareTo(maxTransferAmount) > 0) {
            throw new IllegalArgumentException("Максимальная сумма перевода: " + maxTransferAmount);
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0){
            throw new IllegalArgumentException("Недостаточно средств для перевода");
        }

        if (toCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Карта отправителя неактивна");
        }

        if (fromCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Карта получателя неактивна");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .owner(currentUser)
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .transferStatus(TransferStatus.COMPLETED)
                .build();

        transferRepository.save(transfer);

        return transferMapper.toTransferDto(transfer);
    }

    @Override
    public Page<TransferDto> getAllTransfers(Pageable pageable) {
        return transferRepository.findAll(pageable).map(transferMapper::toTransferDto);
    }

    @Override
    public Page<TransferDto> getMyTransfers(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Пользователь " + username + " не найден")
        );
        return transferRepository.findAllByOwner(currentUser, pageable).map(transferMapper::toTransferDto);
    }
}
