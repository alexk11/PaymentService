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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

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
    public List<PaymentDto> getPayments() {
        return paymentRepository
                .findAll().stream()
                .map(paymentMapper::toPaymentDto)
                .toList();
    }

    @Override
    public PaymentDto get(UUID id) {
        return paymentRepository.findById(id)
            .map(paymentMapper::toPaymentDto)
            .orElseThrow(() -> new AppException(
                    HttpStatus.NOT_FOUND.value(),
                    "Payment with the id '" + id + "' was not found"));
    }

    @Override
    public PaymentDto create(PaymentDto paymentDto) {
        if (paymentDto.getAmount().doubleValue() <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Payment is not valid");
        }
        final var paymentEntity = paymentMapper.toPaymentEntity(paymentDto);
        return paymentMapper.toPaymentDto(paymentRepository.save(paymentEntity));
    }

    @Override
    public PaymentDto update(UUID id, PaymentDto dto) {
        return paymentRepository.findById(id)
            .map(p -> {
                p.setInquiryRefId(dto.getInquiryRefId());
                p.setAmount(dto.getAmount());
                p.setCurrency(dto.getCurrency());
                p.setTransactionRefId(dto.getTransactionRefId());
                p.setStatus(dto.getStatus());
                p.setNote(dto.getNote());
                p.setCreatedAt(dto.getCreatedAt());
                p.setUpdatedAt(OffsetDateTime.now());
                return paymentMapper.toPaymentDto(paymentRepository.save(p));
            })
            .orElseThrow(() -> new AppException(
                    HttpStatus.NOT_FOUND.value(),
                    "Update failed. Payment with id '" + id + "' does not exist."));
    }

    @Override
    public PaymentDto updateNote(UUID id, String updatedNote) {
        return paymentRepository.findById(id)
            .map(p -> {
                p.setNote(updatedNote);
                p.setUpdatedAt(OffsetDateTime.now());
                return paymentMapper.toPaymentDto(paymentRepository.save(p));
            })
            .orElseThrow(() -> new AppException(
                    HttpStatus.NOT_FOUND.value(),
                    "Note update failed. Payment with id '" + id + "' does not exist."));
    }

    @Override
    public void delete(UUID id) {
         paymentRepository.findById(id)
            .map(p -> {
                paymentRepository.delete(p);
                return id;
            })
            .orElseThrow(() -> new AppException(
                    HttpStatus.NOT_FOUND.value(),
                    "Delete failed. Payment with id '" + id + "' does not exist."));
    }

}
