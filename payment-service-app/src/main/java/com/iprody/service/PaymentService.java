package com.iprody.service;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import com.iprody.specification.PaymentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<PaymentDto> fetchAllPayments();
    PaymentDto fetchSinglePayment(UUID id);
    PaymentDto processPayment(PaymentDto paymentDto);
    List<PaymentEntity> search(PaymentFilter filter);
    Page<PaymentEntity> searchPaged(PaymentFilter filter, Pageable pageable);
}
