package com.iprody.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentDto {
    private Long paymentId;
    private String amount;
}
