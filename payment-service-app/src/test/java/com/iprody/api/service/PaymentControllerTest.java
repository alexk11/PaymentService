package com.iprody.api.service;

import com.iprody.api.PaymentStatus;
import com.iprody.controller.PaymentController;
import com.iprody.model.PaymentDto;
import com.iprody.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    private PaymentDto dto_1;
    private PaymentDto dto_2;

    @BeforeEach
    void setUp() {
        PaymentController controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        initData();
    }

    private void initData() {
        LocalDateTime now = LocalDateTime.now();
        dto_1 = new PaymentDto(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa7"),
                BigDecimal.valueOf(100.89),
                "EUR",
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa8"),
                PaymentStatus.RECEIVED,
                "Some info 1",
                OffsetDateTime.of(now, ZoneOffset.UTC),
                OffsetDateTime.of(now, ZoneOffset.UTC)
        );
        dto_2 = new PaymentDto(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afb6"),
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afb7"),
                BigDecimal.valueOf(200.51),
                "USD",
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afb8"),
                PaymentStatus.APPROVED,
                "Some info 2",
                OffsetDateTime.of(now, ZoneOffset.UTC),
                OffsetDateTime.of(now, ZoneOffset.UTC)
        );
    }

    @Test
    @DisplayName("GET /payments should return list of two PaymentDto")
    void findAll_ReturnsListOfTwoPayments() throws Exception {
        // when
        when(paymentService.getPayments()).thenReturn(List.of(dto_1, dto_2));
        // then
        mockMvc.perform(get("/payments").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].amount").value(100.89))
                .andExpect(jsonPath("$[0].note").value("Some info 1"))
                .andExpect(jsonPath("$[1].currency").value("USD"))
                .andExpect(jsonPath("$[1].amount").value(200.51))
                .andExpect(jsonPath("$[1].note").value("Some info 2"));
    }

    @Test
    @DisplayName("GET /payments should return empty list when there is no payments")
    void findAll_ReturnsEmptyList() throws Exception {
        // when
        when(paymentService.getPayments()).thenReturn(List.of());
        // then
        mockMvc.perform(get("/payments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /payments/{id} should return PaymentDto when found")
    void getById_ReturnsPayment_WhenFound() throws Exception {
        // given
        UUID id = dto_1.getGuid();
        // when
        when(paymentService.get(id)).thenReturn(dto_1);
        // then
        mockMvc.perform(get("/payments/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.guid").value("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .andExpect(jsonPath("$.amount").value(100.89))
                .andExpect(jsonPath("$.note").value("Some info 1"));
    }

}
