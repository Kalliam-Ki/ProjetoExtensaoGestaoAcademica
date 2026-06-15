import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import ProjetoCard from './ProjetoCard';
import LoadingSpinner from '../common/LoadingSpinner';
import './ProjetoList.css';

const ProjetoList = () => {
  const [projetos, setProjetos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarProjetos();
  }, []);

  const carregarProjetos = async () => {
    try {
      const response = await api.get('/projetos');
      setProjetos(response.data);
    } catch (error) {
      console.error('Erro ao carregar projetos', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="projetos-container">
      <div className="projetos-header">
        <h1>Projetos</h1>
        <Link to="/projetos/novo" className="btn btn-primary">+ Novo Projeto</Link>
      </div>
      <div className="projetos-grid">
        {projetos.map((projeto) => (
          <ProjetoCard key={projeto.id} projeto={projeto} />
        ))}
      </div>
    </div>
  );
};

export default ProjetoList;