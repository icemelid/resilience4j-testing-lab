
package demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.function.Supplier;

public class PaymentProviderAdapter implements PaymentRepository {

    private final PaymentClient paymentClient;
    private final CircuitBreaker circuitBreaker;

    public PaymentProviderAdapter(
            PaymentClient paymentClient,
            CircuitBreaker circuitBreaker) {

        this.paymentClient = paymentClient;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public String getPaymentStatus(Long orderId) {

        Supplier<String> operation =
                () -> paymentClient.getPaymentStatus(orderId);

        Supplier<String> protectedOperation =
                CircuitBreaker.decorateSupplier(
                        circuitBreaker,
                        operation
                );

        return protectedOperation.get();
    }
}
