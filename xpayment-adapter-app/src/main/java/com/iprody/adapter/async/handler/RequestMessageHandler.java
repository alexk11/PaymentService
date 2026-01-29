package com.iprody.adapter.async.handler;

import com.iprody.adapter.api.XPaymentProviderGateway;
import com.iprody.api.AsyncSender;
import com.iprody.api.XPaymentAdapterStatus;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;

import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;


@Slf4j
@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    @Autowired
    public RequestMessageHandler(
            XPaymentProviderGateway xPaymentProviderGateway,
            AsyncSender<XPaymentAdapterResponseMessage> asyncSender) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.asyncSender = asyncSender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {

        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",

        message.getPaymentGuid(), message.getAmount(), message.getCurrency());

        CreateChargeRequest createChargeRequest = new CreateChargeRequest();
        createChargeRequest.setAmount(message.getAmount());
        createChargeRequest.setCurrency(message.getCurrency());
        createChargeRequest.setOrder(message.getPaymentGuid());

        try {
            ChargeResponse chargeResponse =
                    xPaymentProviderGateway.createCharge(createChargeRequest);

            log.info("Payment request with paymentGuid - {} is sent for payment processing. " +
                            "Current status - ", chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));

            responseMessage.setOccurredAt(OffsetDateTime.now());
            asyncSender.send(responseMessage);

        } catch (RestClientException ex) {

            log.error("Error in time of sending payment request with paymentGuid - {}",
                    message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(OffsetDateTime.now());
            asyncSender.send(responseMessage);
        }
    }

}
