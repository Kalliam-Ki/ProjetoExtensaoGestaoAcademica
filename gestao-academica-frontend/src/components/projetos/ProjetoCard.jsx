import './ProjetoCard.css';

const getStatusColor = (status) => {
  const colors = {
    RASCUNHO: 'status-rascunho',
    SUBMETIDO: 'status-submetido',
    EM_ANALISE: 'status-analise',
    APROVADO: 'status-aprovado',
    EM_ANDAMENTO: 'status-andamento',
    CONCLUIDO: 'status-concluido',
  };
  return colors[status] || '';
};

const ProjetoCard = ({ projeto }) => {
  return (
    <div className="projeto-card">
      <h3 className="projeto-titulo">{projeto.titulo}</h3>
      <p className="projeto-orientador">Orientador: {projeto.orientador}</p>
      <p className="projeto-area">Área: {projeto.area}</p>
      <p className="projeto-status">
        Status: <span className={getStatusColor(projeto.status)}>{projeto.status}</span>
      </p>
      <p className="projeto-data">
        Criado em: {new Date(projeto.dataCriacao).toLocaleDateString()}
      </p>
    </div>
  );
};

export default ProjetoCard;