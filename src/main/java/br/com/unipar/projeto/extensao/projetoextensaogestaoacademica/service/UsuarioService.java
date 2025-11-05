package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarUsuarioRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.UsuarioResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO criarUsuario(CriarUsuarioRequestDTO request) {
        logger.info("Criando usuario: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            logger.warn("Tentativa de criar usuario com email ja existente: {}", request.getEmail());
            throw new RuntimeException("Ja existe um usuario com este email: " + request.getEmail());
        }

        Usuario usuario = new Usuario(
                request.getNome(),
                request.getEmail(),
                request.getSenha(),
                request.getPerfil()
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("Usuario criado com ID: {}", usuarioSalvo.getId());

        // Irá mandar as infos para o último método
        // Convertendo as infos para dto e salvando de fato
        return converterParaDTO(usuarioSalvo);
    }

    public List<UsuarioResponseDTO> listarTodosUsuarios() {
        logger.info("Listando todos os usuarios");

        List<Usuario> usuarios = usuarioRepository.findAll();
        logger.debug("Encontrados {} usuarios", usuarios.size());

        return usuarios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        logger.debug("Buscando usuario por ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario nao encontrado com ID: {}", id);
                    return new RuntimeException("Usuario nao encontrado com ID: " + id);
                });

        // Utiliza o Método de converter para mostrar apenas as informações
        // seguras do usuário (que escolhemos quais mostrar lá no conversor)
        return converterParaDTO(usuario);
    }

    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        logger.debug("Buscando usuario por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario nao encontrado com email: {}", email);
                    return new RuntimeException("Usuario nao encontrado com email: " + email);
                });

        // Utiliza o Método de converter para mostrar apenas as informações
        // seguras do usuário (que escolhemos quais mostrar lá no conversor)
        return converterParaDTO(usuario);
    }

    public List<UsuarioResponseDTO> listarUsuariosPorPerfil(PerfilUsuario perfil) {
        logger.debug("Listando usuarios por perfil: {}", perfil);

        List<Usuario> usuarios = usuarioRepository.findByPerfil(perfil);
        logger.debug("Encontrados {} usuarios com perfil {}", usuarios.size(), perfil);

        return usuarios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public void inativarUsuario(Long id) {
        logger.info("Inativando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de inativar. Usuario nao encontrado com ID: {}", id);
                    return new RuntimeException("Usuario nao encontrado com ID: " + id);
                });

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        logger.info("Usuario inativado: {}", usuario.getEmail());
    }

    public void ativarUsuario(Long id) {
        logger.info("Ativando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de ativar usuario nao encontrado com ID: {}", id);
                    return new RuntimeException("Usuario nao encontrado com ID: " + id);
                });

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        logger.info("Usuario ativado: {}", usuario.getEmail());
    }

    // Sempre converter a entity para dto antes do retorno
    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setPerfil(usuario.getPerfil());
        dto.setAtivo(usuario.getAtivo());
        dto.setDataCriacao(usuario.getDataCriacao());
        return dto;
    }
}