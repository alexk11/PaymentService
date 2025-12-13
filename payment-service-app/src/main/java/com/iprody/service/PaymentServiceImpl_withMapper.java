package com.iprody.service;

import com.iprody.exception.AppException;
import com.iprody.mapper.PaymentMapper;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import com.iprody.specification.PaymentFilter;
import com.iprody.specification.PaymentFilterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service("withMapper")
@RequiredArgsConstructor
public class PaymentServiceImpl_withMapper implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> search(PaymentFilter filter) {
        final Specification<PaymentEntity> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository
                .findAll(spec).stream()
                .map(paymentMapper::toPaymentDto)
                .toList();
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        final Specification<PaymentEntity> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository
                .findAll(spec, pageable)
                .map(paymentMapper::toPaymentDto);
    }

    @Override
    public List<PaymentDto> fetchAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toPaymentDto)
                .toList();
    }

    @Override
    public PaymentDto fetchSinglePayment(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toPaymentDto)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND.value(), "Payment with the id '" + id + "' was not found"));
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        if (paymentDto.getAmount().doubleValue() <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Payment is not valid");
        }
        final var paymentEntity = paymentMapper.toPaymentEntity(paymentDto);
        return paymentMapper.toPaymentDto(paymentRepository.save(paymentEntity));
    }

}
