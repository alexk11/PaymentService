package com.iprody.converter;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import org.springframework.stereotype.Component;

@Component("converter")
public class PaymentConverter {

    public PaymentEntity toPaymentEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                .guid(paymentDto.getGuid())
                .inquiryRefId(paymentDto.getInquiryRefId())
                .amount(paymentDto.getAmount())
                .currency(paymentDto.getCurrency())
                .transactionRefId(paymentDto.getTransactionRefId())
                .status(paymentDto.getStatus())
                .note(paymentDto.getNote())
                .createdAt(paymentDto.getCreatedAt())
                .updatedAt(paymentDto.getUpdatedAt())
                .build();
    }

    public PaymentDto toPaymentDto(PaymentEntity paymentEntity) {
        return PaymentDto.builder()
                .guid(paymentEntity.getGuid())
                .inquiryRefId(paymentEntity.getInquiryRefId())
                .amount(paymentEntity.getAmount())
                .currency(paymentEntity.getCurrency())
                .transactionRefId(paymentEntity.getTransactionRefId())
                .status(paymentEntity.getStatus())
                .note(paymentEntity.getNote())
                .createdAt(paymentEntity.getCreatedAt())
                .updatedAt(paymentEntity.getUpdatedAt())
                .build();
    }

}
