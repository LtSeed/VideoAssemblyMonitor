package nusri.fyp.demo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents alert information that appears in a state machine or process,
 * such as timeouts or execution order errors.
 */
@Getter
@Setter
public class Alarm {
    /**
     * The title or prompt of the alert.
     */
    private String message;

    /**
     * The severity or completion percentage of the alert (e.g., error percentage).
     */
    private int percentage;

    /**
     * A more detailed description of the alert.
     */
    private String description;

    /**
     * The type of the alert (e.g., "error", "warning", etc.).
     */
    private String type;

    /**
     * Constructor to initialize an Alarm instance with all parameters.
     *
     * @param message     The alert title message<br>
     *                    The alert title or prompt information.
     * @param description A detailed description of the alert.<br>
     *                    A more detailed explanation of the alert.
     * @param percentage  The associated percentage value.<br>
     *                    The percentage value (e.g., error percentage).
     * @param type        The type of the alert.<br>
     *                    The type of the alert (e.g., "error", "warning").
     */
    public Alarm(String message, String description, int percentage, String type) {
        this.message = message;
        this.description = description;
        this.percentage = percentage;
        this.type = type;
    }
}
