package com.iprody.service;


import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    private final List<PaymentEntity> payments = new ArrayList<>();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    void initPayments() {
        var payment_1 = PaymentEntity.builder().paymentId(1L).amount("100").build();
        var payment_2 = PaymentEntity.builder().paymentId(2L).amount("200").build();

        this.payments.addAll(Arrays.asList(payment_1, payment_2));
    }

    @Test
    void fetchAllPayments() {

        when(paymentRepository.findAll()).thenReturn(new ArrayList<>(payments));

        List<PaymentDto> payments = paymentService.fetchAllPayments();

        verify(paymentRepository, times(1)).findAll();
        assertEquals(2, payments.size());
    }

    @Test
    void fetchSinglePayment() {
        when(paymentRepository.findByPaymentId(anyLong())).thenReturn(Optional.ofNullable(payments.getFirst()));

        PaymentDto paymentDto = paymentService.fetchSinglePayment(1L);

        verify(paymentRepository, times(1)).findByPaymentId(1L);
        assertEquals("100", paymentDto.getAmount());
    }

    @Test
    void processPayment() {
        paymentService.processPayment(PaymentDto.builder().paymentId(3L).amount("300").build());
        verify(paymentRepository, times(1)).save(any());
    }

}
