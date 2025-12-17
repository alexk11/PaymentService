package com.iprody.mapper;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toPaymentDto(PaymentEntity paymentEntity);
    PaymentEntity toPaymentEntity(PaymentDto paymentDto);
}
