package nusri.fyp.demo.repository;

import nusri.fyp.demo.entity.PythonServer;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.function.Function;

public interface PythonServerRepository extends JpaRepository<PythonServer, Long> {

    PythonServer findByHostAndPort(String host, String port);
}