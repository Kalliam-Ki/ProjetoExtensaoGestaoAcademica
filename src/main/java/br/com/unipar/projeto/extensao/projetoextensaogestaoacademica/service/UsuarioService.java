package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.service;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.request.CriarUsuarioRequestDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.dto.response.UsuarioResponseDTO;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NegocioException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.exception.NotFoundException;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    // Logger para logs melhores e facilitar no debug
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final HttpServletRequest request;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          AuditoriaService auditoriaService,
                          HttpServletRequest request,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.request = request;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para criar o primeiro administrador (sem validação de permissão e sem auditoria)
    public UsuarioResponseDTO criarPrimeiroAdmin(CriarUsuarioRequestDTO requestDTO) {
        logger.info("Criando primeiro administrador: {}", requestDTO.getEmail());

        if (usuarioRepository.count() > 0) {
            throw new NegocioException("Já existem usuários cadastrados. Este endpoint é apenas para inicialização.");
        }

        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new NegocioException("Ja existe um usuario com este email: " + requestDTO.getEmail());
        }

        String senhaCriptografada = passwordEncoder.encode(requestDTO.getSenha());

        Usuario usuario = new Usuario(
                requestDTO.getNome(),
                requestDTO.getEmail(),
                senhaCriptografada,
                PerfilUsuario.ADMINISTRADOR  // Força perfil ADMIN
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("Administrador criado com ID: {}", usuarioSalvo.getId());

        // Não registra auditoria pois é o primeiro usuário (não há quem o tenha criado)
        return converterParaDTO(usuarioSalvo);
    }

    public UsuarioResponseDTO criarUsuario(CriarUsuarioRequestDTO requestDTO, Long usuarioCriadorId) {
        logger.info("Criando usuario: {}", requestDTO.getEmail());

        // Permissão: apenas ADMIN pode criar outros usuários
        Usuario criador = usuarioRepository.findById(usuarioCriadorId)
                .orElseThrow(() -> new NotFoundException("Usuario criador nao encontrado"));
        if (criador.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new NegocioException("Apenas administradores podem criar novos usuarios");
        }

        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            logger.warn("Tentativa de criar usuario com email ja existente: {}", requestDTO.getEmail());
            throw new NegocioException("Ja existe um usuario com este email: " + requestDTO.getEmail());
        }

        String senhaCriptografada = passwordEncoder.encode(requestDTO.getSenha());

        Usuario usuario = new Usuario(
                requestDTO.getNome(),
                requestDTO.getEmail(),
                senhaCriptografada,
                requestDTO.getPerfil()
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        logger.info("Usuario criado com ID: {}", usuarioSalvo.getId());

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "USUARIO",
                usuarioSalvo.getId(),
                "CREATE",
                null, // Valores antigos (não existem para CREATE)
                usuarioSalvo, // Valores novos
                usuarioCriadorId,
                request
        );

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
                    return new NotFoundException("Usuario nao encontrado com ID: " + id);
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
                    return new NotFoundException("Usuario nao encontrado com email: " + email);
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

    public void inativarUsuario(Long id, Long usuarioId) {
        logger.info("Inativando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de inativar. Usuario nao encontrado com ID: {}", id);
                    return new NotFoundException("Usuario nao encontrado com ID: " + id);
                });

        // Salva estado antigo para auditoria
        Usuario usuarioAntigo = new Usuario();
        usuarioAntigo.setAtivo(usuario.getAtivo());

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "USUARIO",
                usuario.getId(),
                "UPDATE",
                usuarioAntigo, // Valores antigos (apenas o campo alterado)
                usuario, // Valores novos
                usuarioId,
                request
        );

        logger.info("Usuario inativado: {}", usuario.getEmail());
    }

    public void ativarUsuario(Long id, Long usuarioId) {
        logger.info("Ativando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Tentativa de ativar usuario nao encontrado com ID: {}", id);
                    return new NotFoundException("Usuario nao encontrado com ID: " + id);
                });

        // Salva estado antigo para auditoria
        Usuario usuarioAntigo = new Usuario();
        usuarioAntigo.setAtivo(usuario.getAtivo());

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        // Registra na auditoria
        auditoriaService.registrarAlteracao(
                "USUARIO",
                usuario.getId(),
                "UPDATE",
                usuarioAntigo,
                usuario,
                usuarioId,
                request
        );

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