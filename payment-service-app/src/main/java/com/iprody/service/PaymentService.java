package com.iprody.service;

import com.iprody.PaymentStatus;
import com.iprody.model.PaymentDto;
import com.iprody.specification.PaymentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentDto create(PaymentDto paymentDto);
    PaymentDto createAsync(PaymentDto paymentDto);
    PaymentDto get(UUID id);
    List<PaymentDto> search(PaymentFilter filter);
    Page<PaymentDto> searchPaged(PaymentFilter filter, Pageable pageable);
    List<PaymentDto> getPayments();
    PaymentDto update(UUID id, PaymentDto dto);
    PaymentDto updateNote(UUID id, String updatedNote);
    PaymentDto updateStatus(UUID id, PaymentStatus status);
    void delete(UUID id);
}
