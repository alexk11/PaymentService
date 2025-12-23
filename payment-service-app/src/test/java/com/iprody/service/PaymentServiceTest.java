package com.iprody.service;

import com.iprody.exception.EntityNotFoundException;
import com.iprody.mapper.PaymentMapper;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import com.iprody.persistence.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

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
    private PaymentMapper paymentMapper;

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
    @DisplayName("getPayments should return list of payments when payments exist")
    void getPayments_shouldReturnListOfPayments_whenPaymentsExist() {
        // Given
        List<PaymentEntity> entities = Arrays.asList(paymentEntity1, paymentEntity2);
        when(paymentRepository.findAll()).thenReturn(entities);
        when(paymentMapper.toPaymentDto(paymentEntity1)).thenReturn(paymentDto1);
        when(paymentMapper.toPaymentDto(paymentEntity2)).thenReturn(paymentDto2);
        // When
        List<PaymentDto> result = paymentService.getPayments();
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
        verify(paymentMapper, times(2)).toPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("getPayments should return empty list when no payments exist")
    void getPayments_shouldReturnEmptyList_whenNoPaymentsExist() {
        // Given
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());
        // When
        List<PaymentDto> result = paymentService.getPayments();
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(paymentRepository, times(1)).findAll();
        verify(paymentMapper, never()).toPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("getPayment should return payment when payment exists")
    void getPayment_shouldReturnPayment_whenExists() {
        // Given
        when(paymentRepository.findById(testUuid1)).thenReturn(Optional.of(paymentEntity1));
        when(paymentMapper.toPaymentDto(paymentEntity1)).thenReturn(paymentDto1);
        // When
        PaymentDto result = paymentService.get(testUuid1);
        // Then
        assertNotNull(result);
        assertEquals(testUuid1, result.getGuid());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals("Test payment 1", result.getNote());

        verify(paymentRepository, times(1)).findById(testUuid1);
        verify(paymentMapper, times(1)).toPaymentDto(paymentEntity1);
    }

    @Test
    @DisplayName("getPayment should throw NoSuchPaymentException when payment does not exist")
    void getPayment_shouldThrowException_whenDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(paymentRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        // When
        assertThrows(EntityNotFoundException.class, () -> paymentService.get(nonExistentId));
        // Then
        verify(paymentRepository, times(1)).findById(nonExistentId);
        verify(paymentMapper, never()).toPaymentDto(any(PaymentEntity.class));
    }

    @Test
    @DisplayName("create should save and return payment")
    void createPayment_shouldSaveAndReturnPayment() {
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

        when(paymentMapper.toPaymentEntity(inputDto)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(paymentMapper.toPaymentDto(savedEntity)).thenReturn(expectedDto);

        // When
        PaymentDto result = paymentService.create(inputDto);

        // Then
        assertNotNull(result);
        assertEquals(newUuid, result.getGuid());
        assertEquals(new BigDecimal("300.00"), result.getAmount());
        assertEquals("GBP", result.getCurrency());
        assertEquals(PaymentStatus.RECEIVED, result.getStatus());

        verify(paymentMapper, times(1)).toPaymentEntity(inputDto);
        verify(paymentRepository, times(1)).save(entityToSave);
        verify(paymentMapper, times(1)).toPaymentDto(savedEntity);
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    @DisplayName("get should handle different payment statuses")
    void get_shouldHandleDifferentStatuses(PaymentStatus status) {
        // Given
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .guid(testUuid1)
                .inquiryRefId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .status(status)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        PaymentDto paymentDto = PaymentDto.builder()
                .guid(testUuid1)
                .status(status)
                .build();

        when(paymentRepository.findById(testUuid1)).thenReturn(Optional.of(paymentEntity));
        when(paymentMapper.toPaymentDto(paymentEntity)).thenReturn(paymentDto);

        // When
        PaymentDto result = paymentService.get(testUuid1);

        // Then
        assertEquals(status, result.getStatus());
    }

    private static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.RECEIVED,
                PaymentStatus.PENDING,
                PaymentStatus.APPROVED,
                PaymentStatus.DECLINED,
                PaymentStatus.NOT_SENT
        );
    }

}
