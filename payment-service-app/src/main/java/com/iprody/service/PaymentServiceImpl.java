package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.ApplicationException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new ApplicationException(
                        HttpStatus.NOT_FOUND.value(), "Payment with the id '" + id + "' was not found"));
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        if (paymentDto.getAmount().doubleValue() <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST.value(), "Payment is not valid");
        }
        final var paymentEntity = paymentConverter.convertToPaymentEntity(paymentDto);
        return paymentConverter.convertToPaymentDto(paymentRepository.save(paymentEntity));
    }

}
