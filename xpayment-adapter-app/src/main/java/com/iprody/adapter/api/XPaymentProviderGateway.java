package com.iprody.adapter.api;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


public interface XPaymentProviderGateway {

    CreateChargeResponseDto createCharge(CreateChargeRequestDto dto) throws RestClientException;

    CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException;
}
