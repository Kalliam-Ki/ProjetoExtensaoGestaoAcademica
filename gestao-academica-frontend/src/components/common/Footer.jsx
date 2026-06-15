import './Footer.css';

const Footer = () => {
  const anoAtual = new Date().getFullYear();

  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section">
          <h4>Gestão Acadêmica</h4>
          <p>Sistema de gerenciamento de projetos de extensão.</p>
        </div>
        <div className="footer-section">
          <h4>Links Rápidos</h4>
          <ul>
            <li><a href="/projetos">Projetos</a></li>
            <li><a href="/alunos">Alunos</a></li>
            <li><a href="/vinculos">Vincular Aluno</a></li>
          </ul>
        </div>
        <div className="footer-section">
          <h4>Contato</h4>
          <p>Email: contato@gestaoacademica.com</p>
          <p>Telefone: (11) 1234-5678</p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>&copy; {anoAtual} Gestão Acadêmica. Todos os direitos reservados.</p>
      </div>
    </footer>
  );
};

export default Footer;