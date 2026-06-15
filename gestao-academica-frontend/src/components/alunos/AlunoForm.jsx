import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './AlunoForm.css';

const AlunoForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nome: '',
    matricula: '',
    curso: '',
    email: '',
    periodo: 1
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/alunos', formData);
      navigate('/alunos');
    } catch (err) {
      setError(err.response?.data?.erro || 'Erro ao criar aluno');
    }
  };

  return (
    <div className="aluno-form-container">
      <div className="aluno-form-card">
        <h1>Novo Aluno</h1>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome</label>
            <input name="nome" value={formData.nome} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Matrícula</label>
            <input name="matricula" value={formData.matricula} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Curso</label>
            <input name="curso" value={formData.curso} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" name="email" value={formData.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Período</label>
            <input type="number" name="periodo" min="1" max="12" value={formData.periodo} onChange={handleChange} required />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary">Salvar</button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/alunos')}>Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AlunoForm;