package com.iprody.adapter.api;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.adapter.mapper.XPaymentConverter;
import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Slf4j
@Service
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;
    private final XPaymentConverter converter;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi,
                                       XPaymentConverter converter) {
        this.defaultApi = defaultApi;
        this.converter = converter;
    }

    @Override
    public CreateChargeResponseDto createCharge(CreateChargeRequestDto dto)
            throws RestClientException {
        try {
            log.info("Creating charge for payment '{}'", dto.getOrder());
            CreateChargeRequest chargeRequest = converter.toCreateChargeRequest(dto);
            ChargeResponse response = defaultApi.createCharge(chargeRequest);
            log.info("Got charge response for payment '{}', the status is {}",
                    response.getId(), response.getStatus());
            return converter.toCreateChargeResponseDto(response);
        } catch (Exception e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }

    @Override
    public CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        try {
            ChargeResponse response = defaultApi.retrieveCharge(id);
            log.info("Retrieved charge for id = {} in status {} ", id, response.getStatus());
            return converter.toCreateChargeResponseDto(response);
        } catch (Exception e) {
            throw toRestClientException("GET /charges/{id} failed (id=" + id + ")", e);
        }
    }

    private RestClientException toRestClientException(String prefix, Exception e) {
        String msg = String.format("%s: HTTP %s, cause: %s",
                prefix,
                e.getMessage(),
                safeStringConverter(String.valueOf(e.getCause())));
        return new RestClientException(msg, e);
    }

    private String safeStringConverter(String s) {
        return s == null ? "<empty>" : s;
    }

}
