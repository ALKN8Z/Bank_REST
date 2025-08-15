# Bank REST Application

## **Описание проекта**

Bank REST Application – это сервис для управления банковскими картами и переводами между ними. Приложение позволяет:

- Создавать, просматривать и обновлять банковские карты;
- Выполнять переводы между картами;
- Управлять пользователями (создание, обновление, удаление, просмотр);
- Вести аудит операций и отслеживать их статус.

**Технологии:**

- Java 17, Spring Boot
- PostgreSQL
- Maven
- Docker & Docker Compose
- Swagger/OpenAPI для документации API

---

## **Быстрый старт с Docker**

1. Клонировать репозиторий:

```bash
  git clone https://github.com/ALKN8Z/Bank_REST.git
```

2. Запустить приложение и базу данных через Docker Compose:

```bash
docker-compose up --build
```

3. Приложение будет доступно по адресу:

- Основной сервис: [http://localhost:8080](http://localhost:8080)
- Swagger UI (документация API): [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

4. Остановка контейнеров:

```bash
docker-compose down
```

> Для удаления тома с данными базы:

```bash
docker-compose down -v
```

---

## **API**

Документация доступна через Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- Пользователи: `/api/users`
- Карты: `/api/cards`
- Переводы: `/api/transfers`
- Авторизация: `/api/auth`

---

## **Примечания**

- Все операции с базой данных и приложением происходят в Docker-контейнерах, поэтому локальная база не требуется.
- В проекте используется встроенная поддержка OpenAPI для генерации документации.

