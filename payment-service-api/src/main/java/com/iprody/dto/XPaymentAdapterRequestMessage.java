package com.iprody.dto;

import com.iprody.Message;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Сообщение-запрос для платёжной системы X Payment.
 * <p>
 * Используется для передачи информации о платеже, включая идентификаторы,
 * сумму, валюту и время возникновения события.
 * <p>
 * Реализует интерфейс {@link Message}, обеспечивая уникальный
 * идентификатор сообщения и метку времени его возникновения.
 */
public class XPaymentAdapterRequestMessage implements Message {

    /**
     * Уникальный идентификатор платежа.
     */
    @Getter
    @Setter
    private UUID paymentGuid;

    /**
     * Сумма платежа.
     */
    @Getter
    @Setter
    private BigDecimal amount;

    /**
     * Валюта платежа в формате ISO 4217 (например, "USD", "EUR").
     */
    @Getter
    @Setter
    private String currency;

    /**
     * Момент времени, когда событие произошло.
     */
    @Getter
    @Setter
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return paymentGuid;
    }

    @Override
    public String toString() {
        return "XPaymentAdapterRequestMessage { " +
                "paymentGuid: " + paymentGuid + ", " +
                "amount: " + amount + ", " +
                "currency: " + currency + ", " +
                "occurredAt: " + occurredAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + " }";
    }

}
