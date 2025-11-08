package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByMatricula(String matricula);

    Optional<Aluno> findByEmail(String email);

    Boolean existsByMatricula(String matricula);

    Boolean existsByEmail(String email);

    List<Aluno> findByCurso(String curso);

    List<Aluno> findByPeriodo(Integer periodo);

    List<Aluno> findByUsuarioCriadorId(Long usuarioCriadorId);

    List<Aluno> findByAtivoTrue();

    List<Aluno> findByAtivoFalse();

    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT a FROM Aluno a WHERE a.curso = :curso AND a.periodo = :periodo")
    List<Aluno> findByCursoAndPeriodo(@Param("curso") String curso, @Param("periodo") Integer periodo);

    Long countByCurso(String curso);

    Long countByAtivoTrue();
}