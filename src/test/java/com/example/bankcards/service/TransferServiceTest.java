package com.example.bankcards.service;

import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessToResourceDeniedException;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.TransferServiceImpl;
import com.example.bankcards.util.TransferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transferService, "maxTransferAmount", BigDecimal.valueOf(250000));

        user = User.builder().id(1L).username("test").build();

        fromCard = Card.builder()
                .id(1L)
                .owner(user)
                .balance(BigDecimal.valueOf(3500))
                .cardStatus(CardStatus.ACTIVE)
                .expiryDate(LocalDateTime.now().plusYears(5))
                .build();

        toCard = Card.builder()
                .id(2L)
                .owner(user)
                .balance(BigDecimal.valueOf(5200))
                .cardStatus(CardStatus.ACTIVE)
                .expiryDate(LocalDateTime.now().plusYears(5))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );
    }

    @Test
    void createTransfer_shouldCreateTransfer() {
        CreateTransferRequest request = CreateTransferRequest.builder()
                .amount(BigDecimal.valueOf(500))
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .build();

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(fromCard.getId())).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(toCard.getId())).thenReturn(Optional.of(toCard));
        when(transferMapper.toTransferDto(any(Transfer.class))).thenReturn(TransferDto.builder().amount(BigDecimal.valueOf(350)).build());

        TransferDto result = transferService.createTransfer(request);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(350));
        assertThat(fromCard.getBalance()).isEqualTo(BigDecimal.valueOf(3000));
        assertThat(toCard.getBalance()).isEqualTo(BigDecimal.valueOf(5700));
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void createTransfer_shouldThrowUserNotFoundException() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(400))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowCardNotFoundExceptionIfFromCardIdIsNull() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .amount(BigDecimal.valueOf(400))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowCardNotFoundExceptionIfToCardIdIsNull() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(400))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowAccessToResourceDeniedExceptionIfNotOwner() {
        User other = User.builder().id(2L).username("test2").build();
        toCard.setOwner(other);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(AccessToResourceDeniedException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(400))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowBadRequestExceptionIfAmountIsMoreThanMax() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(BadRequestException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(300000))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowBadRequestExceptionIfAmountIsMoreThanBalance() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(BadRequestException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(30000))
                        .build()));
    }

    @Test
    void createTransfer_shouldThrowBadRequestExceptionIfCardIsBlocked() {
        toCard.setCardStatus(CardStatus.BLOCKED);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(BadRequestException.class,
                () -> transferService.createTransfer(CreateTransferRequest.builder()
                        .fromCardId(1L)
                        .toCardId(2L)
                        .amount(BigDecimal.valueOf(300))
                        .build()));
    }

    @Test
    void getAllTransfers_shouldReturnData() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Transfer> page = new PageImpl<>(List.of(new Transfer()));

        when(transferRepository.findAll(pageable)).thenReturn(page);
        when(transferMapper.toTransferDto(any(Transfer.class))).thenReturn(TransferDto.builder().build());

        Page<TransferDto> result = transferService.getAllTransfers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(transferRepository).findAll(pageable);
    }

    @Test
    void getMyTransfers_shouldReturnData() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Transfer> page = new PageImpl<>(List.of(new Transfer()));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(transferRepository.findAllByOwner(user, pageable)).thenReturn(page);
        when(transferMapper.toTransferDto(any(Transfer.class))).thenReturn(TransferDto.builder().build());

        Page<TransferDto> result = transferService.getMyTransfers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(transferRepository).findAllByOwner(user, pageable);
    }

    @Test
    void getMyTransfers_shouldThrowUserNotFoundException() {
        Pageable pageable = Pageable.ofSize(10);
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transferService.getMyTransfers(pageable));
    }

}
