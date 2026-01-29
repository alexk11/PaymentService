package com.iprody.api.dto;

import com.iprody.api.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Getter
@Setter
@ToString
public class XPaymentAdapterRequestMessage implements Message {

    /**
     * Уникальный идентификатор платежа.
     */
    private UUID paymentGuid;

    /**
     * Сумма платежа.
     */
    private BigDecimal amount;

    /**
     * Валюта платежа в формате ISO 4217 (например, "USD", "EUR").
     */
    private String currency;

    /**
     * Момент времени, когда событие произошло.
     */
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return paymentGuid;
    }

}
