package com.iprody.async.message;

import com.iprody.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("In handle, message = {}", message.toString());
        paymentService.updateStatus(message.getPaymentGuid(), message.getStatus().asPaymentStatus());
    }
}
