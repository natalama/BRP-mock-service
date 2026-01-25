package nl.pink.mocks.brp.config;

import nl.pink.mocks.brp.interceptor.ForcedStatusInterceptor;
import nl.pink.mocks.brp.interceptor.LoggingInterceptor;
import nl.pink.mocks.brp.interceptor.SimpleResponseDelayInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SimpleResponseDelayInterceptor simpleResponseDelayInterceptor;
    private final ForcedStatusInterceptor forcedStatusInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    public WebConfig(SimpleResponseDelayInterceptor simpleResponseDelayInterceptor,
                     ForcedStatusInterceptor forcedStatusInterceptor,
                     LoggingInterceptor loggingInterceptor) {
        this.simpleResponseDelayInterceptor = simpleResponseDelayInterceptor;
        this.forcedStatusInterceptor = forcedStatusInterceptor;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //NB: Apply correct status first before checking if there are delays
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(forcedStatusInterceptor);
        registry.addInterceptor(simpleResponseDelayInterceptor);
    }


}
