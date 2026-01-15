package com.iprody.async;

import com.iprody.service.PaymentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InMemoryXPaymentAdapterResultListenerAdapter
        implements AsyncListener<XPaymentAdapterResponseMessage> {

    private static final Logger log = LoggerFactory.getLogger(InMemoryXPaymentAdapterResultListenerAdapter.class);

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;
    //private final MessageHandlerImpl handler;
    //private final PaymentService paymentService;

    @Override
    public void onMessage(XPaymentAdapterResponseMessage msg) {
        log.info("In onMessage, msg = {}", msg.toString());
        //new MessageHandlerImpl().handle(msg);
        handler.handle(msg); // handle method implementation ??
    }

}
