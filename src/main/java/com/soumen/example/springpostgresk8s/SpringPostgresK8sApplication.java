package com.soumen.example.springpostgresk8s;

import io.r2dbc.spi.ConnectionFactory;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class SpringPostgresK8sApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPostgresK8sApplication.class, args);
    }

    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource =
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        initializer.setDatabasePopulator(resource);
        return initializer;
    }

    @Bean
    ApplicationRunner runner(EmployeeRepository repository) {
        return args -> {
            log.info("initializing Database");
            repository.saveAll(Arrays.asList(
                    new Employee(null, "Soumen"),
                    new Employee(null, "Vincent"),
                    new Employee(null, "Adam"),
                    new Employee(null, "David")
            )).blockLast(Duration.ofSeconds(5));
            repository.findAll().doOnNext(employee -> log.info("Emp - {}", employee))
                    .blockLast(Duration.ofSeconds(5));
        };
    }
}

@RestController
@RequestMapping(value = "/api/employee")
@RequiredArgsConstructor
class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping
    public Flux<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{name}")
    public Mono<Employee> getOneByName(@PathVariable String name) {
        return employeeRepository.findByName(name);
    }
}

@Repository
interface EmployeeRepository extends R2dbcRepository<Employee, Long> {
    Mono<Employee> findByName(String name);
}


@Value
@Builder
class Employee {
    @Id
    Long id;
    String name;
}


