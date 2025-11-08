package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.AlunoProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoProjetoRepository extends JpaRepository<AlunoProjeto, Long> {

    Optional<AlunoProjeto> findByAlunoIdAndProjetoId(Long alunoId, Long projetoId);

    List<AlunoProjeto> findByAlunoId(Long alunoId);

    List<AlunoProjeto> findByProjetoId(Long projetoId);

    List<AlunoProjeto> findByAlunoIdAndAtivoTrue(Long alunoId);

    List<AlunoProjeto> findByProjetoIdAndAtivoTrue(Long projetoId);

    Boolean existsByAlunoIdAndProjetoIdAndAtivoTrue(Long alunoId, Long projetoId);

    Long countByProjetoIdAndAtivoTrue(Long projetoId);

    Long countByAlunoIdAndAtivoTrue(Long alunoId);

    @Query("SELECT ap FROM AlunoProjeto ap WHERE ap.aluno.id = :alunoId AND ap.ativo = true")
    List<AlunoProjeto> findProjetosAtivosPorAluno(@Param("alunoId") Long alunoId);

    @Query("SELECT ap FROM AlunoProjeto ap WHERE ap.projeto.id = :projetoId AND ap.ativo = true")
    List<AlunoProjeto> findAlunosAtivosPorProjeto(@Param("projetoId") Long projetoId);

    @Query("SELECT COUNT(ap) FROM AlunoProjeto ap WHERE ap.aluno.id = :alunoId AND ap.ativo = true")
    Long countProjetosAtivosPorAluno(@Param("alunoId") Long alunoId);
}