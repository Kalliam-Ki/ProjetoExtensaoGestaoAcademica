package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.AtualizarAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.AlunoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.AlunoService;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.utils.SecurityUtils;
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
@RequestMapping("/alunos")
@Tag(name = "Alunos", description = "Endpoints para gerenciamento de alunos")
public class AlunoController {

    private static final Logger logger = LoggerFactory.getLogger(AlunoController.class);
    private final AlunoService alunoService;
    private final SecurityUtils securityUtils;

    public AlunoController(AlunoService alunoService, SecurityUtils securityUtils) {
        this.alunoService = alunoService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @Operation(summary = "Criar aluno", description = "Cria um novo aluno")
    public ResponseEntity<AlunoResponseDTO> criarAluno(
            @Valid @RequestBody CriarAlunoRequestDTO request) {
        Long usuarioId = securityUtils.getUsuarioLogadoId();
        logger.info("Recebida requisicao para criar aluno: {} pelo usuario: {}", request.getNome(), usuarioId);
        AlunoResponseDTO response = alunoService.criarAluno(request, usuarioId);
        logger.info("Aluno criado com sucesso: {}", response.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar alunos", description = "Lista todos os alunos cadastrados")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunos() {
        logger.debug("Recebida requisicao para listar alunos");
        List<AlunoResponseDTO> alunos = alunoService.listarTodosAlunos();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID", description = "Busca um aluno especifico pelo ID")
    public ResponseEntity<AlunoResponseDTO> buscarAlunoPorId(@PathVariable Long id) {
        logger.debug("Recebida requisicao para buscar aluno por ID: {}", id);
        AlunoResponseDTO aluno = alunoService.buscarAlunoPorId(id);
        return ResponseEntity.ok(aluno);
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar aluno por matricula", description = "Busca um aluno pela matricula")
    public ResponseEntity<AlunoResponseDTO> buscarAlunoPorMatricula(@PathVariable String matricula) {
        logger.debug("Recebida requisicao para buscar aluno por matricula: {}", matricula);
        AlunoResponseDTO aluno = alunoService.buscarAlunoPorMatricula(matricula);
        return ResponseEntity.ok(aluno);
    }

    @GetMapping("/curso/{curso}")
    @Operation(summary = "Listar alunos por curso", description = "Lista alunos filtrados por curso")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunosPorCurso(@PathVariable String curso) {
        logger.debug("Recebida requisicao para listar alunos por curso: {}", curso);
        List<AlunoResponseDTO> alunos = alunoService.listarAlunosPorCurso(curso);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/periodo/{periodo}")
    @Operation(summary = "Listar alunos por periodo", description = "Lista alunos filtrados por periodo")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunosPorPeriodo(@PathVariable Integer periodo) {
        logger.debug("Recebida requisicao para listar alunos por periodo: {}", periodo);
        List<AlunoResponseDTO> alunos = alunoService.listarAlunosPorPeriodo(periodo);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar alunos ativos", description = "Lista apenas alunos ativos")
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunosAtivos() {
        logger.debug("Recebida requisicao para listar alunos ativos");
        List<AlunoResponseDTO> alunos = alunoService.listarAlunosAtivos();
        return ResponseEntity.ok(alunos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar aluno", description = "Atualiza um aluno existente")
    public ResponseEntity<AlunoResponseDTO> atualizarAluno(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarAlunoRequestDTO request) {
        Long usuarioId = securityUtils.getUsuarioLogadoId();
        logger.info("Recebida requisicao para atualizar aluno ID: {} pelo usuario: {}", id, usuarioId);
        AlunoResponseDTO aluno = alunoService.atualizarAluno(id, request, usuarioId);
        return ResponseEntity.ok(aluno);
    }

    @PatchMapping("/{id}/inativar")
    @Operation(summary = "Inativar aluno", description = "Inativa um aluno pelo ID")
    public ResponseEntity<Void> inativarAluno(@PathVariable Long id) {
        Long usuarioId = securityUtils.getUsuarioLogadoId();
        logger.info("Recebida requisicao para inativar aluno ID: {} pelo usuario: {}", id, usuarioId);
        alunoService.inativarAluno(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar aluno", description = "Ativa um aluno pelo ID")
    public ResponseEntity<Void> ativarAluno(@PathVariable Long id) {
        Long usuarioId = securityUtils.getUsuarioLogadoId();
        logger.info("Recebida requisicao para ativar aluno ID: {} pelo usuario: {}", id, usuarioId);
        alunoService.ativarAluno(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}