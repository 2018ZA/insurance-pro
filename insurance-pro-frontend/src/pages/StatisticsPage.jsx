import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Spin, message, DatePicker, Space, Empty } from 'antd';
import { UserOutlined, FileTextOutlined, CalendarOutlined } from '@ant-design/icons';
import { Pie, Column, Bar } from '@ant-design/plots';
import api from '../api/axiosConfig';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

// Компонент страницы статистики
const StatisticsPage = () => {
  // Состояние для хранения данных статистики
  const [data, setData] = useState(null);
  
  // Состояние для отслеживания загрузки данных
  const [loading, setLoading] = useState(true);
  
  // Состояние для хранения выбранного периода (даты начала и конца)
  const [period, setPeriod] = useState([]);

  // Функция для загрузки данных статистики с сервера
  const fetchData = async (dates = []) => {
    setLoading(true); // Включаем индикатор загрузки
    try {
      const params = {};
      // Если выбран период, добавляем даты в параметры запроса
      if (dates && dates.length === 2) {
        params.startDate = dates[0].format('YYYY-MM-DD');
        params.endDate = dates[1].format('YYYY-MM-DD');
      }
      // Отправляем GET-запрос для получения статистики
      const response = await api.get('/statistics', { params });
      setData(response.data); // Сохраняем полученные данные
    } catch (error) {
      // В случае ошибки показываем сообщение пользователю
      message.error('Ошибка загрузки статистики');
    } finally {
      // Выключаем индикатор загрузки независимо от результата
      setLoading(false);
    }
  };

  // Эффект для загрузки данных при монтировании компонента и изменении периода
  useEffect(() => {
    fetchData(period); // Загружаем данные с текущим периодом
  }, [period]); // Зависимость от period - выполняется при его изменении

  // Обработчик изменения периода в RangePicker
  const onPeriodChange = (dates) => {
    setPeriod(dates); // Обновляем состояние периода
  };

  // Показываем спиннер загрузки, если данные еще не загружены
  if (loading && !data) return <Spin size="large" style={{ display: 'flex', justifyContent: 'center', marginTop: 50 }} />;

  // Конфигурация для круговой диаграммы (Pie chart)
  const pieConfig = {
    data: data?.contractsByType || [], // Данные по типам договоров
    angleField: 'count', // Поле для угла сектора (количество)
    colorField: 'type', // Поле для цвета (тип страхования)
    radius: 0.8, // Радиус диаграммы (0.8 = 80%)
    label: {
      text: (d) => `${d.type}: ${d.count}`, // Формат подписи: "Тип: количество"
      position: 'outside', // Позиция подписей вне диаграммы
    },
    legend: {
      color: {
        title: false, // Скрываем заголовок легенды
        position: 'right', // Позиция легенды справа
        rowPadding: 5, // Отступ между строками в легенде
      },
    },
  };

  // Конфигурация для горизонтальной столбчатой диаграммы (Bar chart)
  const barConfig = {
    data: data?.averagePremiumByType || [], // Данные по средней премии
    xField: 'type', // Ось X: тип страхования
    yField: 'averagePremium', // Ось Y: средняя премия
    label: {
      text: (d) => `${Math.round(d.averagePremium).toLocaleString()} ₽`, // Формат подписи с округлением
      position: 'right', // Позиция подписи справа от столбца
    },
    tooltip: {
      // Форматирование всплывающей подсказки
      formatter: (d) => ({ name: 'Средняя премия', value: `${d.averagePremium.toLocaleString()} ₽` }),
    },
  };

  // Конфигурация для вертикальной столбчатой диаграммы (Column chart)
  const columnConfig = {
    data: data?.dynamicByMonth || [], // Динамика по месяцам
    xField: 'month', // Ось X: месяц
    yField: 'count', // Ось Y: количество договоров
    label: {
      text: (d) => d.count, // Подпись с количеством
      position: 'top', // Позиция подписи сверху столбца
    },
    tooltip: {
      // Форматирование всплывающей подсказки
      formatter: (d) => ({ name: 'Количество договоров', value: d.count }),
    },
  };

  return (
    <div style={{ padding: '0 0 24px 0' }}>
      {/* Карточка с выбором периода и временем обновления */}
      <Card style={{ marginBottom: 24 }}>
        <Row gutter={[16, 16]} align="middle" justify="space-between">
          <Col xs={24} md={12}>
            <Space size="middle">
              <CalendarOutlined style={{ fontSize: 20, color: '#1890ff' }} />
              <span style={{ fontWeight: 500, fontSize: 16 }}>Период статистики:</span>
              {/* Компонент выбора диапазона дат */}
              <RangePicker 
                value={period} // Привязка к состоянию
                onChange={onPeriodChange} // Обработчик изменения
                allowClear // Разрешить очистку выбора
                placeholder={['Начало', 'Конец']} // Плейсхолдеры
              />
            </Space>
          </Col>
          <Col xs={24} md={12} style={{ textAlign: 'right' }}>
            {/* Отображение времени последнего обновления */}
            <span style={{ color: '#8c8c8c' }}>
              Данные обновлены: {dayjs().format('HH:mm:ss')}
            </span>
          </Col>
        </Row>
      </Card>

      {/* Верхний ряд: карточки с общей статистикой */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12}>
          <Card bordered={false} className="stat-card" style={{ boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}>
            <Statistic 
              title="Всего клиентов" 
              value={data?.totalClients} 
              prefix={<UserOutlined style={{ color: '#1890ff' }} />} 
              loading={loading} // Передаем состояние загрузки
            />
          </Card>
        </Col>
        <Col xs={24} sm={12}>
          <Card bordered={false} className="stat-card" style={{ boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}>
            <Statistic 
              title="Всего договоров" 
              value={data?.totalContracts} 
              prefix={<FileTextOutlined style={{ color: '#52c41a' }} />} 
              loading={loading}
            />
          </Card>
        </Col>
      </Row>

      {/* Средний ряд: две диаграммы (Pie и Bar) */}
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card 
            title="Распределение по типам страхования (Количество)" 
            bordered={false} 
            style={{ height: '100%', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
          >
            {/* Условный рендеринг: если есть данные - показываем диаграмму, иначе Empty */}
            {data?.contractsByType?.length > 0 ? (
              <Pie {...pieConfig} />
            ) : (
              <Empty description="Нет данных за указанный период" />
            )}
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card 
            title="Средняя страховая премия по типам (₽)" 
            bordered={false} 
            style={{ height: '100%', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
          >
            {data?.averagePremiumByType?.length > 0 ? (
              <Bar {...barConfig} />
            ) : (
              <Empty description="Нет данных за указанный период" />
            )}
          </Card>
        </Col>
      </Row>

      {/* Нижний ряд: одна большая диаграмма динамики */}
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col span={24}>
          <Card 
            title="Динамика заключения договоров по месяцам" 
            bordered={false} 
            style={{ boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}
          >
            {data?.dynamicByMonth?.length > 0 ? (
              <Column {...columnConfig} />
            ) : (
              <Empty description="Нет данных за указанный период" />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default StatisticsPage;