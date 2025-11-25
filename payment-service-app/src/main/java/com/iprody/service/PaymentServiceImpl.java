package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.NoSuchPaymentException;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> fetchAllPayments() {
        log.info("Start fetch all payments");
        try {
            List<PaymentDto> result = new ArrayList<>();
            paymentRepository.findAll().forEach(p ->
                    result.add(paymentConverter.convertToPaymentDto(p)));
            return result;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public PaymentDto fetchSinglePayment(long paymentId) {
       log.info("Start fetching one payment");
       Optional<PaymentEntity> entityOptional = paymentRepository.findByPaymentId(paymentId);
       if (entityOptional.isPresent()) {
           return paymentConverter.convertToPaymentDto(entityOptional.get());
       } else {
           throw new NoSuchPaymentException("Payment with the id " + paymentId + " not found");
       }
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        log.info("Start payment processing");
        try {
            var savedEntity = paymentRepository.save(
                    paymentConverter.convertToPaymentEntity(paymentDto));
            return paymentConverter.convertToPaymentDto(savedEntity);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

}
