import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import PrivateRoute from './components/common/PrivateRoute';
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import Login from './components/auth/Login';
import ProjetoList from './components/projetos/ProjetoList';
import ProjetoForm from './components/projetos/ProjetoForm';
import AlunoList from './components/alunos/AlunoList';
import AlunoForm from './components/alunos/AlunoForm';
import VinculoForm from './components/alunoProjeto/VinculoForm';
import AuditoriaList from './components/audit/AuditoriaList';
import './App.css';

function AppContent() {
  const { user } = useAuth();

  return (
    <>
      {user && <Navbar />}
      <div className="app-main">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<PrivateRoute><ProjetoList /></PrivateRoute>} />
          <Route path="/projetos" element={<PrivateRoute><ProjetoList /></PrivateRoute>} />
          <Route path="/projetos/novo" element={<PrivateRoute><ProjetoForm /></PrivateRoute>} />
          <Route path="/alunos" element={<PrivateRoute><AlunoList /></PrivateRoute>} />
          <Route path="/alunos/novo" element={<PrivateRoute><AlunoForm /></PrivateRoute>} />
          <Route path="/vinculos" element={<PrivateRoute><VinculoForm /></PrivateRoute>} />
          {user?.perfil === 'ADMINISTRADOR' && (
            <Route path="/auditoria" element={<PrivateRoute><AuditoriaList /></PrivateRoute>} />
          )}
        </Routes>
      </div>
      {user && <Footer />} 
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;