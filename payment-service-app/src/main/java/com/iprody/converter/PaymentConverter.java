package com.iprody.converter;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter {

    public PaymentEntity convertToPaymentEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                .amount(paymentDto.getAmount())
                .build();
    }

    public PaymentDto convertToPaymentDto(PaymentEntity paymentEntity) {
        return PaymentDto.builder()
                .id(paymentEntity.getId())
                .amount(paymentEntity.getAmount())
                .build();
    }

}
