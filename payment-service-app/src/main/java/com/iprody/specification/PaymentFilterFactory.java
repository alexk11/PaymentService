package com.iprody.specification;

import com.iprody.persistence.PaymentEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PaymentFilterFactory {

    public static Specification<PaymentEntity> fromFilter(PaymentFilter filter) {

        Specification<PaymentEntity> spec = Specification.unrestricted();

        if (StringUtils.hasText(filter.getCurrency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.getCurrency()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.getMinAmount(), filter.getMaxAmount()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.getCreatedAfter(), filter.getCreatedBefore()));
        }

        if (filter.getStatus() != null) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.getStatus()));
        }

        return spec;
    }

}
