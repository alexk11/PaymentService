package com.iprody.service;

import com.iprody.converter.PaymentConverter;
import com.iprody.exception.AppException;
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


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentConverter paymentConverter;
    private final PaymentRepository paymentRepository;

    @Override
    public List<PaymentDto> search(PaymentFilter filter) {
        final Specification<PaymentEntity> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository
                .findAll(spec).stream()
                .map(paymentConverter::toPaymentDto)
                .toList();
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable) {
        final Specification<PaymentEntity> spec = PaymentFilterFactory.fromFilter(filter);
        return paymentRepository
                .findAll(spec, pageable)
                .map(paymentConverter::toPaymentDto);
    }

    @Override
    public List<PaymentDto> fetchAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentConverter::toPaymentDto)
                .toList();
    }

    @Override
    public PaymentDto fetchSinglePayment(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentConverter::toPaymentDto)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND.value(), "Payment with the id '" + id + "' was not found"));
    }

    @Override
    public PaymentDto processPayment(PaymentDto paymentDto) {
        if (paymentDto.getAmount().doubleValue() <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Payment is not valid");
        }
        final var paymentEntity = paymentConverter.toPaymentEntity(paymentDto);
        return paymentConverter.toPaymentDto(paymentRepository.save(paymentEntity));
    }

}
