package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.controller;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarUsuarioRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.UsuarioResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
public class SetupController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public SetupController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/admin")
    public ResponseEntity<UsuarioResponseDTO> criarAdminInicial(@RequestBody CriarUsuarioRequestDTO request) {
        // Só permite criar se não houver nenhum usuário no banco
        if (usuarioRepository.count() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Força perfil ADMIN
        request.setPerfil(PerfilUsuario.ADMINISTRADOR);
        // O usuarioId criador pode ser null ou 0, pois não existe usuário ainda
        // Vamos ajustar o service para aceitar um criadorId = null e não registrar auditoria
        UsuarioResponseDTO response = usuarioService.criarPrimeiroAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}