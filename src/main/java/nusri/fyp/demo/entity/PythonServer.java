package nusri.fyp.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "python_server")
public class PythonServer {

    @Id
    private long id;

    private String host;

    private String port;

    @Override
    public String toString() {
        return host + "@" + port;
    }
}