
package demo;

import io.github.resilience4j.retry.Retry;

import java.util.function.Supplier;

public class OrderService {

    private final PaymentClient paymentClient;
    private final Retry retry;

    public OrderService(PaymentClient paymentClient, Retry retry) {
        this.paymentClient = paymentClient;
        this.retry = retry;
    }

    public String getPaymentStatus(Long orderId) {

        Supplier<String> operation =
                () -> paymentClient.getPaymentStatus(orderId);

        Supplier<String> protectedOperation =
                Retry.decorateSupplier(retry, operation);

        return protectedOperation.get();
    }
}
