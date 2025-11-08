package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.LogAlteracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAlteracaoRepository extends JpaRepository<LogAlteracao, Long> {

    List<LogAlteracao> findByTabelaAfetada(String tabelaAfetada);

    List<LogAlteracao> findByTabelaAfetadaAndIdRegistroAfetado(String tabelaAfetada, Long idRegistroAfetado);

    List<LogAlteracao> findByUsuarioId(Long usuarioId);

    List<LogAlteracao> findByAcao(String acao);

    List<LogAlteracao> findByDataAlteracaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @Query("SELECT l FROM LogAlteracao l WHERE l.tabelaAfetada = :tabela AND l.dataAlteracao BETWEEN :dataInicio AND :dataFim")
    List<LogAlteracao> findByTabelaAndPeriodo(
            @Param("tabela") String tabela,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT l FROM LogAlteracao l WHERE l.usuarioId = :usuarioId AND l.dataAlteracao BETWEEN :dataInicio AND :dataFim")
    List<LogAlteracao> findByUsuarioAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(l) FROM LogAlteracao l WHERE l.tabelaAfetada = :tabela")
    Long countByTabelaAfetada(@Param("tabela") String tabela);
}