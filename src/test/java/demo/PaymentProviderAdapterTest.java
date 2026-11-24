package demo;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class PaymentProviderAdapterTest {

        /*
1	Un fallo y circuito cerrado	Número mínimo de llamadas
2	Dos fallos y circuito abierto	Umbral de fallos
3	Un fallo y un éxito con umbral del 60 %	Cálculo de la tasa
4	Tercera llamada rechazada	CallNotPermittedException
5	Recuperación tras OPEN	Estado HALF_OPEN
        */

        @Test
        void shouldRemainClosedAfterOneFailureAndOneSuccess() {
                PaymentClient client = mock(PaymentClient.class);

                CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .failureRateThreshold(60)
                        .build();

                CircuitBreaker circuitBreaker =
                        CircuitBreaker.of("mixedResults", config);

                PaymentProviderAdapter adapter =
                        new PaymentProviderAdapter(client, circuitBreaker);

                when(client.getPaymentStatus(100L))
                        .thenThrow(new PaymentProviderException("HTTP 503"))
                        .thenReturn("PAID");

                PaymentProviderException ex1 = assertThrows(
                PaymentProviderException.class,
                () -> adapter.getPaymentStatus(100L)
                );
                System.out.println("LLAMADA 1: " + ex1.getMessage());  

                assertEquals("PAID", adapter.getPaymentStatus(100L));

                assertEquals(
                        CircuitBreaker.State.CLOSED,
                        circuitBreaker.getState()
                );

                System.out.println("ESTADO FINAL: " + circuitBreaker.getState());

                verify(client, times(2)).getPaymentStatus(100L);
        }





        // @Test
        void shouldRemainClosedAfterOneFailure() {
        PaymentClient client = mock(PaymentClient.class);

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(2)
                .minimumNumberOfCalls(2)
                .failureRateThreshold(50)
                .build();

        CircuitBreaker circuitBreaker =
                CircuitBreaker.of("singleFailure", config);

        PaymentProviderAdapter adapter =
                new PaymentProviderAdapter(client, circuitBreaker);

        when(client.getPaymentStatus(100L))
                .thenThrow(new PaymentProviderException("HTTP 503"));

        /*
        assertThrows(
                PaymentProviderException.class,
                () -> adapter.getPaymentStatus(100L)
        );
        */

                PaymentProviderException ex1 = assertThrows(
                PaymentProviderException.class,
                () -> adapter.getPaymentStatus(100L)
                );
                System.out.println("LLAMADA 1: " + ex1.getMessage());        

        assertEquals(
                CircuitBreaker.State.CLOSED,
                circuitBreaker.getState()
        );

                System.out.println("ESTADO FINAL: " + circuitBreaker.getState());

        verify(client, times(1)).getPaymentStatus(100L);
        }





    // @Test
    void shouldOpenCircuitAfterTwoFailures() {

        // 1. Creamos el cliente simulado
        PaymentClient client = mock(PaymentClient.class);

        // 2. Configuramos el CircuitBreaker
        CircuitBreakerConfig config =
                CircuitBreakerConfig.custom()
                        .slidingWindowSize(2)
                        .minimumNumberOfCalls(2)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(
                                Duration.ofSeconds(30))
                        .build();

        CircuitBreaker circuitBreaker =
                CircuitBreaker.of("paymentCircuitBreaker", config);

        // 3. Creamos el adaptador
        PaymentProviderAdapter adapter =
                new PaymentProviderAdapter(client, circuitBreaker);

        // 4. Simulamos dos fallos del proveedor
        when(client.getPaymentStatus(100L))
                .thenThrow(new PaymentProviderException("HTTP 503"))
                .thenThrow(new PaymentProviderException("HTTP 503"));

                PaymentProviderException ex1 = assertThrows(
                PaymentProviderException.class,
                () -> adapter.getPaymentStatus(100L)
                );
                System.out.println("LLAMADA 1: " + ex1.getMessage());

                        PaymentProviderException ex2 = assertThrows(
                        PaymentProviderException.class,
                        () -> adapter.getPaymentStatus(100L)
                        );

                        System.out.println("LLAMADA 2: " + ex2.getMessage());

                assertEquals(
                CircuitBreaker.State.OPEN,
                circuitBreaker.getState()
                );

                // 7. El circuito debe haberse abierto
                assertEquals(
                        CircuitBreaker.State.CLOSED,
                        circuitBreaker.getState()
                );

                                CallNotPermittedException ex3 = assertThrows(
                                        CallNotPermittedException.class,
                                        () -> adapter.getPaymentStatus(100L)
                                );

                System.out.println("LLAMADA 3: " + ex3.getClass().getSimpleName());

                System.out.println("ESTADO FINAL: " + circuitBreaker.getState());

        // 9. El cliente externo solo debe haberse llamado dos veces
        verify(client, times(2)).getPaymentStatus(100L);
    }
}