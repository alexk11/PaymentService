package com.iprody.service;

import com.iprody.model.PaymentDto;

import java.util.List;

public interface PaymentService {

    List<PaymentDto> fetchAllPayments();
    PaymentDto fetchSinglePayment(long paymentId);
    PaymentDto processPayment(PaymentDto paymentDto);
}
