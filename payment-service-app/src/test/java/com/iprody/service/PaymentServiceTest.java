package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.ApplicationException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import com.iprody.persistence.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
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
    private UUID testUuid1;
    private UUID testUuid2;

    @BeforeEach
    void setUp() {
        testUuid1 = UUID.randomUUID();
        testUuid2 = UUID.randomUUID();

        paymentEntity1 = PaymentEntity.builder()
                .guid(testUuid1)
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.APPROVED)
                .note("Test payment 1")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        paymentEntity2 = PaymentEntity.builder()
                .guid(testUuid2)
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("200.00"))
                .currency("EUR")
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .note("Test payment 2")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        paymentDto1 = PaymentDto.builder()
                .guid(testUuid1)
                .inquiryRefId(paymentEntity1.getInquiryRefId())
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .transactionRefId(paymentEntity1.getTransactionRefId())
                .status(PaymentStatus.APPROVED)
                .note("Test payment 1")
                .createdAt(paymentEntity1.getCreatedAt())
                .updatedAt(paymentEntity1.getUpdatedAt())
                .build();

        paymentDto2 = PaymentDto.builder()
                .guid(testUuid2)
                .inquiryRefId(paymentEntity2.getInquiryRefId())
                .amount(new BigDecimal("200.00"))
                .currency("EUR")
                .transactionRefId(paymentEntity2.getTransactionRefId())
                .status(PaymentStatus.PENDING)
                .note("Test payment 2")
                .createdAt(paymentEntity2.getCreatedAt())
                .updatedAt(paymentEntity2.getUpdatedAt())
                .build();
    }

    @Test
    @DisplayName("fetchAllPayments should return list of payments when payments exist")
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
        assertEquals(testUuid1, result.get(0).getGuid());
        assertEquals(testUuid2, result.get(1).getGuid());
        assertEquals(new BigDecimal("100.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("200.00"), result.get(1).getAmount());
        assertEquals("USD", result.get(0).getCurrency());
        assertEquals("EUR", result.get(1).getCurrency());

        verify(paymentRepository, times(1)).findAll();
        verify(paymentConverter, times(2)).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("fetchAllPayments should return empty list when no payments exist")
    void fetchAllPayments_shouldReturnEmptyList_whenNoPaymentsExist() {
        // Given
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PaymentDto> result = paymentService.fetchAllPayments();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(paymentRepository, times(1)).findAll();
        verify(paymentConverter, never()).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("fetchAllPayments should handle multiple payments correctly")
    void fetchAllPayments_shouldHandleMultiplePayments() {
        // Given
        List<PaymentEntity> entities = Arrays.asList(paymentEntity1, paymentEntity2);
        when(paymentRepository.findAll()).thenReturn(entities);
        when(paymentConverter.convertToPaymentDto(any(PaymentEntity.class)))
                .thenReturn(paymentDto1, paymentDto2);

        // When
        List<PaymentDto> result = paymentService.fetchAllPayments();

        // Then
        assertEquals(2, result.size());
        verify(paymentConverter, times(2)).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("fetchSinglePayment should return payment when payment exists")
    void fetchSinglePayment_shouldReturnPayment_whenPaymentExists() {
        // Given
        when(paymentRepository.findById(testUuid1)).thenReturn(Optional.of(paymentEntity1));
        when(paymentConverter.convertToPaymentDto(paymentEntity1)).thenReturn(paymentDto1);
        // When
        PaymentDto result = paymentService.fetchSinglePayment(testUuid1);
        // Then
        assertNotNull(result);
        assertEquals(testUuid1, result.getGuid());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals("Test payment 1", result.getNote());

        verify(paymentRepository, times(1)).findById(testUuid1);
        verify(paymentConverter, times(1)).convertToPaymentDto(paymentEntity1);
    }

    @Test
    @DisplayName("fetchSinglePayment should throw NoSuchPaymentException when payment does not exist")
    void fetchSinglePayment_shouldThrowException_whenPaymentDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        // When
        when(paymentRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        // Then
        assertThrows(ApplicationException.class,
                () -> paymentService.fetchSinglePayment(nonExistentId));

        verify(paymentRepository, times(1)).findById(nonExistentId);
        verify(paymentConverter, never()).convertToPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("fetchSinglePayment should handle different payment statuses")
    void fetchSinglePayment_shouldHandleDifferentStatuses() {
        // Given
        PaymentEntity pendingEntity = PaymentEntity.builder()
                .guid(testUuid1)
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .status(PaymentStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        PaymentDto pendingDto = PaymentDto.builder()
                .guid(testUuid1)
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findById(testUuid1)).thenReturn(Optional.of(pendingEntity));
        when(paymentConverter.convertToPaymentDto(pendingEntity)).thenReturn(pendingDto);

        // When
        PaymentDto result = paymentService.fetchSinglePayment(testUuid1);

        // Then
        assertEquals(PaymentStatus.PENDING, result.getStatus());
    }

    @Test
    @DisplayName("processPayment should save and return payment")
    void processPayment_shouldSaveAndReturnPayment() {
        // Given
        UUID newUuid = UUID.randomUUID();
        PaymentDto inputDto = PaymentDto.builder()
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("300.00"))
                .currency("GBP")
                .status(PaymentStatus.RECEIVED)
                .note("New payment")
                .build();

        PaymentEntity entityToSave = PaymentEntity.builder()
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("300.00"))
                .currency("GBP")
                .status(PaymentStatus.RECEIVED)
                .note("New payment")
                .build();

        PaymentEntity savedEntity = PaymentEntity.builder()
                .guid(newUuid)
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("300.00"))
                .currency("GBP")
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.RECEIVED)
                .note("New payment")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        PaymentDto expectedDto = PaymentDto.builder()
                .guid(newUuid)
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("300.00"))
                .currency("GBP")
                .transactionRefId(savedEntity.getTransactionRefId())
                .status(PaymentStatus.RECEIVED)
                .note("New payment")
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();

        when(paymentConverter.convertToPaymentEntity(inputDto)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(paymentConverter.convertToPaymentDto(savedEntity)).thenReturn(expectedDto);

        // When
        PaymentDto result = paymentService.processPayment(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(newUuid, result.getGuid());
        assertEquals(new BigDecimal("300.00"), result.getAmount());
        assertEquals("GBP", result.getCurrency());
        assertEquals(PaymentStatus.RECEIVED, result.getStatus());

        verify(paymentConverter, times(1)).convertToPaymentEntity(inputDto);
        verify(paymentRepository, times(1)).save(entityToSave);
        verify(paymentConverter, times(1)).convertToPaymentDto(savedEntity);
    }

    @Test
    @DisplayName("processPayment should handle payment with all fields populated")
    void processPayment_shouldHandleCompletePayment() {
        // Given
        UUID guid = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        PaymentDto inputDto = PaymentDto.builder()
                .guid(guid)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("500.50"))
                .currency("USD")
                .transactionRefId(transactionRefId)
                .status(PaymentStatus.APPROVED)
                .note("Complete payment with all fields")
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentEntity entityToSave = PaymentEntity.builder()
                .guid(guid)
                .inquiryRefId(inquiryRefId)
                .amount(new BigDecimal("500.50"))
                .currency("USD")
                .transactionRefId(transactionRefId)
                .status(PaymentStatus.APPROVED)
                .note("Complete payment with all fields")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(paymentConverter.convertToPaymentEntity(inputDto)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(entityToSave);
        when(paymentConverter.convertToPaymentDto(entityToSave)).thenReturn(inputDto);

        // When
        PaymentDto result = paymentService.processPayment(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(guid, result.getGuid());
        assertEquals(inquiryRefId, result.getInquiryRefId());
        assertEquals(transactionRefId, result.getTransactionRefId());
        assertEquals(new BigDecimal("500.50"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals("Complete payment with all fields", result.getNote());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
    }

    @Test
    @DisplayName("processPayment should handle payment with minimal fields")
    void processPayment_shouldHandleMinimalPayment() {
        // Given
        PaymentDto inputDto = PaymentDto.builder()
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("10.00"))
                .currency("USD")
                .status(PaymentStatus.RECEIVED)
                .build();

        PaymentEntity entityToSave = PaymentEntity.builder()
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("10.00"))
                .currency("USD")
                .status(PaymentStatus.RECEIVED)
                .build();

        PaymentEntity savedEntity = PaymentEntity.builder()
                .guid(UUID.randomUUID())
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("10.00"))
                .currency("USD")
                .status(PaymentStatus.RECEIVED)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        PaymentDto savedDto = PaymentDto.builder()
                .guid(savedEntity.getGuid())
                .inquiryRefId(inputDto.getInquiryRefId())
                .amount(new BigDecimal("10.00"))
                .currency("USD")
                .status(PaymentStatus.RECEIVED)
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();

        when(paymentConverter.convertToPaymentEntity(inputDto)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(paymentConverter.convertToPaymentDto(savedEntity)).thenReturn(savedDto);

        // When
        PaymentDto result = paymentService.processPayment(inputDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getGuid());
        assertEquals(new BigDecimal("10.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.RECEIVED, result.getStatus());
    }
}
