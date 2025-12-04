package com.iprody.service;

import com.iprody.model.PaymentDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> fetchAllPayments();
    PaymentDto fetchSinglePayment(UUID id);
    PaymentDto processPayment(PaymentDto paymentDto);
}
