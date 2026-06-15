import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import logo from '../../assets/LogoUnipar.png'; 
import './Navbar.css';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="brand-link">
            <img src={logo} alt="Logo" className="navbar-logo" />
            <span className="brand-text">Gestão Acadêmica</span>
          </Link>
        <div className="navbar-links">
          <Link to="/projetos" className="nav-link">Projetos</Link>
          <Link to="/alunos" className="nav-link">Alunos</Link>
          <Link to="/vinculos" className="nav-link">Vincular</Link>
          {user?.perfil === 'ADMINISTRADOR' && (
            <Link to="/auditoria" className="nav-link">Auditoria</Link>
          )}
          <span className="user-info">Olá, {user?.email}</span>
          <button onClick={handleLogout} className="logout-btn">Sair</button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;