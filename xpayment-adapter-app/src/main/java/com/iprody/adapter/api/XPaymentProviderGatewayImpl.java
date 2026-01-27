package com.iprody.adapter.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Service
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi) {
        this.defaultApi = defaultApi;
    }

    @Override
    public ChargeResponse createCharge(CreateChargeRequest createChargeRequest)
            throws RestClientException {
        try {
            return defaultApi.createCharge(createChargeRequest);
        } catch (Exception e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }

    @Override
    public ChargeResponse retrieveCharge(UUID id) throws RestClientException {
        try {
            return defaultApi.retrieveCharge(id);
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
