package nl.pink.mocks.brp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class ForcedStatusInterceptor implements HandlerInterceptor {

    private final String globalForcedCode;
    private static final Logger log = LoggerFactory.getLogger(ForcedStatusInterceptor.class);

    public ForcedStatusInterceptor(@Value("${environment.global-forced-response-status:}") String globalForcedStatus) {
        this.globalForcedCode = globalForcedStatus;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String forcedStatusCode = StringUtils.firstNonBlank(
                request.getHeader("X-Forced-Status"),
                request.getParameter("forcedMockStatus"),
                globalForcedCode);
        if (StringUtils.isNotBlank(forcedStatusCode)) {
            if (!NumberUtils.isParsable(forcedStatusCode)) {
                throw new IllegalArgumentException("Forced status code is not a valid number: " + forcedStatusCode);
            }
            HttpStatus code = HttpStatus.resolve(NumberUtils.toInt(forcedStatusCode));
            if (code == null) {
                throw new IllegalArgumentException("Forced status code is not a valid HTTP status: " + forcedStatusCode);
            }
            if (!response.isCommitted()) {
                response.setStatus(code.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"mockedStatus\":" + code.value() + "}");
                response.getWriter().flush();
                return false; // skip controller
            }
        }
        return true; // continue to controller
    }
}
