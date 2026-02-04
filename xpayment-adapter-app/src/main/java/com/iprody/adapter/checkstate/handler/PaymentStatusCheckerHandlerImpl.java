package com.iprody.adapter.checkstate.handler;

import com.iprody.adapter.api.XPaymentProviderGateway;
import com.iprody.adapter.dto.CreateChargeResponseDto;
import com.iprody.adapter.mapper.XPaymentMapper;
import com.iprody.api.AsyncSender;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.iprody.api.XPaymentAdapterStatus.CANCELED;
import static com.iprody.api.XPaymentAdapterStatus.SUCCEEDED;


@Service
@RequiredArgsConstructor
public class PaymentStatusCheckerHandlerImpl implements PaymentStatusCheckHandler {

    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGateway gateway;
    private final XPaymentMapper mapper;

    @Override
    public boolean handle(UUID id) {
        CreateChargeResponseDto dto = gateway.retrieveCharge(id);

        if (dto != null && (dto.getStatus().equals(SUCCEEDED.name()) || dto.getStatus().equals(CANCELED.name()))) {
            asyncSender.send(mapper.responseDtoToKafkaMessage(dto));
            return true;
        } else {
            return false;
        }
    }

}
