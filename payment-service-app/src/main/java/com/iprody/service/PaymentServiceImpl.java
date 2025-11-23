package com.iprody.service;

import com.iprody.converter.PaymentConverter;
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
        log.info("Start fetch payment method");
        try {
            var entity = paymentRepository.findByPaymentId(paymentId);
            return paymentConverter.convertToPaymentDto(entity);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    //@Transactional
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
