import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import LoadingSpinner from '../common/LoadingSpinner';
import './AlunoList.css';

const AlunoList = () => {
  const [alunos, setAlunos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarAlunos();
  }, []);

  const carregarAlunos = async () => {
    try {
      const response = await api.get('/alunos');
      setAlunos(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="alunos-container">
      <div className="alunos-header">
        <h1>Alunos</h1>
        <Link to="/alunos/novo" className="btn btn-primary">+ Novo Aluno</Link>
      </div>
      <div className="alunos-grid">
        {alunos.map((aluno) => (
          <div key={aluno.id} className="aluno-card">
            <h3>{aluno.nome}</h3>
            <p>Matrícula: {aluno.matricula}</p>
            <p>Curso: {aluno.curso}</p>
            <p>Período: {aluno.periodo}</p>
            <p className={aluno.ativo ? 'ativo' : 'inativo'}>
              {aluno.ativo ? 'Ativo' : 'Inativo'}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AlunoList;