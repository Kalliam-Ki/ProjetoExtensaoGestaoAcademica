import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './ProjetoForm.css';

const ProjetoForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    titulo: '',
    descricao: '',
    orientador: '',
    area: '',
    dataPrevistaTermino: ''
  });
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/projetos', formData);
      navigate('/projetos');
    } catch (err) {
      setError(err.response?.data?.erro || 'Erro ao criar projeto');
    }
  };

  return (
    <div className="form-container">
      <div className="form-card">
        <h1>Novo Projeto</h1>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Título</label>
            <input name="titulo" value={formData.titulo} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Descrição</label>
            <textarea name="descricao" rows="3" value={formData.descricao} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label>Orientador</label>
            <input name="orientador" value={formData.orientador} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Área</label>
            <input name="area" value={formData.area} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Data Prevista Término</label>
            <input type="date" name="dataPrevistaTermino" value={formData.dataPrevistaTermino} onChange={handleChange} required />
          </div>
          <div className="form-actions">
            <button type="submit" className="btn btn-primary">Salvar</button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/projetos')}>Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProjetoForm;