import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Layout } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const { Content } = Layout;

// Компонент страницы авторизации
const LoginPage = () => {
  // Состояние для отслеживания загрузки (отправки формы)
  const [loading, setLoading] = useState(false);
  
  // Хук для программной навигации между маршрутами
  const navigate = useNavigate();

  // Обработчик отправки формы
  const onFinish = async (values) => {
    setLoading(true); // Активируем состояние загрузки
    try {
      // Отправляем POST-запрос на эндпоинт авторизации
      const response = await api.post('/auth/login', values);
      
      // Сохраняем токен в localStorage для последующих авторизованных запросов
      localStorage.setItem('token', response.data.token);
      
      // Сохраняем данные пользователя (может содержать роль, имя и т.д.)
      localStorage.setItem('user', JSON.stringify(response.data));
      
      // Показываем сообщение об успехе
      message.success('Успешный вход!');
      
      // Перенаправляем пользователя на страницу клиентов
      navigate('/clients');
    } catch (error) {
      // Логируем ошибку для отладки
      console.error(error);
      
      // Показываем пользователю сообщение об ошибке
      message.error('Ошибка авторизации. Проверьте логин и пароль.');
    } finally {
      // Выключаем состояние загрузки независимо от результата
      setLoading(false);
    }
  };

  return (
    // Основной контейнер с градиентным фоном
    <Layout style={{ 
      minHeight: '100vh', 
      background: 'linear-gradient(135deg, #1890ff 0%, #001529 100%)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center'
    }}>
      {/* Контентная область с центрированием */}
      <Content style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center',
        padding: '20px',
        width: '100%'
      }}>
        {/* Карточка с формой логина */}
        <Card 
          style={{ 
            width: '100%', 
            maxWidth: 400, // Ограничение максимальной ширины
            boxShadow: '0 10px 25px rgba(0,0,0,0.2)', // Тень для выделения
            borderRadius: 12 // Скругление углов
          }}
        >
          {/* Заголовок карточки */}
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <h2 style={{ margin: 0, color: '#1890ff' }}>InsurancePro</h2>
            <p style={{ color: '#8c8c8c' }}>Вход в систему управления страхованием</p>
          </div>
          
          {/* Форма авторизации */}
          <Form
            name="login"
            onFinish={onFinish} // Обработчик при успешной валидации и отправке
            autoComplete="off" // Отключаем автозаполнение браузера
            layout="vertical" // Вертикальное расположение полей
          >
            {/* Поле для ввода логина */}
            <Form.Item
              name="username"
              rules={[{ required: true, message: 'Введите логин!' }]} // Правило валидации
            >
              <Input 
                prefix={<UserOutlined style={{ color: '#bfbfbf' }} />} // Иконка перед полем
                placeholder="Логин (admin/agent1/manager1)" // Примеры логинов
                size="large" // Большой размер поля
              />
            </Form.Item>

            {/* Поле для ввода пароля */}
            <Form.Item
              name="password"
              rules={[{ required: true, message: 'Введите пароль!' }]} // Правило валидации
            >
              <Input.Password 
                prefix={<LockOutlined style={{ color: '#bfbfbf' }} />} // Иконка замка
                placeholder="Пароль"
                size="large"
              />
            </Form.Item>

            {/* Кнопка отправки формы */}
            <Form.Item style={{ marginBottom: 0 }}>
              <Button 
                type="primary" // Основной стиль кнопки
                htmlType="submit" // Тип кнопки - отправка формы
                loading={loading} // Отображение индикатора загрузки
                block // Растягивание на всю ширину
                size="large"
                style={{ 
                  height: 48, 
                  borderRadius: 8, 
                  fontSize: 16, 
                  fontWeight: 600 
                }}
              >
                Войти
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </Content>
    </Layout>
  );
};

export default LoginPage;