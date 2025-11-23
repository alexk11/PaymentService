package com.iprody.service;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> fetchAllPayments() {
        LOGGER.info("Start fetch all payments");
        try {
            List<PaymentDto> result = new ArrayList<>();
            paymentRepository.findAll().forEach(p -> result.add(convertToPaymentDto(p)));
            return result;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public PaymentDto fetchSinglePayment(long paymentId) {
        LOGGER.info("Start fetch payment method");
        try {
            var entity = paymentRepository.findByPaymentId(paymentId);
            return convertToPaymentDto(entity);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    @Transactional
    public PaymentDto processPayment(PaymentDto paymentDto) {
        LOGGER.info("Start payment processing");
        try {
            var savedEntity = paymentRepository.save(convertToPaymentEntity(paymentDto));
            return convertToPaymentDto(savedEntity);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    private PaymentEntity convertToPaymentEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                //.paymentId(paymentDto.getPaymentId())
                .amount(paymentDto.getAmount())
                .build();
    }

    private PaymentDto convertToPaymentDto(PaymentEntity paymentEntity) {
        return PaymentDto.builder()
                .paymentId(paymentEntity.getPaymentId())
                .amount(paymentEntity.getAmount())
                .build();
    }

}
