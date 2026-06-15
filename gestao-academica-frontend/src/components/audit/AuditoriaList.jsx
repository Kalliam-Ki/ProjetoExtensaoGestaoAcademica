import { useEffect, useState } from 'react';
import api from '../../services/api';
import LoadingSpinner from '../common/LoadingSpinner';
import './AuditoriaList.css';

const AuditoriaList = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarLogs();
  }, []);

  const carregarLogs = async () => {
    try {
      const response = await api.get('/auditoria');
      setLogs(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="auditoria-container">
      <h1>Logs de Auditoria</h1>
      <div className="auditoria-table-wrapper">
        <table className="auditoria-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tabela</th>
              <th>Registro ID</th>
              <th>Ação</th>
              <th>Usuário</th>
              <th>Data</th>
              <th>IP</th>
            </tr>
          </thead>
          <tbody>
            {logs.map(log => (
              <tr key={log.id}>
                <td>{log.id}</td>
                <td>{log.tabelaAfetada}</td>
                <td>{log.idRegistroAfetado}</td>
                <td>{log.acao}</td>
                <td>{log.usuarioNome}</td>
                <td>{new Date(log.dataAlteracao).toLocaleString()}</td>
                <td>{log.ipRequisicao}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AuditoriaList;