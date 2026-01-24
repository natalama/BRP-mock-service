package nl.pink.mocks.brp.config;

import nl.pink.mocks.brp.interceptor.ForcedStatusInterceptor;
import nl.pink.mocks.brp.interceptor.SimpleResponseDelayInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SimpleResponseDelayInterceptor simpleResponseDelayInterceptor;
    private final ForcedStatusInterceptor forcedStatusInterceptor;

    public WebConfig(SimpleResponseDelayInterceptor simpleResponseDelayInterceptor, ForcedStatusInterceptor forcedStatusInterceptor) {
        this.simpleResponseDelayInterceptor = simpleResponseDelayInterceptor;
        this.forcedStatusInterceptor = forcedStatusInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //NB: Apply correct status first before checking if there are delays
        registry.addInterceptor(forcedStatusInterceptor);
        registry.addInterceptor(simpleResponseDelayInterceptor);
    }


}
