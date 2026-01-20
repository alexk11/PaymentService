package com.iprody.dto;

import com.iprody.Message;
import com.iprody.XPaymentAdapterStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Сообщение-ответ от платёжной системы X Payment.
 * <p>
 * Содержит сведения о результате обработки платежа, включая его идентификаторы,
 * сумму, валюту, ссылку на транзакцию, статус и момент времени события.
 * <p>
 * Реализует интерфейс {@link Message}, предоставляя уникальный
 * идентификатор сообщения и метку времени его возникновения.
 */
public class XPaymentAdapterResponseMessage implements Message {

    /**
     * Уникальный идентификатор сообщения.
     */
    private UUID messageGuid;

    /**
     * Уникальный идентификатор платежа.
     */
    @Getter
    @Setter
    private UUID paymentGuid;

    /**
     * Сумма платежа.
     */
    @Setter
    @Getter
    private BigDecimal amount;

    /**
     * Валюта платежа в формате ISO 4217 (например, "USD", "EUR").
     */
    @Setter
    @Getter
    private String currency;

    /**
     * Уникальный идентификатор транзакции в платёжной системе.
     */
    @Setter
    @Getter
    private UUID transactionRefId;

    /**
     * Статус платежа.
     */
    @Setter
    @Getter
    private XPaymentAdapterStatus status;

    /**
     * Момент времени, когда событие произошло.
     */
    @Setter
    @Getter
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return messageGuid;
    }

    @Override
    public String toString() {
        return "XPaymentAdapterResponseMessage { " +
                "paymentGuid: " + paymentGuid + ", " +
                "amount: " + amount + ", " +
                "currency: " + currency + ", " +
                "status: " + status.name() + ", " +
                "occurredAt: " + occurredAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + " }";
    }

}
