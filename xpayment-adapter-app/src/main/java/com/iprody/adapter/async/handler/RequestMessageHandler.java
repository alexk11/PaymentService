package com.iprody.adapter.async.handler;

import com.iprody.api.AsyncSender;
import com.iprody.api.XPaymentAdapterStatus;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void handle(XPaymentAdapterRequestMessage requestMessage) {

        scheduler.schedule(() -> {
            XPaymentAdapterResponseMessage message = new XPaymentAdapterResponseMessage();
            message.setPaymentGuid(requestMessage.getPaymentGuid());
            message.setAmount(requestMessage.getAmount());
            message.setCurrency(requestMessage.getCurrency());
            message.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            message.setTransactionRefId(UUID.randomUUID());
            message.setOccurredAt(OffsetDateTime.from(LocalDateTime.now()));

            log.info("Sending XPayment Adapter message: {}", message.getMessageId());
            sender.send(message);

        }, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

}
