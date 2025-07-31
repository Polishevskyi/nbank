# CI/CD Pipeline Setup 

## Опис

Налаштований CI/CD пайплайн для автоматичної збірки та деплою Docker-образу з автотестами в Docker Hub.

## Функціональність

- ✅ Запуск тільки при змінах в папці `src/`
- ✅ Збірка проекту Maven
- ✅ Перевірка стилю коду (Checkstyle)
- ✅ Запуск тестів
- ✅ Збірка Docker-образу з тегом commit hash
- ✅ Пуш в Docker Hub

## Налаштування

### 1. GitHub Secrets

Додайте наступні секрети в налаштуваннях репозиторію (Settings → Secrets and variables → Actions):

- `DOCKER_USERNAME` - ваш Docker Hub username
- `DOCKER_PASSWORD` - ваш Docker Hub password або access token

### 2. Docker Hub Access Token (рекомендовано)

1. Увійдіть в Docker Hub
2. Перейдіть в Account Settings → Security
3. Створіть New Access Token
4. Використовуйте токен замість пароля в `DOCKER_PASSWORD`

## Workflow

### Тригери

- Push в `main` або `develop` гілки з змінами в `src/`
- Pull Request в `main` або `develop` з змінами в `src/`

### Jobs

#### 1. build-and-test

- Збірка проекту: `mvn clean compile`
- Перевірка Checkstyle: `mvn checkstyle:check`
- Запуск тестів: `mvn test`

#### 2. build-and-push-docker (тільки для push)

- Збірка Docker-образу з тегом commit hash
- Пуш в Docker Hub з тегами:
  - `{username}/nbank-autotests:{commit-hash}`
  - `{username}/nbank-autotests:latest`

## Використання Docker-образу

```bash
# Запуск API тестів
docker run -e TEST_PROFILE=api {username}/nbank-autotests:{commit-hash}

# Запуск UI тестів
docker run -e TEST_PROFILE=ui {username}/nbank-autotests:{commit-hash}

# З кастомними URL
docker run -e TEST_PROFILE=api -e APIBASEURL=http://your-api-url {username}/nbank-autotests:{commit-hash}
```

## Checkstyle

Використовується Google Java Style Guide з наступними налаштуваннями:

- Максимальна довжина рядка: 100 символів
- Максимальна довжина методу: 150 рядків
- Максимальна кількість параметрів: 7
- Відступи: 4 пробіли

## Troubleshooting

### Checkstyle помилки

```bash
# Запуск Checkstyle локально
mvn checkstyle:check

# Генерація звіту
mvn checkstyle:checkstyle
```

### Docker build помилки

```bash
# Локальна збірка
docker build -t nbank-autotests .

# Запуск з логами
docker run nbank-autotests
```
