package demo;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;

import demo.PaymentProviderException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class OrderServiceTest {

        /*
        
        Añadir una espera entre reintento

         */
        @Test
        void shouldWaitBetweenRetries() {

        PaymentClient client = mock(PaymentClient.class);

        Retry retry = Retry.of(
                "paymentRetry",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ofMillis(500))
                        .retryExceptions(TransientPaymentException.class)
                        .build()
        );

        OrderService service = new OrderService(client, retry);

        when(client.getPaymentStatus(100L))
                .thenThrow(new TransientPaymentException("Temporary error"))
                .thenThrow(new TransientPaymentException("Temporary error"))
                .thenReturn("PAID");

        long start = System.nanoTime();

        String result = service.getPaymentStatus(100L);

        long elapsedMs =
                (System.nanoTime() - start) / 1_000_000;

        System.out.println("RESULTADO: " + result);
        System.out.printf("TIEMPO TOTAL: %.3f s%n", elapsedMs / 1_000_000_000.0);

        assertEquals("PAID", result);

        verify(client, times(3)).getPaymentStatus(100L);

        // Las dos esperas suman aproximadamente 1 segundo.
        assertTrue(elapsedMs >= 900,
                "El tiempo debería incluir las esperas entre reintentos");
        }

        
        


        /*
        @Test
        void shouldFailAfterMaximumAttempts() {

        PaymentClient client = mock(PaymentClient.class);

        Retry retry = Retry.of(
                "paymentRetry",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ZERO)
                        .retryExceptions(
                                TransientPaymentException.class)
                        .build()
        );

        OrderService service = new OrderService(client, retry);


        when(client.getPaymentStatus(100L))
                .thenThrow(new TransientPaymentException("Temporary error"))
                .thenThrow(new IllegalArgumentException("Incorrect Argument"));        

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.getPaymentStatus(100L)
        );

        System.out.println("EXCEPCIÓN FINAL: " + ex.getMessage());                

        verify(client, times(2)).getPaymentStatus(100L);
        
        }
        */






        /*
       @Test
        void shouldFailAfterMaximumAttempts() {

        PaymentClient client = mock(PaymentClient.class);

        Retry retry = Retry.of(
                "paymentRetry",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ZERO)
                        .retryExceptions(
                                TransientPaymentException.class)
                        .build()
        );

        OrderService service = new OrderService(client, retry);

        when(client.getPaymentStatus(100L)).thenAnswer(invocation -> {
        System.out.println("LLAMADA AL CLIENTE");
        throw new TransientPaymentException("Temporary error");
        });

        assertThrows(
                TransientPaymentException.class,
                () -> service.getPaymentStatus(100L)
        );
        

        verify(client, times(3)).getPaymentStatus(100L);
        }
*/






/*
         @Test
    void shouldRetryAndSucceedOnSecondAttempt() {

        PaymentClient client = mock(PaymentClient.class);

        Retry retry = Retry.of(
                "paymentRetry",
                RetryConfig.custom()
                        .maxAttempts(3)
                        .waitDuration(Duration.ZERO)
                        .retryExceptions(
                                TransientPaymentException.class)
                        .build()
        );

        retry.getEventPublisher()
            .onRetry(event -> System.out.println(
                "[RETRY] Intento fallido. Número de intento: "
                + event.getNumberOfRetryAttempts()
                + " | Error: "
                + event.getLastThrowable().getMessage()
        ));

        OrderService service = new OrderService(client, retry);

        // Primera llamada: falla.
        // Segunda llamada: funciona.
        when(client.getPaymentStatus(100L))
                .thenThrow(new TransientPaymentException(
                        "Temporary error"))
                .thenReturn("PAID");

                

        // Inicio
        String result = service.getPaymentStatus(100L);

        System.err.println("RESULTADO DEL TEST: " + result);

        assertEquals("PAID", result);

        // Verificamos que hubo exactamente dos llamadas
        verify(client, times(2)).getPaymentStatus(100L);
    }
        */
}