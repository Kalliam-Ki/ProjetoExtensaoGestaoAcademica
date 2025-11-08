package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.VincularAlunoRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.AlunoProjetoResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.AlunoProjetoService;
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
@RequestMapping("/aluno-projeto")
@Tag(name = "Aluno-Projeto", description = "Endpoints para gerenciamento de vinculos entre alunos e projetos")
public class AlunoProjetoController {

    private static final Logger logger = LoggerFactory.getLogger(AlunoProjetoController.class);
    private final AlunoProjetoService alunoProjetoService;

    public AlunoProjetoController(AlunoProjetoService alunoProjetoService) {
        this.alunoProjetoService = alunoProjetoService;
    }

    @PostMapping("/vincular")
    @Operation(summary = "Vincular aluno a projeto", description = "Vincula um aluno a um projeto")
    public ResponseEntity<AlunoProjetoResponseDTO> vincularAlunoProjeto(
            @Valid @RequestBody VincularAlunoRequestDTO request,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            logger.info("Recebida requisicao para vincular aluno {} ao projeto {} pelo usuario: {}",
                    request.getAlunoId(), request.getProjetoId(), usuarioId);
            AlunoProjetoResponseDTO response = alunoProjetoService.vincularAlunoProjeto(request, usuarioId);
            logger.info("Aluno vinculado com sucesso ao projeto");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Erro ao vincular aluno ao projeto: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/aluno/{alunoId}")
    @Operation(summary = "Listar vinculos por aluno", description = "Lista todos os vinculos de um aluno")
    public ResponseEntity<List<AlunoProjetoResponseDTO>> listarVinculosPorAluno(@PathVariable Long alunoId) {
        try {
            logger.debug("Recebida requisicao para listar vinculos do aluno: {}", alunoId);
            List<AlunoProjetoResponseDTO> vinculos = alunoProjetoService.listarVinculosPorAluno(alunoId);
            return ResponseEntity.ok(vinculos);
        } catch (RuntimeException e) {
            logger.warn("Erro ao listar vinculos do aluno {}: {}", alunoId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projeto/{projetoId}")
    @Operation(summary = "Listar vinculos por projeto", description = "Lista todos os vinculos de um projeto")
    public ResponseEntity<List<AlunoProjetoResponseDTO>> listarVinculosPorProjeto(@PathVariable Long projetoId) {
        try {
            logger.debug("Recebida requisicao para listar vinculos do projeto: {}", projetoId);
            List<AlunoProjetoResponseDTO> vinculos = alunoProjetoService.listarVinculosPorProjeto(projetoId);
            return ResponseEntity.ok(vinculos);
        } catch (RuntimeException e) {
            logger.warn("Erro ao listar vinculos do projeto {}: {}", projetoId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/aluno/{alunoId}/ativos")
    @Operation(summary = "Listar vinculos ativos por aluno", description = "Lista apenas vinculos ativos de um aluno")
    public ResponseEntity<List<AlunoProjetoResponseDTO>> listarVinculosAtivosPorAluno(@PathVariable Long alunoId) {
        try {
            logger.debug("Recebida requisicao para listar vinculos ativos do aluno: {}", alunoId);
            List<AlunoProjetoResponseDTO> vinculos = alunoProjetoService.listarVinculosAtivosPorAluno(alunoId);
            return ResponseEntity.ok(vinculos);
        } catch (RuntimeException e) {
            logger.warn("Erro ao listar vinculos ativos do aluno {}: {}", alunoId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projeto/{projetoId}/ativos")
    @Operation(summary = "Listar vinculos ativos por projeto", description = "Lista apenas vinculos ativos de um projeto")
    public ResponseEntity<List<AlunoProjetoResponseDTO>> listarVinculosAtivosPorProjeto(@PathVariable Long projetoId) {
        try {
            logger.debug("Recebida requisicao para listar vinculos ativos do projeto: {}", projetoId);
            List<AlunoProjetoResponseDTO> vinculos = alunoProjetoService.listarVinculosAtivosPorProjeto(projetoId);
            return ResponseEntity.ok(vinculos);
        } catch (RuntimeException e) {
            logger.warn("Erro ao listar vinculos ativos do projeto {}: {}", projetoId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{alunoProjetoId}/desvincular")
    @Operation(summary = "Desvincular aluno do projeto", description = "Desvincula um aluno de um projeto")
    public ResponseEntity<Void> desvincularAlunoProjeto(
            @PathVariable Long alunoProjetoId,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            logger.info("Recebida requisicao para desvincular aluno do projeto. ID do vinculo: {} pelo usuario: {}",
                    alunoProjetoId, usuarioId);
            alunoProjetoService.desvincularAlunoProjeto(alunoProjetoId, usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Erro ao desvincular aluno do projeto ID {}: {}", alunoProjetoId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{alunoProjetoId}/reativar")
    @Operation(summary = "Reativar vinculo", description = "Reativa um vinculo entre aluno e projeto")
    public ResponseEntity<Void> reativarVinculo(
            @PathVariable Long alunoProjetoId,
            @RequestHeader("usuario-id") Long usuarioId) {
        try {
            logger.info("Recebida requisicao para reativar vinculo ID: {} pelo usuario: {}", alunoProjetoId, usuarioId);
            alunoProjetoService.reativarVinculo(alunoProjetoId, usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Erro ao reativar vinculo ID {}: {}", alunoProjetoId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}