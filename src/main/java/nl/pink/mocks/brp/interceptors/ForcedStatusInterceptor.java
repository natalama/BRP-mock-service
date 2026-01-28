package nl.pink.mocks.brp.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.pink.mocks.brp.constants.RequestConstants;
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

    private final String globalForcedResponseStatus;
    private static final Logger log = LoggerFactory.getLogger(ForcedStatusInterceptor.class);

    public ForcedStatusInterceptor(@Value("${environment.global-forced-response-status:}") String globalForcedStatus) {
        this.globalForcedResponseStatus = globalForcedStatus;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String forcedStatusCode = StringUtils.firstNonBlank(
                request.getParameter(RequestConstants.REQ_PARAM_MOCKED_STATUS),
                request.getHeader(RequestConstants.HEADER_MOCKED_STATUS),
                globalForcedResponseStatus);
        if (StringUtils.isNotBlank(forcedStatusCode) && !forcedStatusCode.equalsIgnoreCase("-")) {
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
