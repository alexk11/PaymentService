package com.iprody.mapper;

import com.iprody.model.PaymentDto;
import com.iprody.persistence.PaymentEntity;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;


@AnnotateWith(value = Service.class, elements = @AnnotateWith.Element(strings = "mapper"))
@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toPaymentDto(PaymentEntity paymentEntity);
    PaymentEntity toPaymentEntity(PaymentDto dto);
}
