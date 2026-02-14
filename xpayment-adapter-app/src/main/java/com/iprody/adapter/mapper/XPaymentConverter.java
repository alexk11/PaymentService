package com.iprody.adapter.mapper;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.springframework.stereotype.Component;


@Component
public class XPaymentConverter {

    public CreateChargeResponseDto toCreateChargeResponseDto(ChargeResponse chargeResponse) {
        return CreateChargeResponseDto.builder()
                .id(chargeResponse.getId())
                .amount(chargeResponse.getAmount())
                .currency(chargeResponse.getCurrency())
                .amountReceived(chargeResponse.getAmountReceived())
                .createdAt(chargeResponse.getCreatedAt())
                .chargedAt(chargeResponse.getChargedAt())
                .customer("test customer")
                .order(chargeResponse.getOrder())
                .receiptEmail("abc@test.com")
                .status(chargeResponse.getStatus())
                .metadata(chargeResponse.getMetadata())
                .build();
    }

    public CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto dto) {
        CreateChargeRequest result = new CreateChargeRequest();
        result.setOrder(dto.getOrder());
        result.setAmount(dto.getAmount());
        result.setCurrency(dto.getCurrency());
        result.setCustomer("test customer");
        result.setReceiptEmail("abc@test.com");
        result.setMetadata(dto.getMetadata());

        return result;
    }

}
