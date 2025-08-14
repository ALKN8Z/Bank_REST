package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessToResourceDeniedException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardMapper;
import com.example.bankcards.util.CardNumberUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardNumberUtils cardNumberUtils;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cardService, "cardExpirationYears", 2);

        user = User.builder()
                .id(1L)
                .username("test")
                .password("test")
                .role(Role.USER)
                .cards(new HashSet<>())
                .build();

        card = Card.builder()
                .id(1L)
                .owner(user)
                .number("encryptedNumber")
                .cardStatus(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(3500))
                .expiryDate(LocalDateTime.now().plusYears(2))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );
    }

    @Test
    void createCard_shouldCreateNewCard() {
        CreateCardRequest request = new CreateCardRequest();
        request.setOwnerId(1L);
        request.setBalance(new BigDecimal("3500.00"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardNumberUtils.generateCardNumber()).thenReturn("1234567890123456");
        when(cardNumberUtils.encryptCardNumber("1234567890123456")).thenReturn("encryptedNumber");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().number("**** **** **** 3456").ownerUsername("test").balance(new BigDecimal("3500.00")).build());

        CardDto result = cardService.createCard(request);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo("**** **** **** 3456");
        assertThat(result.getOwnerUsername()).isEqualTo("test");
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("3500.00"));

        verify(userRepository).findById(1L);
        verify(cardNumberUtils).encryptCardNumber(any());
        verify(cardNumberUtils).generateCardNumber();
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toCardDto(any(Card.class));
    }

    @Test
    void createCard_UserNotFound_ThrowsException() {
        CreateCardRequest request = new CreateCardRequest();
        request.setOwnerId(77L);
        request.setBalance(new BigDecimal("3500.00"));
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getCard_shouldReturnCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardMapper.toCardDto(card)).thenReturn(CardDto.builder().id(1L).ownerUsername("test").number("**** **** **** 3456").balance(new BigDecimal("3500.00")).build());

        CardDto result = cardService.getCard(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo("**** **** **** 3456");
        assertThat(result.getOwnerUsername()).isEqualTo("test");
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("3500.00"));
        verify(cardMapper).toCardDto(card);
        verify(userRepository).findByUsername("test");
        verify(cardRepository).findById(1L);
    }

    @Test
    void getCard_shouldThrowAccessToResourceDeniedException() {
        User otherUser = User.builder().id(2L).username("test2").role(Role.USER).build();
        card.setOwner(otherUser);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        assertThrows(AccessToResourceDeniedException.class, () -> cardService.getCard(1L));
    }

    @Test
    void getCard_dontShouldThrowAccessToResourceDeniedExceptionIfRoleAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null));
        user.setRole(Role.ADMIN);
        User otherUser = User.builder().id(2L).username("testUser").role(Role.USER).build();
        card.setOwner(otherUser);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(cardMapper.toCardDto(card)).thenReturn(CardDto.builder().number("**** **** **** 3456").ownerUsername("testUser").balance(new BigDecimal("3500.00")).build());

        CardDto result = cardService.getCard(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo("**** **** **** 3456");
        assertThat(result.getOwnerUsername()).isEqualTo("testUser");
        assertThat(result.getBalance()).isEqualTo(new BigDecimal("3500.00"));
        verify(cardMapper).toCardDto(card);
        verify(userRepository).findByUsername("admin");
        verify(cardRepository).findById(1L);

    }

    @Test
    void blockUserCard_shouldSetBlockedStatus() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(CardDto.builder().cardStatus(CardStatus.BLOCKED).ownerUsername("test").build());

        CardDto result = cardService.blockUserCard(1L, "test");

        assertThat(result).isNotNull();
        assertThat(result.getCardStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void blockUserCard_shouldThrowAccessToResourceDeniedException() {
        card.setOwner(User.builder().id(2L).username("test2").role(Role.USER).build());
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(AccessToResourceDeniedException.class,
                () -> cardService.blockUserCard(1L, "test"));
    }

    @Test
    void getCardBalance_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        BigDecimal balance = cardService.getCardBalance(1L);

        assertThat(balance).isEqualTo(BigDecimal.valueOf(3500));
    }

    @Test
    void getCardBalance_shouldThrowAccessToResourceDeniedException() {
        card.setOwner(User.builder().id(2L).username("test2").build());
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));

        assertThrows(AccessToResourceDeniedException.class, () -> cardService.getCardBalance(1L));
    }

    @Test
    void getCardBalance_dontShouldThrowAccessToResourceDeniedExceptionIfRoleAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null));
        card.setOwner(User.builder().id(2L).username("test2").role(Role.USER).build());
        user.setRole(Role.ADMIN);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        BigDecimal balance = cardService.getCardBalance(1L);

        assertThat(balance).isEqualTo(BigDecimal.valueOf(3500));
    }

    @Test
    void getAllCards_shouldReturnAllCardsWithNoFilters() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().id(1L).build());

        Page<CardDto> result = cardService.getAllCards(pageable, null, null);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getAllCards_shouldReturnAllCardsWithFilterByOwner() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByOwner(user, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().id(1L).ownerUsername("test").build());

        Page<CardDto> result = cardService.getAllCards(pageable, null, "test");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByOwner(user, pageable);
    }

    @Test
    void getAllCards_shouldReturnAllCardsWithFilterByStatus() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAllByCardStatus(CardStatus.ACTIVE, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().id(1L).cardStatus(CardStatus.ACTIVE).build());

        Page<CardDto> result = cardService.getAllCards(pageable, "active", null);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByCardStatus(CardStatus.ACTIVE, pageable);
    }

    @Test
    void getAllCards_shouldReturnAllCardsWithFilterByOwnerAndStatus() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByOwnerAndCardStatus(user, CardStatus.BLOCKED, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().cardStatus(CardStatus.BLOCKED).ownerUsername("test").build());

        Page<CardDto> result = cardService.getAllCards(pageable, "blocked", "test");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByOwnerAndCardStatus(user, CardStatus.BLOCKED, pageable);
    }

    @Test
    void getMyCards_shouldReturnMyCardsWithNoFilter() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByOwner(user, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().ownerUsername("test").build());

        Page<CardDto> result = cardService.getMyCards(pageable, null, "test");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByOwner(user, pageable);
    }

    @Test
    void getMyCards_shouldReturnMyCardsWithFilterByStatus() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Card> page = new PageImpl<>(List.of(card));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByOwnerAndCardStatus(user, CardStatus.ACTIVE, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(any(Card.class))).thenReturn(CardDto.builder().ownerUsername("test").cardStatus(CardStatus.ACTIVE).build());

        Page<CardDto> result = cardService.getMyCards(pageable, "ACTIVE", "test");

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByOwnerAndCardStatus(user, CardStatus.ACTIVE, pageable);
    }

    @Test
    void getMyCards_shouldThrowUserNotFoundException() {
        Pageable pageable = Pageable.ofSize(10);
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.getMyCards(pageable, null, "test"));
    }
}
