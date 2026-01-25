package nl.pink.mocks.brp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class SimpleResponseDelayInterceptor implements HandlerInterceptor {

    private static final int MAX_DELAY_MS = 30_000;
    private static final ScheduledExecutorService SCHED = Executors.newScheduledThreadPool(2);
    private final String globalForcedDelayMs;

    public SimpleResponseDelayInterceptor(@Value("${environment.global-forced-delay-ms}") String globalForcedDelayMs) {
        this.globalForcedDelayMs = globalForcedDelayMs;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InterruptedException {
        String configDelayMs = StringUtils.firstNonBlank(
                request.getHeader("X-Mock-Delay"),
                request.getParameter("mockDelayMs")
                , globalForcedDelayMs);
        int delayMs = NumberUtils.toInt(configDelayMs, 0);
        /**
         * Just a simple Thread.sleep
         * Not ideal since it's actually blocking the servlet thread
         * but good enough for simple mocking purposes
         * */
        if (delayMs > 0) {
            Thread.currentThread().sleep(delayMs);
        }
        return true;
    }
}