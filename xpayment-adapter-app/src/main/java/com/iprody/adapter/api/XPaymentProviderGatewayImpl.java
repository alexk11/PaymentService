package com.iprody.adapter.api;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.adapter.mapper.XPaymentMapper;
import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Service
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;
    private final XPaymentMapper mapper;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi, XPaymentMapper mapper) {
        this.defaultApi = defaultApi;
        this.mapper = mapper;
    }

    @Override
    public CreateChargeResponseDto createCharge(CreateChargeRequestDto dto)
            throws RestClientException {
        try {
            CreateChargeRequest chargeRequest = mapper.toCreateChargeRequest(dto);
            return mapper.toCreateChargeResponseDto(defaultApi.createCharge(chargeRequest));
        } catch (Exception e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }

    @Override
    public CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        try {
            return mapper.toCreateChargeResponseDto(defaultApi.retrieveCharge(id));
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
