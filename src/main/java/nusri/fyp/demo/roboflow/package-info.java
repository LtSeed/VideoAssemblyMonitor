/**
 * <p>
 * The <code>nusri.fyp.demo.roboflow</code> package contains the core classes for interacting with the Roboflow API,
 * facilitating various image processing tasks such as image classification, object detection, segmentation, and
 * keypoint detection. This package is designed to enable seamless communication between your application and the
 * Roboflow inference services, including making requests and handling responses.
 * </p>
 *
 * <p>
 * The primary functionalities of this package include:
 * </p>
 * <ul>
 *     <li>Defining data models for request and response structures for different Roboflow services.</li>
 *     <li>Supporting various machine learning tasks, such as image classification, object detection, and image embeddings.</li>
 *     <li>Providing classes for serializing and deserializing JSON data to easily communicate with Roboflow's API.</li>
 *     <li>Managing image data for inference, including encoding images in base64 and handling file uploads.</li>
 * </ul>
 *
 * <p>
 * Key components in this package:
 * </p>
 * <ul>
 *     <li>{@link nusri.fyp.demo.roboflow.RoboflowConfig} - Configuration class to initialize the Roboflow service client.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.request.RequestSender} - Interface for sending HTTP requests to the Roboflow API.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.request.RequestSenderOfHttpClient} - Implementation of the RequestSender interface using Java's HttpClient.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.request.RequestSenderOfOKHttp} - Another implementation of RequestSender using OKHttp.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.RoboflowRequestData} - Base class for request data models, used to structure data sent to Roboflow.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.RoboflowResponseData} - Base class for response data models, used to handle data returned from Roboflow.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.entity} - A package containing data models for image processing results such as predictions, classifications, and bounding boxes.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.request} - Request models for specific tasks, including image classification, segmentation, and OCR inference.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.response} - Response models for handling the output of various image processing tasks.</li>
 * </ul>
 *
 * <p>
 * The package also includes classes that handle advanced use cases such as image segmentation, keypoints detection,
 * and dynamic block processing through workflows. These features are essential for applications that need to analyze
 * and process images for various machine learning tasks, including visual recognition and object tracking.
 * </p>
 *
 * <p>
 * For a more detailed understanding of how to use this package and integrate it with your application, refer to the
 * Roboflow API documentation:
 * <a href="https://roboflow.com/docs">https://roboflow.com/docs</a>.
 * </p>
 *
 * <p>
 * This package is crucial for sending, receiving, and processing image data from Roboflow’s cloud-based image processing
 * services, and it abstracts the complexity of making API calls, simplifying the integration of powerful machine
 * learning models into applications.
 * </p>
 *
 * @author Liu Binghong
 * @since 1.0
 */
package nusri.fyp.demo.roboflow;
