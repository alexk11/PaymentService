package com.iprody.async.handler;

import com.iprody.api.PaymentStatus;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import com.iprody.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentService paymentService;

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        paymentService.updateStatus(message.getPaymentGuid(), PaymentStatus.APPROVED);
    }

}
