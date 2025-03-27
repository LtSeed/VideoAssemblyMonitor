package nusri.fyp.demo.dto;

import lombok.Data;

/**
 * DTO for returning JVM-level information such as memory usage, thread counts, and class loading details.
 * <br> This class provides monitoring data related to the JVM, including heap memory usage, thread count,
 * and class loading statistics.
 */
@Data
public class JvmInfoDto {
    /**
     * The amount of heap memory used in the JVM, in bytes.
     *
     */
    private long heapUsed;

    /**
     * The maximum amount of heap memory available in the JVM, in bytes.
     *
     */
    private long heapMax;

    /**
     * The amount of non-heap memory used in the JVM, in bytes.
     *
     */
    private long nonHeapUsed;

    /**
     * The current number of active threads in the JVM.
     *
     */
    private int threadCount;

    /**
     * The peak number of threads ever created in the JVM.
     * <br> This is the maximum thread count the JVM has reached during its execution.
     *
     */
    private int peakThreadCount;

    /**
     * The current number of classes loaded in the JVM.
     *
     */
    private long loadedClassCount;

    /**
     * The total number of classes loaded, including unloaded classes.
     *
     */
    private long totalLoadedClassCount;

    /**
     * The number of classes that have been unloaded by the JVM.
     *
     */
    private long unloadedClassCount;
}
