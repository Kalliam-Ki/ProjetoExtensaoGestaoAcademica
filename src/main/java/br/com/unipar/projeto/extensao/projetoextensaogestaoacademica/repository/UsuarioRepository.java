package br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.repository;

import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.entity.Usuario;
import br.com.unipar.projeto.extensao.projetoextensaogestaoacademica.model.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuário por email
    Optional<Usuario> findByEmail(String email);

    // Verificar se email já existe
    Boolean existsByEmail(String email);

    // Buscar usuários por perfil
    List<Usuario> findByPerfil(PerfilUsuario perfil);

    // Buscar usuários ativos
    List<Usuario> findByAtivoTrue();

    // Buscar usuários inativos
    List<Usuario> findByAtivoFalse();
}