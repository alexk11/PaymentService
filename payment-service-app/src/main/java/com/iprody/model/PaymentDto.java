package com.iprody.model;

import com.iprody.persistence.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID guid;
    private UUID inquiryRefId;
    private BigDecimal amount;
    private String currency;
    private UUID transactionRefId;
    private PaymentStatus status;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ guid: ").append(guid).append(", ");
        sb.append("amount: ").append(amount).append(", ");
        sb.append("currency: ").append(currency).append(", ");
        sb.append("note: ").append(note).append(" ]");
        return sb.toString();
    }
}
