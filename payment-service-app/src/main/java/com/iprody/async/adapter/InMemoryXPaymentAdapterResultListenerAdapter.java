package com.iprody.async.adapter;

import com.iprody.async.AsyncListener;
import com.iprody.async.message.MessageHandler;
import com.iprody.async.message.XPaymentAdapterResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryXPaymentAdapterResultListenerAdapter
        implements AsyncListener<XPaymentAdapterResponseMessage> {

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    public InMemoryXPaymentAdapterResultListenerAdapter(@Lazy MessageHandler<XPaymentAdapterResponseMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterResponseMessage msg) {
        log.info("In onMessage, msg = {}", msg.toString());
        handler.handle(msg);
    }

}
