package com.iprody.adapter.mapper;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface XPaymentMapper {

    CreateChargeResponseDto toCreateChargeResponseDto(ChargeResponse chargeResponse);

    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto dto);

    @Mapping(target = "paymentGuid", source = "order")
    @Mapping(target = "transactionRefId", source = "id")
    @Mapping(target = "occurredAt", expression = "java(java.time.OffsetDateTime.now())")
    XPaymentAdapterResponseMessage responseDtoToKafkaMessage(CreateChargeResponseDto response);

    @Mapping(target = "order", source = "paymentGuid")
    CreateChargeRequestDto kafkaMessageToRequestDto(XPaymentAdapterRequestMessage request);
}
