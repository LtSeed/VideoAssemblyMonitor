package nusri.fyp.demo.controller;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.JvmInfoDto;
import nusri.fyp.demo.dto.SystemInfoDto;
import nusri.fyp.demo.service.SystemInfoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class that provides management endpoints for fetching system and JVM resource information.
 * <br> This controller offers APIs to retrieve system resource metrics (such as CPU, memory, disk usage)
 * and JVM-specific metrics (such as heap memory, thread count, and class loading details).
 */
@Slf4j
@RestController
@RequestMapping("/management")
public class SystemInfoController {

    private final SystemInfoService systemInfoService;
    private final ApplicationContext applicationContext;

    /**
     * Constructs a {@link SystemInfoController} with the required {@link SystemInfoService}.
     *
     * @param systemInfoService The service for retrieving system and JVM information.
     * @param applicationContext The app context to refresh the app.
     */
    public SystemInfoController(SystemInfoService systemInfoService, ApplicationContext applicationContext) {
        this.systemInfoService = systemInfoService;
        this.applicationContext = applicationContext;
    }

    /**
     * Endpoint to retrieve system resource information, including CPU usage, memory usage, and disk space.
     * <br> This endpoint calls {@link SystemInfoService#getSystemInfo()} to gather system-level data.
     *
     * @return A {@link SystemInfoDto} containing system resource information.
     */
    @GetMapping("/system-info")
    public SystemInfoDto getSystemInfo() {
        return systemInfoService.getSystemInfo();
    }

    /**
     * Endpoint to retrieve JVM-level information, such as heap memory usage, thread count, and class loading details.
     * <br> This endpoint calls {@link SystemInfoService#getJvmInfo()} to gather JVM-level data.
     *
     * @return A {@link JvmInfoDto} containing JVM resource information.
     */
    @GetMapping("/jvm-info")
    public JvmInfoDto getJvmInfo() {
        return systemInfoService.getJvmInfo();
    }

    /**
     * Endpoint to test connection.
     *
     * @return pa
     */
    @GetMapping("/ping")
    public String ping() {
        return "pa";
    }

    /**
     * Endpoint to gracefully shut down the entire Spring Boot application.
     * NOTE: Use with caution in production; consider securing this endpoint.
     * @return a sign.
     */
    @GetMapping("/shutdown")
    public String shutdown() {
        log.warn("Received request to shut down application.");
        // First exit the Spring context
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        // Then tell the JVM to halt
        System.exit(exitCode);
        return "Shutting down application...";
    }

    /**
     * Endpoint to trigger a hot reload (restart) via Spring DevTools.
     * NOTE: This works only if DevTools are on the classpath and is typically used in development.
     * @return a sign.
     */
    @GetMapping("/hot-reload")
    public String hotReload() {
        log.warn("Received request to hot reload application.");
        Restarter.getInstance().restart();
        return "Application is restarting (DevTools) ...";
    }
}
