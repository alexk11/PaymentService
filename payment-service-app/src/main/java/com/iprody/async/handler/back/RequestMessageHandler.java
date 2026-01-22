//package com.iprody.async.handler.back;
//
//import com.iprody.api.AsyncSender;
//import com.iprody.api.XPaymentAdapterStatus;
//import handler.async.com.iprody.api.MessageHandler;
//import dto.com.iprody.api.XPaymentAdapterRequestMessage;
//import dto.com.iprody.api.XPaymentAdapterResponseMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import jakarta.annotation.PreDestroy;
//import org.springframework.stereotype.Component;
//import java.time.OffsetDateTime;
//import java.util.UUID;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//
//@Slf4j
//@Component
//public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
//
//    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
//    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//    @Autowired
//    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender) {
//        this.sender = sender;
//    }
//
//    @Override
//    public void handle(XPaymentAdapterRequestMessage message) {
//        scheduler.schedule(() -> {
//            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
//            responseMessage.setPaymentGuid(message.getPaymentGuid());
//            responseMessage.setAmount(message.getAmount());
//            responseMessage.setCurrency(message.getCurrency());
//            responseMessage.setStatus(XPaymentAdapterStatus.SUCCEEDED);
//            responseMessage.setTransactionRefId(UUID.randomUUID());
//            responseMessage.setOccurredAt(OffsetDateTime.now());
//
//            log.info("In request handle, sending response message {}", responseMessage);
//            sender.send(responseMessage);
//        }, 10, TimeUnit.SECONDS);
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        scheduler.shutdown();
//    }
//
//}
