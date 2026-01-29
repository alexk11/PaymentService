package com.iprody.specification;

import com.iprody.api.PaymentStatus;
import com.iprody.persistence.PaymentEntity;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.Instant;


public final class PaymentSpecifications {

    public static Specification<PaymentEntity> hasCurrency(String currency) {
        return (root, query, cb) -> cb.equal(root.get("currency"), currency);
    }

    public static Specification<PaymentEntity> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get("amount"), min, max);
    }

    public static Specification<PaymentEntity> createdBetween(Instant after, Instant before) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), after, before);
    }

    public static Specification<PaymentEntity> hasStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

}
