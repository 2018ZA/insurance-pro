import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Space, Card, Modal, Form, message, Popconfirm, Row, Col } from 'antd';
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import api from '../api/axiosConfig';
import dayjs from 'dayjs';

// Главный компонент для работы с клиентами
const ClientsPage = () => {
  // Состояния компонента
  
  // Хранит список клиентов
  const [data, setData] = useState([]);
  
  // Флаг загрузки данных (для индикатора загрузки в таблице)
  const [loading, setLoading] = useState(false);
  
  // Настройки пагинации таблицы
  const [pagination, setPagination] = useState({ 
    current: 1,     // текущая страница
    pageSize: 10,   // количество записей на странице
    total: 0        // общее количество записей
  });
  
  // Фильтры для поиска клиентов
  const [filters, setFilters] = useState({ 
    fullName: '',   // фильтр по ФИО
    passport: '',   // фильтр по паспорту
    phone: ''       // фильтр по телефону
  });
  
  // Видимость модального окна (для создания/редактирования клиента)
  const [isModalVisible, setIsModalVisible] = useState(false);
  
  // Данные редактируемого клиента (null при создании нового)
  const [editingClient, setEditingClient] = useState(null);
  
  // Форма для создания/редактирования клиента
  const [form] = Form.useForm();

  /**
   * Функция загрузки данных с сервера
   * @param {Object} params - параметры пагинации
   */
  const fetchData = async (params = {}) => {
    setLoading(true); // Показать индикатор загрузки
    try {
      // Выполняем GET-запрос к API
      const response = await api.get('/clients', {
        params: {
          page: (params.current || pagination.current) - 1, // API ожидает нумерацию с 0
          size: params.pageSize || pagination.pageSize,     // количество элементов на странице
          fullName: filters.fullName,                       // фильтр по ФИО
          passport: filters.passport,                       // фильтр по паспорту
          phone: filters.phone,                             // фильтр по телефону
          sort: 'registrationDate,desc'                     // сортировка по дате регистрации (новые сверху)
        }
      });
      
      // Обновляем данные в состоянии
      setData(response.data.content);
      
      // Обновляем пагинацию
      setPagination({
        ...params,
        total: response.data.totalElements, // общее количество элементов
      });
    } catch (error) {
      // Обработка ошибки загрузки
      message.error('Ошибка загрузки данных');
    } finally {
      // Скрыть индикатор загрузки в любом случае
      setLoading(false);
    }
  };

  // Эффект для загрузки данных при изменении фильтров
  useEffect(() => {
    fetchData();
  }, [filters]); // Зависимость от фильтров - при их изменении перезагружаем данные

  /**
   * Обработчик изменения страницы или размера страницы в таблице
   * @param {Object} newPagination - новые параметры пагинации
   */
  const handleTableChange = (newPagination) => {
    fetchData(newPagination);
  };

  /**
   * Показать модальное окно для создания/редактирования клиента
   * @param {Object|null} client - объект клиента или null для создания нового
   */
  const showModal = (client = null) => {
    setEditingClient(client); // Устанавливаем редактируемого клиента
    if (client) {
      // Если редактируем существующего клиента - заполняем форму его данными
      form.setFieldsValue(client);
    } else {
      // Если создаем нового - очищаем форму
      form.resetFields();
    }
    setIsModalVisible(true); // Показать модальное окно
  };

  /**
   * Обработчик сохранения клиента (создание или обновление)
   */
  const handleOk = async () => {
    try {
      // Валидация формы
      const values = await form.validateFields();
      
      if (editingClient) {
        // Если редактируем существующего клиента - PUT запрос
        await api.put(`/clients/${editingClient.id}`, values);
        message.success('Клиент обновлен');
      } else {
        // Если создаем нового клиента - POST запрос
        await api.post('/clients', values);
        message.success('Клиент добавлен');
      }
      
      // Закрыть модальное окно
      setIsModalVisible(false);
      
      // Обновить данные в таблице
      fetchData();
    } catch (error) {
      // Обработка ошибки сохранения
      message.error(error.response?.data?.message || 'Ошибка при сохранении');
    }
  };

  /**
   * Обработчик удаления клиента
   * @param {string} id - идентификатор клиента
   */
  const handleDelete = async (id) => {
    try {
      // DELETE запрос для удаления клиента
      await api.delete(`/clients/${id}`);
      message.success('Клиент удален');
      
      // Обновить данные в таблице
      fetchData();
    } catch (error) {
      // Обработка ошибки удаления
      message.error(error.response?.data?.message || 'Не удалось удалить клиента');
    }
  };

  // Определение колонок таблицы
  const columns = [
    { 
      title: 'ФИО', 
      dataIndex: 'fullName', 
      key: 'fullName', 
      sorter: true // Включить сортировку по этой колонке
    },
    { 
      title: 'Паспорт', 
      key: 'passport', 
      // Комбинируем серию и номер паспорта в одну строку
      render: (text, record) => `${record.passportSeries || ''} ${record.passportNumber || ''}`
    },
    { title: 'Телефон', dataIndex: 'phone', key: 'phone' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { 
      title: 'Дата регистрации', 
      dataIndex: 'registrationDate', 
      key: 'registrationDate', 
      // Форматирование даты в читаемый вид
      render: (date) => dayjs(date).format('DD.MM.YYYY HH:mm')
    },
    {
      title: 'Действия',
      key: 'action',
      // Кнопки действий для каждой строки
      render: (_, record) => (
        <Space size="middle">
          {/* Кнопка редактирования */}
          <Button icon={<EditOutlined />} onClick={() => showModal(record)} />
          
          {/* Кнопка удаления с подтверждением */}
          <Popconfirm 
            title="Удалить клиента?" 
            onConfirm={() => handleDelete(record.id)}
          >
            <Button icon={<DeleteOutlined />} danger />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // Рендеринг компонента
  return (
    <div>
      {/* Карточка с фильтрами и кнопкой добавления */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={[16, 16]} align="middle">
          {/* Поле фильтра по ФИО */}
          <Col xs={24} sm={12} md={6}>
            <Input
              placeholder="ФИО"
              value={filters.fullName}
              onChange={(e) => setFilters({ ...filters, fullName: e.target.value })}
              prefix={<SearchOutlined />}
              allowClear // Кнопка очистки поля
            />
          </Col>
          
          {/* Поле фильтра по паспорту */}
          <Col xs={24} sm={12} md={6}>
            <Input
              placeholder="Номер паспорта"
              value={filters.passport}
              onChange={(e) => setFilters({ ...filters, passport: e.target.value })}
              allowClear
            />
          </Col>
          
          {/* Поле фильтра по телефону */}
          <Col xs={24} sm={12} md={6}>
            <Input
              placeholder="Телефон"
              value={filters.phone}
              onChange={(e) => setFilters({ ...filters, phone: e.target.value })}
              allowClear
            />
          </Col>
          
          {/* Кнопка добавления нового клиента */}
          <Col xs={24} sm={12} md={6}>
            <Button 
              type="primary" 
              icon={<PlusOutlined />} 
              onClick={() => showModal()} 
              block // Растянуть на всю ширину колонки
            >
              Добавить клиента
            </Button>
          </Col>
        </Row>
      </Card>

      {/* Основная таблица с клиентами */}
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id" // Уникальный ключ для каждой строки
        pagination={pagination}
        loading={loading}
        onChange={handleTableChange} // Обработчик изменения пагинации
        scroll={{ x: 800 }} // Горизонтальная прокрутка для малых экранов
      />

      {/* Модальное окно для создания/редактирования клиента */}
      <Modal
        title={editingClient ? 'Редактировать клиента' : 'Новый клиент'}
        open={isModalVisible}
        onOk={handleOk} // Обработчик сохранения
        onCancel={() => setIsModalVisible(false)} // Обработчик закрытия
      >
        <Form form={form} layout="vertical">
          {/* Поле ФИО */}
          <Form.Item 
            name="fullName" 
            label="ФИО" 
            rules={[{ required: true, message: 'Введите ФИО' }]}
          >
            <Input />
          </Form.Item>
          
          {/* Поля паспорта */}
          <Space>
            <Form.Item name="passportSeries" label="Серия паспорта">
              <Input style={{ width: 100 }} />
            </Form.Item>
            <Form.Item 
              name="passportNumber" 
              label="Номер паспорта" 
              rules={[{ required: true, message: 'Введите номер паспорта' }]}
            >
              <Input />
            </Form.Item>
          </Space>
          
          {/* Поле телефона */}
          <Form.Item 
            name="phone" 
            label="Телефон" 
            rules={[{ required: true, message: 'Введите телефон' }]}
          >
            <Input />
          </Form.Item>
          
          {/* Поле email */}
          <Form.Item 
            name="email" 
            label="Email" 
            rules={[{ type: 'email', message: 'Некорректный email' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ClientsPage;