package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.UpdateCardRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardDto cardDto;
    private CreateCardRequest createCardRequest;
    private UpdateCardRequest updateCardRequest;

    @BeforeEach
    void setUp() {
        cardDto = CardDto.builder()
                .id(1L)
                .number("1234567890123456")
                .balance(BigDecimal.valueOf(1000))
                .build();

        createCardRequest = new CreateCardRequest();
        createCardRequest.setBalance(BigDecimal.valueOf(1000));
        createCardRequest.setOwnerId(1L);

        updateCardRequest = new UpdateCardRequest();
        updateCardRequest.setCardStatus(CardStatus.ACTIVE);
        updateCardRequest.setExpiryDate(LocalDateTime.now().plusYears(5));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_withAdmin_shouldReturnCreatedCard() throws Exception {
        when(cardService.createCard(any(CreateCardRequest.class))).thenReturn(cardDto);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardRequest)))
                .andExpect(status().isCreated());

        verify(cardService).createCard(any(CreateCardRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCard_withUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCardRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_withAdmin_shouldReturnOk() throws Exception {
        Page<CardDto> page = new PageImpl<>(List.of(cardDto));
        when(cardService.getAllCards(any(Pageable.class), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/cards/all"))
                .andExpect(status().isOk());

        verify(cardService).getAllCards(any(Pageable.class), any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCards_withUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/cards/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER", username = "test")
    void getMyCards_withUser_shouldReturnOk() throws Exception {
        Page<CardDto> page = new PageImpl<>(List.of(cardDto));
        when(cardService.getMyCards(any(Pageable.class), any(), eq("test"))).thenReturn(page);

        mockMvc.perform(get("/api/cards/my"))
                .andExpect(status().isOk());

        verify(cardService).getMyCards(any(Pageable.class), any(), eq("test"));
    }

    @Test
    @WithMockUser
    void getCard_shouldReturnCard() throws Exception {
        when(cardService.getCard(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk());

        verify(cardService).getCard(1L);
    }

    @Test
    @WithMockUser
    void getCardBalance_shouldReturnBalance() throws Exception {
        when(cardService.getCardBalance(1L)).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/cards/1/balance"))
                .andExpect(status().isOk());

        verify(cardService).getCardBalance(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCard_withAdmin_shouldReturnOk() throws Exception {
        when(cardService.updateCard(any(UpdateCardRequest.class), eq(1L))).thenReturn(cardDto);

        mockMvc.perform(patch("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCardRequest)))
                .andExpect(status().isOk());

        verify(cardService).updateCard(any(UpdateCardRequest.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCard_withUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCardRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test")
    void blockCard_shouldReturnOk() throws Exception {
        when(cardService.blockUserCard(1L, "test")).thenReturn(cardDto);

        mockMvc.perform(patch("/api/cards/1/block"))
                .andExpect(status().isOk());

        verify(cardService).blockUserCard(1L, "test");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_withAdmin_shouldReturnNoContent() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isNoContent());

        verify(cardService).deleteCard(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteCard_withUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isForbidden());
    }
}

