package nusri.fyp.demo.controller;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


/**
 * Controller class for handling video-related API requests, including uploading, processing, and downloading video files.
 * <br> This class exposes endpoints for uploading videos, processing videos frame by frame, retrieving processing progress,
 * and downloading the processed video.
 */
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    /**
     * Constructs the {@link VideoController} with the required {@link VideoService}.
     *
     * @param videoService The service responsible for handling video processing and uploads.
     */
    VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * Endpoint for uploading a video file and saving it on the server.
     * <br> This method receives a video file from the client, saves it to the server, and then processes the file asynchronously.
     *
     * @param videoFile The video file to be uploaded.
     * @param user The user identifier to associate the video with a specific user session.
     * @return A response entity indicating whether the video was successfully uploaded and processed.
     * @throws IOException If an I/O error occurs while uploading or saving the video file.
     */
    @SuppressWarnings("JvmTaintAnalysis")
    @PostMapping("/uploadAndSave/{user}")
    public ResponseEntity<?> uploadAndSaveVideo(@RequestParam("video") MultipartFile videoFile, @PathVariable("user") String user) throws IOException {
        ResponseEntity<String> BAD_REQUEST = videoService.uploadAndSave(videoFile, user);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        return ResponseEntity.ok("File uploaded and processed successfully");
    }

    /**
     * Endpoint for processing the uploaded video based on a preset configuration.
     * <br> This method initiates the processing of the video, where each frame is sent to the model for recognition.
     * The processing result is returned once the video has been processed.
     *
     * @param user The user identifier to associate the video processing with the correct user session.
     * @param presetName The preset name related to the model used for video processing.
     * @return A response entity containing the result of the video processing, including any errors.
     */
    @GetMapping("/proc/{user}")
    public ResponseEntity<?> procVideo(@PathVariable("user") final String user, @RequestParam("preset") String presetName) {
        return videoService.processVideo(user, presetName);
    }

    /**
     * Endpoint to get the progress of video processing by user.
     * <br> This method provides the processing progress of the video, returning a value between 0 and 1
     * representing the percentage of video frames that have been processed.
     *
     * @param user The user identifier to track the video processing progress.
     * @return A response entity containing the processing progress as a double value between 0 and 1.
     */
    @GetMapping("/progress/{user}")
    public ResponseEntity<Double> getProgress(@PathVariable("user") String user) {
        Double progress = videoService.getProgress(user);
        return ResponseEntity.ok(progress);
    }

    /**
     * Endpoint for downloading the processed video file for a specific user.
     * <br> This method retrieves the processed video file and provides it for download or streaming.
     * If the file does not exist, it returns a 404 error.
     *
     * @param user The user identifier to retrieve the correct video file associated with the user.
     * @return A response entity containing the video file for download or a 404 error if the file does not exist.
     */
    @GetMapping("/dl/{user}")
    public ResponseEntity<?> downloadVideo(@PathVariable("user") String user) {
        try {
            return videoService.getFileResponse(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();  // Handles errors and returns 500
        }
    }

    /**
     * Endpoint for processing a single image (frame) for a specific user.
     * <br> This method receives an image (as Base64 encoded data) and processes it by passing it to the model for recognition.
     *
     * @param img The Base64 encoded image data to be processed.
     * @param user The user identifier to associate the image processing with the correct user session.
     * @param timestamp The timestamp or identifier for the image frame.
     * @return A response entity indicating whether the image was successfully processed or if no image was uploaded.
     */
    @PostMapping("/img")
    public ResponseEntity<?> procImg(@RequestParam("img") String img,
                                     @RequestParam("user") String user,
                                     @RequestParam("t") String timestamp) {
        boolean No_img_file_uploaded = !videoService.processImage(img, user, timestamp);
        if (No_img_file_uploaded) return ResponseEntity.badRequest().body("No img file uploaded");

        return ResponseEntity.ok("Video uploaded and processed successfully");
    }
}

