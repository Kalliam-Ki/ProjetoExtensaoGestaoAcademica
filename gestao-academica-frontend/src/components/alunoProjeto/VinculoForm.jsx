import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './VinculoForm.css';

const VinculoForm = () => {
  const navigate = useNavigate();
  const [alunos, setAlunos] = useState([]);
  const [projetos, setProjetos] = useState([]);
  const [formData, setFormData] = useState({
    alunoId: '',
    projetoId: '',
    funcao: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    carregarAlunos();
    carregarProjetos();
  }, []);

  const carregarAlunos = async () => {
    try {
      const response = await api.get('/alunos');
      setAlunos(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const carregarProjetos = async () => {
    try {
      const response = await api.get('/projetos');
      setProjetos(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    try {
      await api.post('/aluno-projeto/vincular', formData);
      setSuccess('Vínculo realizado com sucesso!');
      setTimeout(() => navigate('/projetos'), 2000);
    } catch (err) {
      setError(err.response?.data?.erro || 'Erro ao vincular');
    }
  };

  return (
    <div className="vinculo-container">
      <div className="vinculo-card">
        <h1>Vincular Aluno a Projeto</h1>
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Aluno</label>
            <select name="alunoId" value={formData.alunoId} onChange={handleChange} required>
              <option value="">Selecione um aluno</option>
              {alunos.map(aluno => (
                <option key={aluno.id} value={aluno.id}>{aluno.nome} - {aluno.matricula}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Projeto</label>
            <select name="projetoId" value={formData.projetoId} onChange={handleChange} required>
              <option value="">Selecione um projeto</option>
              {projetos.map(projeto => (
                <option key={projeto.id} value={projeto.id}>{projeto.titulo} - {projeto.status}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Função</label>
            <input name="funcao" value={formData.funcao} onChange={handleChange} required />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary">Vincular</button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/projetos')}>Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default VinculoForm;