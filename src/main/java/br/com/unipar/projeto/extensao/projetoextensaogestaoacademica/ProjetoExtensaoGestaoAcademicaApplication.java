package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetoExtensaoGestaoAcademicaApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoExtensaoGestaoAcademicaApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProjetoExtensaoGestaoAcademicaApplication.class, args);
        logger.info("Aplicação iniciada com sucesso!");
        logger.info("Swagger UI: http://localhost:8080/api/swagger-ui.html");
        logger.info("Banco: PostgreSQL - gestaoacademica");
    }
}