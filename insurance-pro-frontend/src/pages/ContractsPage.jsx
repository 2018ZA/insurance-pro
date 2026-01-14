import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Space, Card, Modal, Form, message, Popconfirm, Select, DatePicker, Tag, Row, Col } from 'antd';
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import api from '../api/axiosConfig';
import dayjs from 'dayjs';

// Компоненты Ant Design
const { Option } = Select; // Опция для выпадающего списка
const { RangePicker } = DatePicker; // Компонент выбора диапазона дат

// Компонент для работы со страховыми договорами
const ContractsPage = () => {
  // ========== СОСТОЯНИЯ КОМПОНЕНТА ==========
  
  // Список договоров для таблицы
  const [data, setData] = useState([]);
  
  // Флаг загрузки данных
  const [loading, setLoading] = useState(false);
  
  // Настройки пагинации
  const [pagination, setPagination] = useState({ 
    current: 1,     // текущая страница
    pageSize: 10,   // элементов на странице
    total: 0        // всего элементов
  });
  
  // Фильтры для поиска договоров
  const [filters, setFilters] = useState({ 
    contractNumber: '',  // номер договора
    insuranceType: '',   // тип страхования
    status: '',          // статус договора
    period: []           // диапазон дат [начало, конец]
  });
  
  // Состояния модального окна
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingContract, setEditingContract] = useState(null);
  
  // Форма для создания/редактирования
  const [form] = Form.useForm();
  
  // Справочники (загружаются один раз при монтировании)
  const [clients, setClients] = useState([]);           // список клиентов
  const [insuranceTypes, setInsuranceTypes] = useState([]); // типы страхования
  const [contractStatuses, setContractStatuses] = useState([]); // статусы договоров

  /**
   * Загрузка списка договоров с сервера с учетом фильтров и пагинации
   * @param {Object} params - параметры пагинации
   */
  const fetchData = async (params = {}) => {
    setLoading(true); // Показать индикатор загрузки
    try {
      // Формируем параметры запроса
      const response = await api.get('/contracts', {
        params: {
          page: (params.current || pagination.current) - 1, // API: нумерация с 0
          size: params.pageSize || pagination.pageSize,
          contractNumber: filters.contractNumber,  // фильтр по номеру
          insuranceType: filters.insuranceType,    // фильтр по типу
          status: filters.status,                  // фильтр по статусу
          start: filters.period?.[0]?.format('YYYY-MM-DD'), // начало периода
          end: filters.period?.[1]?.format('YYYY-MM-DD'),   // конец периода
          sort: 'createdAt,desc' // сортировка по дате создания (новые сверху)
        }
      });
      
      // Обновляем данные таблицы
      setData(response.data.content);
      
      // Обновляем пагинацию
      setPagination({
        ...params,
        total: response.data.totalElements,
      });
    } catch (error) {
      message.error('Ошибка загрузки данных');
    } finally {
      setLoading(false); // Скрыть индикатор загрузки
    }
  };

  /**
   * Загрузка справочников (типы страхования, статусы, клиенты)
   * Выполняется один раз при загрузке страницы
   */
  const fetchDictionaries = async () => {
    try {
      // Параллельная загрузка всех справочников для ускорения
      const [typesRes, statusesRes, clientsRes] = await Promise.all([
        api.get('/dictionaries/insurance-types'),      // типы страхования
        api.get('/dictionaries/contract-statuses'),    // статусы договоров
        api.get('/clients?size=1000')                  // клиенты (все)
      ]);
      
      // Сохраняем справочники в состоянии
      setInsuranceTypes(typesRes.data);
      setContractStatuses(statusesRes.data);
      setClients(clientsRes.data.content);
    } catch (error) {
      message.error('Ошибка загрузки справочников');
    }
  };

  // ========== ЭФФЕКТЫ (HOOKS) ==========
  
  // Загрузить справочники при первом рендере
  useEffect(() => {
    fetchDictionaries();
  }, []); // Пустой массив зависимостей = выполнить один раз

  // Загрузить данные при изменении фильтров
  useEffect(() => {
    fetchData();
  }, [filters]); // Зависимость от фильтров

  // ========== ОБРАБОТЧИКИ СОБЫТИЙ ==========
  
  /**
   * Обработчик изменения пагинации таблицы
   * @param {Object} newPagination - новые параметры пагинации
   */
  const handleTableChange = (newPagination) => {
    fetchData(newPagination);
  };

  /**
   * Открыть модальное окно для создания/редактирования договора
   * @param {Object|null} contract - договор для редактирования или null для создания
   */
  const showModal = (contract = null) => {
    setEditingContract(contract); // Сохраняем редактируемый договор
    
    if (contract) {
      // Для редактирования: заполняем форму данными договора
      form.setFieldsValue({
        ...contract,
        // Преобразуем даты в формат dayjs для RangePicker
        dates: [dayjs(contract.startDate), dayjs(contract.endDate)]
      });
    } else {
      // Для создания: очищаем форму
      form.resetFields();
    }
    
    setIsModalVisible(true); // Показать модальное окно
  };

  /**
   * Сохранение договора (создание или обновление)
   */
  const handleOk = async () => {
    try {
      // Валидация формы
      const values = await form.validateFields();
      
      // Формируем payload для API
      const payload = {
        ...values,
        // Преобразуем даты из RangePicker в строковый формат
        startDate: values.dates[0].format('YYYY-MM-DD'),
        endDate: values.dates[1].format('YYYY-MM-DD'),
      };
      
      // Удаляем временное поле dates из payload
      delete payload.dates;
      
      if (editingContract) {
        // PUT запрос для обновления существующего договора
        await api.put(`/contracts/${editingContract.id}`, payload);
        message.success('Договор обновлен');
      } else {
        // POST запрос для создания нового договора
        await api.post('/contracts', payload);
        message.success('Договор оформлен');
      }
      
      // Закрываем модальное окно
      setIsModalVisible(false);
      
      // Обновляем данные в таблице
      fetchData();
    } catch (error) {
      message.error(error.response?.data?.message || 'Ошибка при сохранении');
    }
  };

  /**
   * Удаление договора
   * @param {string} id - ID договора
   */
  const handleDelete = async (id) => {
    try {
      await api.delete(`/contracts/${id}`);
      message.success('Договор удален');
      fetchData(); // Обновить таблицу после удаления
    } catch (error) {
      message.error(error.response?.data?.message || 'Не удалось удалить договор');
    }
  };

  /**
   * Получить цветной тег для отображения статуса договора
   * @param {string} code - код статуса
   * @returns {JSX.Element} - компонент Tag с цветом
   */
  const getStatusTag = (code) => {
    // Цветовая схема для разных статусов
    const colors = { 
      ACTIVE: 'green',      // активен - зеленый
      TERMINATED: 'red',    // расторгнут - красный
      EXPIRED: 'orange',    // истек - оранжевый
      DRAFT: 'default'      // черновик - серый
    };
    return <Tag color={colors[code] || 'blue'}>{code}</Tag>;
  };

  // ========== КОЛОНКИ ТАБЛИЦЫ ==========
  
  const columns = [
    { title: '№ Договора', dataIndex: 'contractNumber', key: 'contractNumber' },
    { title: 'Клиент', dataIndex: 'clientFullName', key: 'clientFullName' },
    { title: 'Тип', dataIndex: 'insuranceTypeName', key: 'insuranceTypeName' },
    { 
      title: 'Статус', 
      dataIndex: 'statusCode', 
      key: 'statusCode', 
      render: (code) => getStatusTag(code) // Отображаем статус как цветной тег
    },
    { 
      title: 'Период', 
      key: 'period', 
      // Форматируем даты в краткий вид: "01.01.24 - 31.12.24"
      render: (_, record) => `${dayjs(record.startDate).format('DD.MM.YY')} - ${dayjs(record.endDate).format('DD.MM.YY')}`
    },
    { 
      title: 'Премия', 
      dataIndex: 'premiumAmount', 
      key: 'premiumAmount', 
      render: (val) => `${val} ₽` // Добавляем знак рубля
    },
    {
      title: 'Действия',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button icon={<EditOutlined />} onClick={() => showModal(record)} />
          <Popconfirm title="Удалить договор?" onConfirm={() => handleDelete(record.id)}>
            <Button icon={<DeleteOutlined />} danger />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // ========== РЕНДЕРИНГ КОМПОНЕНТА ==========
  
  return (
    <div>
      {/* Карточка с фильтрами */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={[16, 16]} align="middle">
          {/* Фильтр по номеру договора */}
          <Col xs={24} sm={12} md={6}>
            <Input
              placeholder="Номер договора"
              value={filters.contractNumber}
              onChange={(e) => setFilters({ ...filters, contractNumber: e.target.value })}
              prefix={<SearchOutlined />}
              allowClear
            />
          </Col>
          
          {/* Фильтр по типу страхования (выпадающий список) */}
          <Col xs={24} sm={12} md={6}>
            <Select
              placeholder="Тип страхования"
              style={{ width: '100%' }}
              allowClear
              value={filters.insuranceType}
              onChange={(val) => setFilters({ ...filters, insuranceType: val })}
            >
              {/* Динамически заполняем опциями из справочника */}
              {insuranceTypes.map(t => 
                <Option key={t.code} value={t.code}>{t.name}</Option>
              )}
            </Select>
          </Col>
          
          {/* Фильтр по статусу договора */}
          <Col xs={24} sm={12} md={6}>
            <Select
              placeholder="Статус"
              style={{ width: '100%' }}
              allowClear
              value={filters.status}
              onChange={(val) => setFilters({ ...filters, status: val })}
            >
              {contractStatuses.map(s => 
                <Option key={s.code} value={s.code}>{s.name}</Option>
              )}
            </Select>
          </Col>
          
          {/* Фильтр по периоду действия (диапазон дат) */}
          <Col xs={24} sm={12} md={6}>
            <RangePicker
              style={{ width: '100%' }}
              value={filters.period}
              onChange={(dates) => setFilters({ ...filters, period: dates })}
            />
          </Col>
          
          {/* Кнопка добавления нового договора */}
          <Col xs={24}>
            <Button 
              type="primary" 
              icon={<PlusOutlined />} 
              onClick={() => showModal()} 
              block
            >
              Новый договор
            </Button>
          </Col>
        </Row>
      </Card>

      {/* Таблица договоров */}
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        pagination={pagination}
        loading={loading}
        onChange={handleTableChange}
        scroll={{ x: 1000 }} // Горизонтальный скролл для малых экранов
      />

      {/* Модальное окно создания/редактирования договора */}
      <Modal
        title={editingContract ? 'Редактировать договор' : 'Новый договор'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        width={600} // Ширина модального окна
      >
        <Form form={form} layout="vertical">
          {/* Выбор клиента с поиском */}
          <Form.Item 
            name="clientId" 
            label="Клиент" 
            rules={[{ required: true, message: 'Выберите клиента' }]}
          >
            <Select showSearch optionFilterProp="children">
              {clients.map(c => 
                <Option key={c.id} value={c.id}>{c.fullName}</Option>
              )}
            </Select>
          </Form.Item>
          
          {/* Тип страхования и статус в одной строке */}
          <Space>
            <Form.Item 
              name="insuranceTypeCode" 
              label="Тип страхования" 
              rules={[{ required: true, message: 'Выберите тип' }]}
            >
              <Select style={{ width: 250 }}>
                {insuranceTypes.map(t => 
                  <Option key={t.code} value={t.code}>{t.name}</Option>
                )}
              </Select>
            </Form.Item>
            
            <Form.Item 
              name="statusCode" 
              label="Статус" 
              rules={[{ required: true, message: 'Выберите статус' }]}
            >
              <Select style={{ width: 150 }}>
                {contractStatuses.map(s => 
                  <Option key={s.code} value={s.code}>{s.name}</Option>
                )}
              </Select>
            </Form.Item>
          </Space>
          
          {/* Период действия договора */}
          <Form.Item 
            name="dates" 
            label="Период действия" 
            rules={[{ required: true, message: 'Выберите даты' }]}
          >
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>
          
          {/* Суммы: премия и страховая сумма */}
          <Space>
            <Form.Item 
              name="premiumAmount" 
              label="Страховая премия" 
              rules={[{ required: true, message: 'Введите премию' }]}
            >
              <Input type="number" suffix="₽" />
            </Form.Item>
            
            <Form.Item 
              name="insuredAmount" 
              label="Страховая сумма" 
              rules={[{ required: true, message: 'Введите сумму' }]}
            >
              <Input type="number" suffix="₽" />
            </Form.Item>
          </Space>
        </Form>
      </Modal>
    </div>
  );
};

export default ContractsPage;