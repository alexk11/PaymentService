package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.NoSuchPaymentException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> fetchAllPayments() {
        final List<PaymentDto> result = new ArrayList<>();
        try {
            paymentRepository.findAll().forEach(p -> result.add(paymentConverter.convertToPaymentDto(p)));
            return result;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    @Override
    public PaymentDto fetchSinglePayment(long id) {
        return paymentConverter
                .convertToPaymentDto(paymentRepository
                        .findById(id)
                        .orElseThrow(NoSuchPaymentException::new));
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        final var paymentEntity = paymentConverter.convertToPaymentEntity(paymentDto);
        return paymentConverter.convertToPaymentDto(paymentRepository.save(paymentEntity));
    }

}
