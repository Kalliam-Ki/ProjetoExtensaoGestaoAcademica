package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Projeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    // Busca projeto por status
    List<Projeto> findByStatus(StatusProjeto status);

    // Busca projeto por area
    List<Projeto> findByArea(String area);

    // Busca projeto por qual usuario criou ele
    List<Projeto> findByUsuarioCriadorId(Long usuarioCriadorId);

    // Busca projeto pelo título (ignorando mai e min)
    List<Projeto> findByTituloContainingIgnoreCase(String titulo);

    // Busca projeto por orientador responsável
    List<Projeto> findByOrientadorContainingIgnoreCase(String orientador);

    // NOVO: Método eficiente para verificar existência por título (case insensitive)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Projeto p WHERE LOWER(p.titulo) = LOWER(:titulo)")
    boolean existsByTituloIgnoreCase(@Param("titulo") String titulo);

    // Busca projetos que estão atrazados
    // Compara a data prevista para terminar com a data atual e verifica se
    // o status está finalizado
    @Query("SELECT p FROM Projeto p WHERE p.dataPrevistaTermino < CURRENT_DATE AND p.status NOT IN (:statusFinalizados)")
    List<Projeto> findProjetosAtrasados(@Param("statusFinalizados") List<StatusProjeto> statusFinalizados);

    // Conta projeto por status (1, 2, 3, etc)
    Long countByStatus(StatusProjeto status);
}