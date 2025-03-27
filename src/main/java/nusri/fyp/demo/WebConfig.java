package nusri.fyp.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for enabling Cross-Origin Resource Sharing (CORS) in the Spring application.
 * <br> This class allows the backend to specify which frontend origins can access the API.
 */
@SuppressWarnings("HttpUrlsUsage")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mappings to allow cross-origin requests from specified domains and methods.
     * <br> This method adds mappings for all endpoints ("/**") and specifies allowed origins, HTTP methods, and headers.
     *
     * @param registry The CORS registry to register the mappings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allows all endpoints
                .allowedOriginPatterns("https://test.ltseed.cn", "http://192.168.*.*:*", "http://localhost:*") // Replace with your frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
                .allowCredentials(true)  // Allows credentials to be included in the requests
                .allowedHeaders("*");    // Allows all headers
    }
}
