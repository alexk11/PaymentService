package com.iprody.adapter.async.handler;

import com.iprody.adapter.dto.CreateChargeRequestDto;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.adapter.mapper.XPaymentMapper;
import com.iprody.api.AsyncSender;
import com.iprody.api.XPaymentAdapterStatus;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import com.iprody.adapter.api.XPaymentProviderGateway;
import com.iprody.adapter.checkstate.PaymentStateCheckRegistrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;


@Component
@Slf4j
public class RequestMessageHandler implements
        MessageHandler<XPaymentAdapterRequestMessage> {

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final PaymentStateCheckRegistrar paymentStateCheckRegistrar;
    private final XPaymentMapper mapper;

    @Autowired
    public RequestMessageHandler(
            XPaymentProviderGateway xPaymentProviderGateway,
            AsyncSender<XPaymentAdapterResponseMessage> asyncSender,
            PaymentStateCheckRegistrar paymentStateCheckRegistrar,
            XPaymentMapper mapper) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.asyncSender = asyncSender;
        this.paymentStateCheckRegistrar = paymentStateCheckRegistrar;
        this.mapper = mapper;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                message.getPaymentGuid(),
                message.getAmount(),
                message.getCurrency());

        CreateChargeRequestDto dto = new CreateChargeRequestDto();
        dto.setAmount(message.getAmount());
        dto.setCurrency(message.getCurrency());
        dto.setOrder(message.getPaymentGuid());

        try {
            CreateChargeResponseDto chargeResponse =
                    xPaymentProviderGateway.createCharge(dto);

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
            paymentStateCheckRegistrar.register(
                    chargeResponse.getId(),
                    chargeResponse.getOrder(),
                    chargeResponse.getAmount(),
                    chargeResponse.getCurrency()
            );
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
