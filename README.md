# Insurance Pro

## Начало работы с проектом

### 1. Требования к системе
Для запуска проекта вам потребуются:
* **JDK 21**
* **Node.js 20+** и **npm**
* **Docker** и **Docker Compose**

### 2. Стек технологий
#### Бэкенд:
* **Java 21**
* **Spring Boot 3.5.9** (Data JPA, Security)
* **PostgreSQL 17**
* **Liquibase** (миграции БД)
* **JWT** (авторизация)
* **Maven** (сборка)

#### Фронтенд:
* **React 19**
* **Vite** (сборка)
* **Ant Design** (UI компоненты)
* **Axios** (HTTP-клиент)
* **React Router** (навигация)

### 3. Структура проекта
* `insurance-pro-backend` — серверная часть на Spring Boot (Java).
* `insurance-pro-frontend` — клиентская часть на React (Vite, Ant Design).
* `docker-compose.yml` — файл для контейнеризации всех сервисов.
* `init-db.sql` — начальная инициализация базы данных.

### 4. Запуск проекта через Docker Compose
Запуск всей системы (БД + Бэкенд + Фронтенд) одной командой:
```bash
docker-compose up -d
```
* Фронтенд: [http://localhost:9090](http://localhost:9090)
* Бэкенд API: [http://localhost:8080](http://localhost:8080)

### 5. Запуск проекта в IntelliJ IDEA (Разработка)
Для локальной разработки компоненты запускаются отдельно, но **база данных всегда должна быть запущена в Docker**.

#### Шаг 1: Запуск БД
```bash
docker-compose up -d insurance-pro-db
```
База данных PostgreSQL будет доступна на `localhost:25432`.

#### Шаг 2: Запуск бэкенда
1. Откройте проект в IDEA.
2. Перейдите в модуль `insurance-pro-backend`.
3. Запустите основной класс `InsuranceprobackendApplication`.

#### Шаг 3: Запуск фронтенда
1. В терминале перейдите в папку `insurance-pro-frontend`:
   ```bash
   cd insurance-pro-frontend
   ```
2. Установите зависимости и запустите:
   ```bash
   npm install
   npm run dev
   ```
3. Фронтенд будет доступен по адресу, указанному в консоли (обычно `http://localhost:5173`).

---

