package nusri.fyp.demo.controller;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.JvmInfoDto;
import nusri.fyp.demo.dto.SystemInfoDto;
import nusri.fyp.demo.service.SystemInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class that provides management endpoints for fetching system and JVM resource information.
 * <br> This controller offers APIs to retrieve system resource metrics (such as CPU, memory, disk usage)
 * and JVM-specific metrics (such as heap memory, thread count, and class loading details).
 */
@Slf4j
@RestController
public class SystemInfoController {

    private final SystemInfoService systemInfoService;

    /**
     * Constructs a {@link SystemInfoController} with the required {@link SystemInfoService}.
     *
     * @param systemInfoService The service for retrieving system and JVM information.
     */
    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    /**
     * Endpoint to retrieve system resource information, including CPU usage, memory usage, and disk space.
     * <br> This endpoint calls {@link SystemInfoService#getSystemInfo()} to gather system-level data.
     *
     * @return A {@link SystemInfoDto} containing system resource information.
     */
    @GetMapping("/management/system-info")
    public SystemInfoDto getSystemInfo() {
        return systemInfoService.getSystemInfo();
    }

    /**
     * Endpoint to retrieve JVM-level information, such as heap memory usage, thread count, and class loading details.
     * <br> This endpoint calls {@link SystemInfoService#getJvmInfo()} to gather JVM-level data.
     *
     * @return A {@link JvmInfoDto} containing JVM resource information.
     */
    @GetMapping("/management/jvm-info")
    public JvmInfoDto getJvmInfo() {
        return systemInfoService.getJvmInfo();
    }
}
