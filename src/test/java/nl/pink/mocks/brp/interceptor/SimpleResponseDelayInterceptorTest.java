package nl.pink.mocks.brp.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleResponseDelayInterceptorTest {

    @Test
    void headerDelay_shouldSleepAtLeastHeaderValue() throws Exception {
        var interceptor = new SimpleResponseDelayInterceptor("0");
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        int expectedMs = 50;
        request.addHeader("X-Mock-Delay", String.valueOf(expectedMs));

        long start = System.currentTimeMillis();
        boolean allowed = interceptor.preHandle(request, response, new Object());
        long elapsedMs = (System.currentTimeMillis() - start);

        assertThat(allowed).isTrue();
        assertThat(elapsedMs).isGreaterThanOrEqualTo(expectedMs);
    }

    @Test
    void parameterDelay_takesPrecedenceOverGlobal() throws Exception {
        // global says 100ms, parameter says 30ms -> effective 30ms
        var interceptor = new SimpleResponseDelayInterceptor("100");
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        int paramMs = 30;
        request.setParameter("mockDelayMs", String.valueOf(paramMs));

        long start = System.currentTimeMillis();
        interceptor.preHandle(request, response, new Object());
        long elapsedMs = (System.currentTimeMillis() - start);

        assertThat(elapsedMs).isGreaterThanOrEqualTo(paramMs);
    }

    @Test
    void invalidDelay_shouldNotSleep() throws Exception {
        // global is invalid, no header/param -> no delay
        var interceptor = new SimpleResponseDelayInterceptor("not-a-number");
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        long start = System.currentTimeMillis();
        interceptor.preHandle(request, response, new Object());
        long elapsedMs = System.currentTimeMillis() - start;

        assertThat(elapsedMs).isLessThan(50);
    }
}
