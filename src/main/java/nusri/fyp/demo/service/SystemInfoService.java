package nusri.fyp.demo.service;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.dto.JvmInfoDto;
import nusri.fyp.demo.dto.SystemInfoDto;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.*;

/**
 * Service class for fetching system-level and JVM-level resource information, including CPU, memory, and thread statistics.
 * <br> This service interacts with the JVM and operating system to gather data on system resources.
 */
@Service
@Slf4j
public class SystemInfoService {

    /**
     * Retrieves system resource information, including CPU usage, memory usage, and disk space.
     * <br> This method uses the {@link OperatingSystemMXBean} to collect details on the system's hardware.
     *
     * @return A {@link SystemInfoDto} containing system resource data.
     */
    public SystemInfoDto getSystemInfo() {
        SystemInfoDto dto = new SystemInfoDto();

        // Get the OperatingSystemMXBean to access CPU, memory, and disk info.
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // CPU usage (0.0 = 0%, 1.0 = 100%)
        double cpuUsage = osBean.getCpuLoad();
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }
        dto.setCpuUsage(cpuUsage);

        // System memory details
        long totalPhysicalMemory = osBean.getTotalMemorySize();
        long freePhysicalMemory = osBean.getFreeMemorySize();
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
        dto.setTotalMemory(totalPhysicalMemory);
        dto.setUsedMemory(usedPhysicalMemory);

        // System load average over the last minute
        dto.setSystemLoadAverage(osBean.getSystemLoadAverage());

        // Virtual memory information
        dto.setCommittedVirtualMemory(osBean.getCommittedVirtualMemorySize());
        dto.setFreeSwapSpace(osBean.getFreeSwapSpaceSize());
        dto.setTotalSwapSpace(osBean.getTotalSwapSpaceSize());

        // Disk information (example using the root directory)
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        dto.setDiskTotal(totalSpace);
        dto.setDiskFree(freeSpace);
        dto.setDiskUsed(totalSpace - freeSpace);

        return dto;
    }

    /**
     * Retrieves JVM-level information, including heap memory usage, thread count, and class loading statistics.
     * <br> This method collects JVM-related data using {@link MemoryMXBean}, {@link ThreadMXBean}, and
     * {@link ClassLoadingMXBean}.
     *
     * @return A {@link JvmInfoDto} containing JVM resource data.
     */
    public JvmInfoDto getJvmInfo() {
        JvmInfoDto dto = new JvmInfoDto();

        // MemoryMXBean - Retrieves memory usage for heap and non-heap memory
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        dto.setHeapUsed(heapUsage.getUsed());
        dto.setHeapMax(heapUsage.getMax());
        dto.setNonHeapUsed(nonHeapUsage.getUsed());

        // ThreadMXBean - Retrieves thread count statistics
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        dto.setThreadCount(threadMXBean.getThreadCount());
        dto.setPeakThreadCount(threadMXBean.getPeakThreadCount());

        // ClassLoadingMXBean - Retrieves class loading statistics
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        dto.setLoadedClassCount(classLoadingMXBean.getLoadedClassCount());
        dto.setTotalLoadedClassCount(classLoadingMXBean.getTotalLoadedClassCount());
        dto.setUnloadedClassCount(classLoadingMXBean.getUnloadedClassCount());

        return dto;
    }
}
