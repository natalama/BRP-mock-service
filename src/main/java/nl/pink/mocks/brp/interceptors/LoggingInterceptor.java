package nl.pink.mocks.brp.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String START_TIME = "loggingStartTime";
    private static final List<String> SENSITIVE_HEADERS = new ArrayList<>(Arrays.asList("authorization", "cookie", "set-cookie"));
    private static final Pattern NUMERIC_PATH_SEGMENT = Pattern.compile("(?<=/)(\\d+)(?=(?:/|$))");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);

        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (SENSITIVE_HEADERS.contains(name.toLowerCase())) continue;
            headers.append(name).append("=").append(request.getHeader(name)).append(", ");
        }

        log.info("Incoming request method={} path={} query={} headers={}",
                request.getMethod(),
                maskBsnInPath(request.getRequestURI()),
                request.getQueryString(),
                headers.length() > 0 ? headers.toString() : "-");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        Long start = (Long) request.getAttribute(START_TIME);
        long elapsed = start != null ? System.currentTimeMillis() - start : -1;

        String path = request.getRequestURI();
        String maskedPath = maskBsnInPath(path);

        if (ex != null) {
            log.error("Request failed method={} path={} status={} elapsedMs={} error={}",
                    request.getMethod(), maskedPath, response.getStatus(), elapsed, ex.toString(), ex);
        } else {
            log.info("Request completed method={} path={} status={} elapsedMs={}",
                    request.getMethod(), maskedPath, response.getStatus(), elapsed);
        }
    }

    private String maskBsnInPath(String path) {
        if (path == null) {
            return null;
        }
        return NUMERIC_PATH_SEGMENT.matcher(path).replaceAll(mr -> {
            String digit = mr.group(1);
            int numberOfDigits = digit.length();
            return numberOfDigits <= 3
                    ? StringUtils.repeat('*', numberOfDigits)
                    : StringUtils.repeat('*', numberOfDigits - 3) + StringUtils.right(digit, 3);
        });
    }

}