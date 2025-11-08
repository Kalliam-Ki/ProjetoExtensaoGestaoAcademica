package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.LogAlteracaoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auditoria")
@Tag(name = "Auditoria", description = "Endpoints para consulta de logs de auditoria (apenas ADMIN)")
public class AuditoriaController {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaController.class);
    private final AuditoriaService auditoriaService;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaController(AuditoriaService auditoriaService, UsuarioRepository usuarioRepository) {
        this.auditoriaService = auditoriaService;
        this.usuarioRepository = usuarioRepository;
    }

    // Método de validação de perfil ADMIN
    private void validarPermissaoAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new RuntimeException("Apenas administradores podem acessar logs de auditoria");
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os logs", description = "Lista todos os logs de auditoria (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarTodosLogs(
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.info("Recebida requisicao para listar todos os logs de auditoria pelo usuario: {}", usuarioId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarTodosLogs();
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/tabela/{tabela}")
    @Operation(summary = "Listar logs por tabela", description = "Lista logs de uma tabela especifica (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorTabela(
            @PathVariable String tabela,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.debug("Recebida requisicao para listar logs da tabela: {} pelo usuario: {}", tabela, usuarioId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorTabela(tabela);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs da tabela {}: {}", tabela, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/registro/{tabela}/{idRegistro}")
    @Operation(summary = "Listar logs por registro", description = "Lista logs de um registro especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorRegistro(
            @PathVariable String tabela,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.debug("Recebida requisicao para listar logs da tabela: {} pelo usuario: {}", tabela, usuarioId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorTabela(tabela);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs da tabela {}: {}", tabela, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar logs por usuario", description = "Lista logs de um usuario especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorUsuario(
            @PathVariable Long usuarioId,
            @RequestHeader("usuario-id") Long usuarioRequisitanteId) {
        try {
            validarPermissaoAdmin(usuarioRequisitanteId);
            logger.debug("Recebida requisicao para listar logs do usuario: {} pelo usuario: {}",
                    usuarioId, usuarioRequisitanteId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorUsuario(usuarioId);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs do usuario {}: {}", usuarioId, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar logs por periodo", description = "Lista logs de um periodo especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.debug("Recebida requisicao para listar logs do periodo: {} ate {} pelo usuario: {}",
                    dataInicio, dataFim, usuarioId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs por periodo: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/acao/{acao}")
    @Operation(summary = "Listar logs por acao", description = "Lista logs de uma acao especifica (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorAcao(
            @PathVariable String acao,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.debug("Recebida requisicao para listar logs da acao: {} pelo usuario: {}", acao, usuarioId);
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorAcao(acao);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs da acao {}: {}", acao, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/tabela-periodo/{tabela}")
    @Operation(summary = "Listar logs por tabela e periodo", description = "Lista logs de uma tabela em periodo especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorTabelaEPeriodo(
            @PathVariable String tabela,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            validarPermissaoAdmin(usuarioId);
            logger.debug("Recebida requisicao para listar logs da tabela {} no periodo: {} ate {} pelo usuario: {}",
                    tabela, dataInicio, dataFim, usuarioId);

            // Este método precisa ser implementado no AuditoriaService
            List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorTabelaEPeriodo(tabela, dataInicio, dataFim);
            return ResponseEntity.ok(logs);
        } catch (RuntimeException e) {
            logger.warn("Acesso negado ou erro ao listar logs da tabela {} no periodo: {}", tabela, e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
}