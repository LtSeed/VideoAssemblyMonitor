package nusri.fyp.demo.roboflow.data.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents the metadata of a video frame associated with the output image.
 * This class stores information about the video from which the frame was captured, including the video identifier,
 * frame number, timestamp, and frame rate details.
 * Example of the expected metadata:
 * <pre>
 * "video_identifier": "video123",
 * "frame_number": 456,
 * "frame_timestamp": "00:05:30",
 * "fps": 30.0,
 * "measured_fps": 29.8,
 * "comes_from_video_file": true
 * </pre>
 */
@Data
public class VideoMetadata {

    /**
     * The identifier of the video from which the frame was captured.
     */
    @JsonProperty("video_identifier")
    private String videoIdentifier;

    /**
     * The number of the current frame within the video.
     */
    @JsonProperty("frame_number")
    private int frameNumber;

    /**
     * The timestamp of the current frame in the video.
     */
    @JsonProperty("frame_timestamp")
    private String frameTimestamp;

    /**
     * The frame rate (frames per second) of the video.
     */
    private double fps;

    /**
     * The measured frame rate of the video, which may differ from the nominal frame rate.
     * This field may be null if not available.
     */
    @JsonProperty("measured_fps")
    private Double measuredFps; // Nullable

    /**
     * Indicates whether the frame comes from a video file.
     * This field may be null if not available.
     */
    @JsonProperty("comes_from_video_file")
    private Boolean comesFromVideoFile; // Nullable
}
