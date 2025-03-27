/**
 * <p>
 * The <code>nusri.fyp.demo.controller</code> package contains the controllers responsible
 * for handling HTTP requests in the demo application. These controllers act as intermediaries
 * between the client and the backend services, managing the flow of data and ensuring that
 * the appropriate responses are returned.
 * </p>
 *
 * <p>
 * Each controller in this package is designed to process different types of requests related
 * to video processing, configuration management, system information, and state machine operations.
 * The controllers interact with services and repositories to perform the required business logic
 * and return the results to the client.
 * </p>
 *
 * <p>
 * Key components in this package include:
 * </p>
 * <ul>
 *     <li>{@link nusri.fyp.demo.controller.VideoController} - Handles video-related endpoints,
 *         such as uploading videos, processing, retrieving progress, and downloading the processed videos.</li>
 *     <li>{@link nusri.fyp.demo.controller.ConfigController} - Manages configuration settings for various services,
 *         including updating Python server settings, Roboflow server settings, and model presets.</li>
 *     <li>{@link nusri.fyp.demo.controller.SystemInfoController} - Provides system resource and JVM-level information
 *         such as CPU, memory, disk usage, and other system statistics.</li>
 *     <li>{@link nusri.fyp.demo.controller.ReviewController} - Retrieves and manages state machine logs,
 *         including logs of state transitions and action executions.</li>
 *     <li>{@link nusri.fyp.demo.controller.WorkInfoController} - Handles work session-related endpoints
 *         such as retrieving progress and alarms for a specific user session.</li>
 * </ul>
 *
 * <p>
 * These controllers follow RESTful principles and allow users to interact with various
 * components of the application, such as uploading videos, reviewing logs, and managing configurations.
 * </p>
 *
 * <p>
 * The main tasks of this package include:
 * </p>
 * <ul>
 *     <li>Providing REST endpoints for interacting with video processing tasks.</li>
 *     <li>Handling configuration updates for system services and presets.</li>
 *     <li>Returning system and JVM resource usage statistics for monitoring purposes.</li>
 *     <li>Managing user sessions and state machine logs for workflow tracking.</li>
 * </ul>
 *
 * <p>
 * The package also supports integration with the Roboflow API for performing image processing tasks such as object detection,
 * segmentation, and inference. For detailed API usage and implementation, refer to the respective controller classes.
 * </p>
 *
 * <p>
 * For more details on how each controller handles specific requests, refer to the OpenAPI documentation linked to each controller's API.
 * </p>
 * @see <a href="https://www.postman.com/satellite-astronaut-90149468/workspace/my-workspace/collection/42644368-060efa78-7326-4886-98ec-078ad3063e25?action=share&creator=42644368">more REST API details on Postman</a>.
 *
 */
package nusri.fyp.demo.controller;
