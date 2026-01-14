import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import ClientsPage from './pages/ClientsPage';
import ContractsPage from './pages/ContractsPage';
import StatisticsPage from './pages/StatisticsPage';
import AboutAuthorPage from './pages/AboutAuthorPage';
import './App.css';

// Основной компонент приложения
function App() {
  return (
    // BrowserRouter обеспечивает клиентскую маршрутизацию
    <BrowserRouter>
      {/* Routes - контейнер для всех маршрутов */}
      <Routes>
        {/* Публичный маршрут для страницы входа */}
        <Route path="/login" element={<LoginPage />} />
        
        {/* Защищенная область приложения (требует аутентификации) */}
        <Route
          path="/" // Базовый путь для всех защищенных страниц
          element={
            // ProtectedRoute проверяет наличие токена авторизации
            <ProtectedRoute>
              {/* MainLayout - общий макет для защищенных страниц (шапка, меню, футер) */}
              <MainLayout />
            </ProtectedRoute>
          }
        >
          {/* Вложенные маршруты отображаются внутри MainLayout */}
          
          {/* Корневой маршрут "/" - перенаправляет на "/clients" */}
          <Route index element={<Navigate to="/clients" replace />} />
          
          {/* Страница управления клиентами */}
          <Route path="clients" element={<ClientsPage />} />
          
          {/* Страница управления договорами */}
          <Route path="contracts" element={<ContractsPage />} />
          
          {/* Страница со статистикой */}
          <Route path="statistics" element={<StatisticsPage />} />
          
          {/* Страница "Об авторе" */}
          <Route path="about" element={<AboutAuthorPage />} />
        </Route>

        {/* "Ловушка" для несуществующих маршрутов - перенаправляет на корень */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;