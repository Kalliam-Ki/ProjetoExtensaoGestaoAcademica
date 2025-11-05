package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarProjetoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.ProjetoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.ProjetoResumoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.StatusProjeto;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
// Tag é para o Swegger agrupar agrupar os endpoints em um lugar (nesse caso usuarios)
// Operation é para especificar cada endpoint
@Tag(name = "Projetos", description = "Endpoints para gerenciamento de projetos academicos")
public class ProjetoController {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoController.class);
    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    // @Valid irá fazer com que o spring valide auto o dto antes de mandar
    // para o service, então ele quebra aqui e não lá
    @PostMapping
    @Operation(summary = "Criar projeto", description = "Cria um novo projeto academico")
    public ResponseEntity<ProjetoResponseDTO> criarProjeto(
            @Valid @RequestBody CriarProjetoRequestDTO request,
            @RequestHeader("usuario-criador-id") Long usuarioCriadorId) {
        try {
            logger.info("Recebida requisicao para criar projeto: {}", request.getTitulo());
            ProjetoResponseDTO response = projetoService.criarProjeto(request, usuarioCriadorId);
            logger.info("Projeto criado com sucesso: {}", response.getTitulo());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Erro ao criar projeto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Lista todos os projetos cadastrados")
    public ResponseEntity<List<ProjetoResumoResponseDTO>> listarProjetos() {
        logger.debug("Recebida requisicao para listar projetos");
        List<ProjetoResumoResponseDTO> projetos = projetoService.listarTodosProjetos();
        return ResponseEntity.ok(projetos);
    }

    @PostMapping("/{id}/submeter")
    @Operation(summary = "Submeter projeto", description = "Submete um projeto para analise")
    public ResponseEntity<ProjetoResponseDTO> submeterProjeto(@PathVariable Long id) {
        try {
            logger.info("Recebida requisicao para submeter projeto ID: {}", id);
            ProjetoResponseDTO projeto = projetoService.submeterProjeto(id);
            return ResponseEntity.ok(projeto);
        } catch (RuntimeException e) {
            logger.warn("Erro ao submeter projeto ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID", description = "Busca um projeto especifico pelo ID")
    public ResponseEntity<ProjetoResponseDTO> buscarProjetoPorId(@PathVariable Long id) {
        try {
            logger.debug("Recebida requisicao para buscar projeto por ID: {}", id);
            ProjetoResponseDTO projeto = projetoService.buscarProjetoPorId(id);
            return ResponseEntity.ok(projeto);
        } catch (RuntimeException e) {
            logger.warn("Projeto nao encontrado com ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar projetos por status", description = "Lista projetos filtrados por status")
    public ResponseEntity<List<ProjetoResumoResponseDTO>> listarProjetosPorStatus(@PathVariable StatusProjeto status) {
        logger.debug("Recebida requisicao para listar projetos por status: {}", status);
        List<ProjetoResumoResponseDTO> projetos = projetoService.listarProjetosPorStatus(status);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/area/{area}")
    @Operation(summary = "Listar projetos por area", description = "Lista projetos filtrados por area")
    public ResponseEntity<List<ProjetoResumoResponseDTO>> listarProjetosPorArea(@PathVariable String area) {
        logger.debug("Recebida requisicao para listar projetos por area: {}", area);
        List<ProjetoResumoResponseDTO> projetos = projetoService.listarProjetosPorArea(area);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/usuario/{usuarioCriadorId}")
    @Operation(summary = "Listar projetos por usuario criador", description = "Lista projetos de um usuario especifico")
    public ResponseEntity<List<ProjetoResumoResponseDTO>> listarProjetosPorUsuarioCriador(@PathVariable Long usuarioCriadorId) {
        logger.debug("Recebida requisicao para listar projetos do usuario: {}", usuarioCriadorId);
        List<ProjetoResumoResponseDTO> projetos = projetoService.listarProjetosPorUsuarioCriador(usuarioCriadorId);
        return ResponseEntity.ok(projetos);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do projeto", description = "Atualiza o status de um projeto")
    public ResponseEntity<ProjetoResponseDTO> atualizarStatusProjeto(
            @PathVariable Long id,
            @RequestParam StatusProjeto status) {
        try {
            logger.info("Recebida requisicao para atualizar status do projeto ID: {} para {}", id, status);
            ProjetoResponseDTO projeto = projetoService.atualizarStatusProjeto(id, status);
            return ResponseEntity.ok(projeto);
        } catch (RuntimeException e) {
            logger.warn("Erro ao atualizar status do projeto ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto", description = "Exclui um projeto pelo ID")
    public ResponseEntity<Void> excluirProjeto(@PathVariable Long id) {
        try {
            logger.info("Recebida requisicao para excluir projeto ID: {}", id);
            projetoService.excluirProjeto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Erro ao excluir projeto ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}