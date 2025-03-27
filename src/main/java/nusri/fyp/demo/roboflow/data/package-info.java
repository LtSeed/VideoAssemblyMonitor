/**
 * <p>
 * The <code>nusri.fyp.demo.roboflow.data</code> package contains the data models used to represent 
 * both request and response structures for interacting with the Roboflow API. This package plays 
 * a crucial role in serializing and deserializing JSON data, allowing seamless communication with 
 * the Roboflow image processing and machine learning services.
 * </p>
 *
 * <p>
 * The key functionality in this package includes the definition of classes for image data, prediction 
 * results, and model configurations. These classes represent various stages of image processing tasks such as 
 * classification, object detection, segmentation, and feature extraction, and facilitate sending and receiving 
 * the data to and from the Roboflow API.
 * </p>
 *
 * <p>
 * The data models in this package include, but are not limited to, the following:
 * </p>
 * <ul>
 *     <li>{@link nusri.fyp.demo.roboflow.data.RoboflowRequestData} - Base class for Roboflow request data models.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.RoboflowResponseData} - Base class for Roboflow response data models.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.entity.ClassificationPrediction} - Model for classification prediction results.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.entity.Box} - Defines bounding boxes used in object detection.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.entity.InferenceRequestImage} - Represents image data for inference requests.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.entity.KeypointsPrediction} - Contains keypoint detection results.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.request.ClassificationInferenceRequest} - Request model for classification inference.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.response.ClassificationInferenceResponse} - Response model for classification inference results.</li>
 *     <li>{@link nusri.fyp.demo.roboflow.data.AnyData} - A flexible model used for raw data or response objects in JSON format.</li>
 * </ul>
 *
 * <p>
 * Some of the notable features of this package include:
 * </p>
 * <ul>
 *     <li>Serialization and deserialization logic for JSON data, making it easy to send requests and receive responses.</li>
 *     <li>Support for a wide range of image processing tasks including image classification, object detection, and segmentation.</li>
 *     <li>Flexible models such as {@link nusri.fyp.demo.roboflow.data.AnyData} that allow handling of dynamic or unknown JSON structures.</li>
 *     <li>Compatibility with the Roboflow API, supporting various endpoints for model inference, image uploads, and more.</li>
 * </ul>
 *
 * <p>
 * This package also defines request and response data models that are integral for Roboflow inference services. 
 * For example, the {@link nusri.fyp.demo.roboflow.data.request.ClipImageEmbeddingRequest} and 
 * {@link nusri.fyp.demo.roboflow.data.response.ClipEmbeddingResponse} classes define the request and response 
 * models for a specific type of image embedding service. Similarly, the {@link nusri.fyp.demo.roboflow.data.request.GazeDetectionInferenceRequest}
 * and {@link nusri.fyp.demo.roboflow.data.response.GazeDetectionInferenceResponse} classes are used for gaze detection tasks.
 * </p>
 *
 * <p>
 * All data models are designed with annotations such as {@link com.fasterxml.jackson.annotation.JsonProperty} to 
 * handle JSON property mapping, making it easier to process and transform the incoming and outgoing data.
 * </p>
 *
 * <p>
 * The package is key to integrating Roboflow's machine learning services into applications, enabling efficient 
 * communication with their endpoints for tasks like image classification, segmentation, and object detection.
 * </p>
 *
 * <p>
 * For detailed usage examples and API documentation, refer to the Roboflow official documentation: 
 * <a href="https://roboflow.com/docs">https://roboflow.com/docs</a>.
 * </p>
 */
package nusri.fyp.demo.roboflow.data;
