package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.UsuarioRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.UsuarioResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.UsuarioService;
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
@RequestMapping("/usuarios")
// Tag é para o Swegger agrupar agrupar os endpoints em um lugar (nesse caso usuarios)
// Operation é para especificar cada endpoint
@Tag(name = "Usuarios", description = "Endpoints para gerenciamento de usuarios")

public class UsuarioController {

    // Logger usado para debug e logs de erros que acontecerem
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // @Valid irá fazer com que o spring valide auto o dto antes de mandar
    // para o service, então ele quebra aqui e não lá
    @PostMapping
    @Operation(summary = "Criar usuario", description = "Cria um novo usuario no sistema")
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO request) {
        try {
            logger.info("Recebida requisicao para criar usuario: {}", request.getEmail());
            UsuarioResponseDTO response = usuarioService.criarUsuario(request);
            logger.info("Usuario criado com sucesso: {}", response.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            logger.error("Erro ao criar usuario: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista todos os usuarios cadastrados")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        logger.debug("Recebida requisicao para listar usuarios");
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Busca um usuario especifico pelo ID")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorId(@PathVariable Long id) {
        try {
            logger.debug("Recebida requisicao para buscar usuario por ID: {}", id);
            UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            logger.warn("Usuario nao encontrado com ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email", description = "Busca um usuario pelo email")
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorEmail(@PathVariable String email) {
        try {
            logger.debug("Recebida requisicao para buscar usuario por email: {}", email);
            UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorEmail(email);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            logger.warn("Usuario nao encontrado com email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/perfil/{perfil}")
    @Operation(summary = "Listar usuarios por perfil", description = "Lista usuarios filtrados por perfil")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuariosPorPerfil(@PathVariable PerfilUsuario perfil) {
        logger.debug("Recebida requisicao para listar usuarios por perfil: {}", perfil);
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuariosPorPerfil(perfil);
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}/inativar")
    @Operation(summary = "Inativar usuario", description = "Inativa um usuario pelo ID")
    public ResponseEntity<Void> inativarUsuario(@PathVariable Long id) {
        try {
            logger.info("Recebida requisicao para inativar usuario ID: {}", id);
            usuarioService.inativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Erro ao inativar usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/ativar")
    @Operation(summary = "Ativar usuario", description = "Ativa um usuario pelo ID")
    public ResponseEntity<Void> ativarUsuario(@PathVariable Long id) {
        try {
            logger.info("Recebida requisicao para ativar usuario ID: {}", id);
            usuarioService.ativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Erro ao ativar usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}