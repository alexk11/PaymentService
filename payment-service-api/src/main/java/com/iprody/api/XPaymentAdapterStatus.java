package com.iprody.api;

/**
 * Статусы в которых может пребывать платежная транзакция X Payment Adapter.
 */
public enum XPaymentAdapterStatus {
    PROCESSING {
        @Override
        public PaymentStatus asPaymentStatus() {
            return PaymentStatus.RECEIVED;
        }
    },
    CANCELED {
        @Override
        public PaymentStatus asPaymentStatus() {
            return PaymentStatus.DECLINED;
        }
    },
    SUCCEEDED {
        @Override
        public PaymentStatus asPaymentStatus() {
            return PaymentStatus.APPROVED;
        }
    };

    public PaymentStatus asPaymentStatus() {
        return PaymentStatus.RECEIVED; // default mapping
    }

}
