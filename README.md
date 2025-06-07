# Transfer App

Это простое приложение с frontend и backend, запускаемое через `docker-compose`.


### Шаг 1. Собрать образ frontend

Dockerfile для frontend:
```dockerfile
# Берем официальный образ Node.js с Alpine Linux (маленький и быстрый)
FROM node:14-alpine

# Создаем рабочую директорию /app внутри контейнера
WORKDIR /app

# Копируем ТОЛЬКО файлы зависимостей (package.json и package-lock.json)
COPY package*.json ./

# Устанавливаем зависимости (ci — для точного соответствия версий)
RUN npm ci

# Копируем ВЕСЬ остальной код проекта в /app
COPY . .

# Собираем фронтенд-приложение (эта команда упаковывает код в статические файлы)
RUN npm run build

# Указываем, что контейнер будет слушать порт 3000
EXPOSE 3000

# Команда запуска сервера при старте контейнера
CMD ["npm", "start"]
```

Создаем образ для frontend:

```bash
docker build -t transfer-frontend .
```


### Шаг 2. Собрать образ frontend

[Dockerfile](Dockerfile)

Создаем образ для backend:

```bash
docker build -t transfer-backend .
```

### Шаг 3. Запусить приложение

[docker-compose.yaml](docker-compose.yaml)

Запускаем приложение:

```bash
docker-compose up
```

## Логирование

Логирование приложения, Request/Response в файл - [application.log](./logs/application.log)

Логирование переводов в файл - [transfer.log](./logs/transfer.log)