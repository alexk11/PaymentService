package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.NoSuchPaymentException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConverter paymentConverter;

    private PaymentEntity paymentEntity1;
    private PaymentEntity paymentEntity2;
    private PaymentDto paymentDto1;
    private PaymentDto paymentDto2;

    @BeforeEach
    void setUp() {
        paymentEntity1 = PaymentEntity.builder()
                .id(1L)
                .amount("100.00")
                .build();

        paymentEntity2 = PaymentEntity.builder()
                .id(2L)
                .amount("200.00")
                .build();

        paymentDto1 = PaymentDto.builder()
                .id(1L)
                .amount("100.00")
                .build();

        paymentDto2 = PaymentDto.builder()
                .id(2L)
                .amount("200.00")
                .build();
    }

    @Test
    void fetchAllPayments_shouldReturnListOfPayments_whenPaymentsExist() {
        // Given
        List<PaymentEntity> entities = Arrays.asList(paymentEntity1, paymentEntity2);
        when(paymentRepository.findAll()).thenReturn(entities);
        when(paymentConverter.convertToPaymentDto(paymentEntity1)).thenReturn(paymentDto1);
        when(paymentConverter.convertToPaymentDto(paymentEntity2)).thenReturn(paymentDto2);

        // When
        List<PaymentDto> result = paymentService.fetchAllPayments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("100.00", result.get(0).getAmount());
        assertEquals("200.00", result.get(1).getAmount());
        verify(paymentRepository, times(1)).findAll();
        verify(paymentConverter, times(2)).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    void fetchAllPayments_shouldReturnEmptyList_whenNoPaymentsExist() {
        // Given
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PaymentDto> result = paymentService.fetchAllPayments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository, times(1)).findAll();
        verify(paymentConverter, times(0)).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    void fetchSinglePayment_shouldReturnPayment_whenPaymentExists() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity1));
        when(paymentConverter.convertToPaymentDto(paymentEntity1)).thenReturn(paymentDto1);

        // When
        PaymentDto result = paymentService.fetchSinglePayment(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("100.00", result.getAmount());
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentConverter, times(1)).convertToPaymentDto(paymentEntity1);
    }

    @Test
    void fetchSinglePayment_shouldThrowException_whenPaymentDoesNotExist() {
        // Given
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchPaymentException.class, () -> paymentService.fetchSinglePayment(999L));
        verify(paymentRepository, times(1)).findById(999L);
        verify(paymentConverter, times(0)).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    void processPayment_shouldSaveAndReturnPayment() {
        // Given
        PaymentDto inputDto = PaymentDto.builder()
                .amount("300.00")
                .build();

        PaymentEntity entityToSave = PaymentEntity.builder()
                .amount("300.00")
                .build();

        PaymentEntity savedEntity = PaymentEntity.builder()
                .id(3L)
                .amount("300.00")
                .build();

        PaymentDto expectedDto = PaymentDto.builder()
                .id(3L)
                .amount("300.00")
                .build();

        when(paymentConverter.convertToPaymentEntity(inputDto)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(paymentConverter.convertToPaymentDto(savedEntity)).thenReturn(expectedDto);

        // When
        PaymentDto result = paymentService.processPayment(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("300.00", result.getAmount());
        verify(paymentConverter, times(1)).convertToPaymentEntity(inputDto);
        verify(paymentRepository, times(1)).save(entityToSave);
        verify(paymentConverter, times(1)).convertToPaymentDto(savedEntity);
    }
}
