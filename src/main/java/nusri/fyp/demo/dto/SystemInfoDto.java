package nusri.fyp.demo.dto;

import lombok.Data;

/**
 * DTO for returning system resource monitoring information, such as CPU usage, memory usage, and disk information.
 * <br> This class contains data related to system resources, which are collected from the operating system.
 */
@Data
public class SystemInfoDto {
    /**
     * The current CPU usage percentage.
     * <br> This value is between 0.0 (0%) and 1.0 (100%).
     *
     */
    private double cpuUsage;
    /**
     * The total physical memory in the system, in bytes.
     *
     */
    private long totalMemory;
    /**
     * The amount of used physical memory in the system, in bytes.
     *
     */
    private long usedMemory;
    /**
     * The amount of committed virtual memory in the system, in bytes.
     *
     */
    private long committedVirtualMemory;
    /**
     * The total swap space available in the system, in bytes.
     *
     */
    private long totalSwapSpace;
    /**
     * The free swap space available in the system, in bytes.
     *
     */
    private long freeSwapSpace;
    /**
     * The system load average over the last minute.
     * <br> This is typically used to assess the system's load.
     *
     */
    private double systemLoadAverage;
    /**
     * The total disk space in the system's root directory, in bytes.
     *
     */
    private long diskTotal;
    /**
     * The free disk space in the system's root directory, in bytes.
     *
     */
    private long diskFree;
    /**
     * The used disk space in the system's root directory, in bytes.
     *
     */
    private long diskUsed;
}
