package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.NoSuchPaymentException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> fetchAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentConverter::convertToPaymentDto)
                .toList();
    }

    @Override
    public PaymentDto fetchSinglePayment(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentConverter::convertToPaymentDto)
                .orElseThrow(NoSuchPaymentException::new);
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        final var paymentEntity = paymentConverter.convertToPaymentEntity(paymentDto);
        return paymentConverter.convertToPaymentDto(paymentRepository.save(paymentEntity));
    }

}
