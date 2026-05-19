package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.LogAlteracaoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.AuditoriaService;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.utils.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    public AuditoriaController(AuditoriaService auditoriaService, SecurityUtils securityUtils) {
        this.auditoriaService = auditoriaService;
        this.securityUtils = securityUtils;
    }

    // Método de validação de perfil ADMIN usando o usuário logado
    private void validarPermissaoAdmin() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        if (usuario.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new RuntimeException("Apenas administradores podem acessar logs de auditoria");
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os logs", description = "Lista todos os logs de auditoria (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarTodosLogs() {
        validarPermissaoAdmin();
        logger.info("Recebida requisicao para listar todos os logs de auditoria");
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarTodosLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/tabela/{tabela}")
    @Operation(summary = "Listar logs por tabela", description = "Lista logs de uma tabela especifica (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorTabela(@PathVariable String tabela) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs da tabela: {}", tabela);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorTabela(tabela);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/registro/{tabela}/{idRegistro}")
    @Operation(summary = "Listar logs por registro", description = "Lista logs de um registro especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorRegistro(
            @PathVariable String tabela,
            @PathVariable Long idRegistro) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs da tabela: {} para registro {}", tabela, idRegistro);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorRegistro(tabela, idRegistro);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar logs por usuario", description = "Lista logs de um usuario especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorUsuario(@PathVariable Long usuarioId) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs do usuario: {}", usuarioId);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorUsuario(usuarioId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar logs por periodo", description = "Lista logs de um periodo especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs do periodo: {} ate {}", dataInicio, dataFim);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/acao/{acao}")
    @Operation(summary = "Listar logs por acao", description = "Lista logs de uma acao especifica (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorAcao(@PathVariable String acao) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs da acao: {}", acao);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorAcao(acao);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/tabela-periodo/{tabela}")
    @Operation(summary = "Listar logs por tabela e periodo", description = "Lista logs de uma tabela em periodo especifico (apenas ADMIN)")
    public ResponseEntity<List<LogAlteracaoResponseDTO>> listarLogsPorTabelaEPeriodo(
            @PathVariable String tabela,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        validarPermissaoAdmin();
        logger.debug("Recebida requisicao para listar logs da tabela {} no periodo: {} ate {}", tabela, dataInicio, dataFim);
        List<LogAlteracaoResponseDTO> logs = auditoriaService.listarLogsPorTabelaEPeriodo(tabela, dataInicio, dataFim);
        return ResponseEntity.ok(logs);
    }
}