package com.example.bankcards.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.bankcards.dto.CreateTransferRequest;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @Test
    @WithMockUser
    void transfer_shouldReturnCreatedTransfer() throws Exception {
        CreateTransferRequest request = new CreateTransferRequest();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));
        TransferDto transferDto = TransferDto.builder()
                .amount(BigDecimal.valueOf(100))
                .build();
        when(transferService.createTransfer(any(CreateTransferRequest.class))).thenReturn(transferDto);

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount", is(100)));

        verify(transferService).createTransfer(eq(request));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTransfers_shouldReturnPageOfTransfers_forAdmin() throws Exception {
        TransferDto transferDto = TransferDto.builder()
                .amount(BigDecimal.valueOf(100))
                .build();
        Page<TransferDto> page = new PageImpl<>(List.of(transferDto));
        when(transferService.getAllTransfers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/transfers/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount", is(100)));
    }

    @Test
    @WithMockUser
    void getMyTransfers_shouldReturnPageOfTransfers_forUser() throws Exception {
        TransferDto transferDto = TransferDto.builder()
                .amount(BigDecimal.valueOf(300))
                .build();
        Page<TransferDto> page = new PageImpl<>(List.of(transferDto));
        when(transferService.getMyTransfers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/transfers/my")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount", is(300)));
    }


}
