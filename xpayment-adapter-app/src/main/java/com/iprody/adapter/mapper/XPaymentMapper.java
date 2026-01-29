package com.iprody.adapter.mapper;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface XPaymentMapper {

    CreateChargeRequestDto toChargeRequestDto(CreateChargeRequest chargeRequest);

    CreateChargeResponseDto toCreateChargeResponseDto(ChargeResponse chargeResponse);

    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto dto);

    ChargeResponse toChargeResponse(CreateChargeResponseDto dto);
}
