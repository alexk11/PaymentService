package com.iprody.async;

import com.iprody.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private static final Logger log =
            LoggerFactory.getLogger(MessageHandlerImpl.class);

    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

//    public MessageHandlerImpl() {
//    }

//    public MessageHandlerImpl(PaymentService paymentService) {
//        this.paymentService = paymentService;
//    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("In handle, message = {}", message.toString());
        paymentService.updateStatus(message.getPaymentGuid(), message.getStatus().asPaymentStatus());
    }
}
