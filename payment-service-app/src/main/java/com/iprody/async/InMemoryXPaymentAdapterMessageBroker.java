package com.iprody.async;

import com.iprody.async.adapter.XPaymentAdapterStatus;
import com.iprody.async.message.XPaymentAdapterRequestMessage;
import com.iprody.async.message.XPaymentAdapterResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;

@Slf4j
@Service
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        log.info("In send method. Got incoming request {}", request.toString());

        UUID txId = UUID.randomUUID();

        if (request.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
            scheduler.schedule(() -> emit(request, txId,
                    XPaymentAdapterStatus.PROCESSING), 0, TimeUnit.SECONDS);

            scheduler.schedule(() -> emit(request, txId,
                    XPaymentAdapterStatus.PROCESSING), 10, TimeUnit.SECONDS);

            scheduler.schedule(() -> emit(request, txId,
                    XPaymentAdapterStatus.SUCCEEDED), 20, TimeUnit.SECONDS);
        } else {
            scheduler.schedule(() -> emit(request, txId,
                    XPaymentAdapterStatus.CANCELED), 0, TimeUnit.SECONDS);
        }
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId, XPaymentAdapterStatus status) {
        log.info("In emit, status = {}", status.name());
        XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(status);
        result.setOccurredAt(OffsetDateTime.now());
        log.info("In emit, sending result {}", result);
        resultListener.onMessage(result);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

}
