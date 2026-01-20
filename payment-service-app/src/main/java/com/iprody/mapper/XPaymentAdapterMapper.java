package com.iprody.mapper;

import com.iprody.async.XPaymentAdapterRequestMessage;
import com.iprody.persistence.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface XPaymentAdapterMapper {
    @Mapping(source = "guid", target = "paymentGuid")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "updatedAt", target = "occurredAt")
    XPaymentAdapterRequestMessage toXPaymentAdapterRequestMessage(PaymentEntity paymentEntity);
}
