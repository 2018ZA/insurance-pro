import React, { useState } from 'react';
import { Layout, Menu, Button, theme, ConfigProvider } from 'antd';
import {
  UserOutlined,
  FileTextOutlined,
  BarChartOutlined,
  InfoCircleOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation, Outlet } from 'react-router-dom';
import ruRU from 'antd/locale/ru_RU';

const { Header, Sider, Content } = Layout;

const MainLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const menuItems = [
    {
      key: '/clients',
      icon: <UserOutlined />,
      label: 'Клиенты',
    },
    {
      key: '/contracts',
      icon: <FileTextOutlined />,
      label: 'Договоры',
    },
    {
      key: '/statistics',
      icon: <BarChartOutlined />,
      label: 'Статистика',
    },
    {
      key: '/about',
      icon: <InfoCircleOutlined />,
      label: 'Об авторе',
    },
  ];

  return (
    <ConfigProvider locale={ruRU}>
      <Layout style={{ minHeight: '100vh' }}>
        <Sider
          trigger={null}
          collapsible
          collapsed={collapsed}
          breakpoint="lg"
          collapsedWidth="80"
          onBreakpoint={(broken) => {
            setCollapsed(broken);
          }}
          style={{
            overflow: 'auto',
            height: '100vh',
            position: 'fixed',
            left: 0,
            top: 0,
            bottom: 0,
            zIndex: 100,
          }}
        >
          <div style={{ height: 64, margin: 16, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'rgba(255, 255, 255, 0.1)', borderRadius: 8 }}>
            <span style={{ color: 'white', fontSize: collapsed ? 12 : 18, fontWeight: 'bold', transition: 'all 0.2s' }}>
              {collapsed ? 'IP' : 'InsurancePro'}
            </span>
          </div>
          <Menu
            theme="dark"
            mode="inline"
            selectedKeys={[location.pathname]}
            items={menuItems}
            onClick={({ key }) => navigate(key)}
          />
        </Sider>
        <Layout style={{ marginLeft: collapsed ? 80 : 200, transition: 'all 0.2s' }}>
          <Header style={{ 
            padding: '0 24px', 
            background: colorBgContainer, 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            position: 'sticky',
            top: 0,
            zIndex: 99,
            width: '100%',
            boxShadow: '0 1px 4px rgba(0,21,41,.08)'
          }}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{ fontSize: '16px', width: 64, height: 64 }}
            />
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <span style={{ fontWeight: 500 }}>{user.fullName} <span style={{ color: '#8c8c8c', fontWeight: 400 }}>({user.roles?.[0]?.replace('ROLE_', '')})</span></span>
              <Button type="primary" danger icon={<LogoutOutlined />} onClick={handleLogout}>
                Выход
              </Button>
            </div>
          </Header>
          <Content
            style={{
              margin: '24px 16px',
              padding: 24,
              minHeight: 280,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
};

export default MainLayout;
