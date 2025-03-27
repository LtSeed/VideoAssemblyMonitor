package nusri.fyp.demo.roboflow.data.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nusri.fyp.demo.state_machine.AbstractActionObservation;

/**
 * Represents a single prediction made in the context of an image inference.
 * <br> This class contains details of the predicted object's location, class, confidence score, and related information.
 * Example of a prediction in the response:
 * <pre>
 * "width": 938,
 * "height": 531,
 * "x": 636,
 * "y": 404.5,
 * "confidence": 0.8899440169334412,
 * "class_id": 1,
 * "class": "Action13",
 * "detection_id": "64a2389f-439b-4c07-a548-3fe5f9e4ece9",
 * "parent_id": "image"
 * </pre>
 * This class is used to represent a prediction result for a single object detected in an image, including information
 * about its dimensions, location, class, and detection details.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public final class SinglePrediction extends AbstractActionObservation {

     /**
      * Label representing the prediction.
      */
     private String label = "";

     /**
      * Confidence score for the prediction.
      */
     private Double confidence;

     /**
      * X-coordinate of the top-left corner of the bounding box.
      */
     private int x;

     /**
      * Y-coordinate of the top-left corner of the bounding box.
      */
     private int y;

     /**
      * Width of the bounding box.
      */
     private int width;

     /**
      * Height of the bounding box.
      */
     private int height;

     /**
      * Class ID associated with the predicted object.
      */
     @JsonProperty("class_id")
     private int classId;

     /**
      * Class name of the predicted object (e.g., "Action13").
      */
     @JsonProperty("class")
     private String clazz;

     /**
      * Unique detection ID for the prediction.
      */
     @JsonProperty("detection_id")
     private String detectionId;

     /**
      * Parent ID for the object (typically refers to the context like "image").
      */
     @JsonProperty("parent_id")
     private String parentId;

     /**
      * Creates a combined prediction from two individual predictions.
      * <p>
      * The label is concatenated, and the confidence is multiplied. This is useful for combining two related predictions.
      * </p>
      *
      * @param action The first prediction to combine.
      * @param object The second prediction to combine.
      */
     public SinglePrediction(SinglePrediction action, SinglePrediction object) {
          this.label = action.label + " and " + object.label;
          this.confidence = action.confidence * object.confidence;
          this.clazz = action.clazz + ' ' + object.clazz;
     }

     /**
      * Retrieves the action and object combined as a string.
      *
      * @return The action and object as a concatenated string.
      */
     @Override
     public String getActionAndObject() {
          return clazz;
     }

     /**
      * Retrieves the confidence probability for the prediction.
      *
      * @return The confidence score of the prediction.
      */
     @Override
     public double getProbability() {
          return confidence;
     }

     /**
      * Sets the confidence probability for the prediction.
      *
      * @param probability The new confidence score.
      */
     @Override
     public void setProbability(double probability) {
          this.confidence = probability;
     }

     /**
      * Returns a string representation of the action and object for debugging.
      *
      * @return The action and object as a string.
      */
     @Override
     public String s() {
          return this.getActionAndObject();
     }
}
