package com.iprody.service;

import com.iprody.async.AsyncSender;
import com.iprody.async.XPaymentAdapterRequestMessage;
import com.iprody.exception.AppException;
import com.iprody.exception.EntityNotFoundException;
import com.iprody.mapper.PaymentMapper;
import com.iprody.mapper.XPaymentAdapterMapper;
import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.persistence.PaymentRepository;
import com.iprody.persistence.PaymentStatus;
import com.iprody.specification.PaymentFilter;
import com.iprody.specification.PaymentFilterFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

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
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "get", id));
    }

    @Override
    public PaymentDto create(PaymentDto paymentDto) {
        if (paymentDto.getAmount().doubleValue() <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Payment is not valid");
        }
        final var paymentEntity = paymentMapper.toPaymentEntity(paymentDto);
        return paymentMapper.toPaymentDto(paymentRepository.save(paymentEntity));
    }

    public PaymentDto createAsync(PaymentDto dto) {
        log.info("Adding payment asynchronously");
        final PaymentEntity entity = paymentMapper.toPaymentEntity(dto);
        final PaymentEntity saved = paymentRepository.save(paymentMapper.toPaymentEntity(dto));
        final PaymentDto resultDto = paymentMapper.toPaymentDto(saved);
        log.info("Payment saved to DB:\n {}", resultDto.toString());
        // Добавляем отправку сообщения
        final XPaymentAdapterRequestMessage requestMessage =
                xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(entity);
        log.info("Request message created: {}", requestMessage.toString());
        sender.send(requestMessage);
        log.info("Request message sent...");
        return resultDto;
    }

    @Override
    public PaymentDto update(UUID id, PaymentDto dto) {
        paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "update", id));
        final PaymentEntity updated = paymentMapper.toPaymentEntity(dto);
        updated.setGuid(id);
        return paymentMapper.toPaymentDto(paymentRepository.save(updated));
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        log.info("Updating payment {}, set status to {}", id, status.name());
        return paymentRepository.findById(id)
                .map(p -> {
                    p.setStatus(status);
                    p.setUpdatedAt(OffsetDateTime.now());
                    return paymentMapper.toPaymentDto(paymentRepository.save(p));
                })
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "updateNote", id));
    }

    @Override
    public PaymentDto updateNote(UUID id, String updatedNote) {
        return paymentRepository.findById(id)
                .map(p -> {
                    p.setNote(updatedNote);
                    p.setUpdatedAt(OffsetDateTime.now());
                    return paymentMapper.toPaymentDto(paymentRepository.save(p));
                })
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "updateNote", id));
    }

    @Override
    public void delete(UUID id) {
        paymentRepository.findById(id)
                .map(p -> {
                    paymentRepository.delete(p);
                    return id;
                })
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "delete", id));
    }

}
