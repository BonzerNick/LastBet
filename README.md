# Последняя Ставка # LastBet 🎲

**"Последняя Ставка"** — это онлайн-казино с ироничным подходом к названию, подчеркивающее реалистичные шансы на выигрыш, предлагая при этом качественный и увлекательный игровой опыт. Проект предоставляет пользователям возможность играть в такие популярные азартные игры, как рулетка, блэкджек и слоты, а также поддерживает внесение депозитов и вывод средств удобными способами.

![Johnny Dodepp](images/dodepp.jpg)

## 🚀 О приложении

Проект разработан на основе **Spring Boot** и является масштабируемым и высокопроизводительным приложением, позволяющим пользователям взаимодействовать с игровыми сервисами через API.

Поддерживается:
- Просмотр списка игр и их параметров.
- Реализация честной игры через генератор случайных чисел (**RNG**).
- Пополнение счета и вывод выигрышей с использованием удобных платежных систем.
- Безопасная авторизация с поддержкой 2FA.
- Многоязычный интерфейс.
- Автоматизация Workflow (CI/CD) через GitHub Actions.


### Основные особенности:
1. Поддерживаемые игры: рулетка, ракетка, слоты.
2. Поддержка различных платежных систем (банковские карты, крипто-кошельки, электронные кошельки).
3. Высокий уровень безопасности благодаря двухфакторной аутентификации (2FA).
4. Локализация интерфейса (русский, английский, немецкий).

---

## 📦 Основные технологии и стек

- **Backend**: Spring Boot, Spring Data JPA, Spring Security.
- **База данных**: PostgreSQL (с Liquibase для миграций).
- **Документирование API**: OpenAPI (Swagger UI).
- **Авторизация**: JWT, 2FA.
- **CI/CD**: GitHub Actions (сборка, тесты).
- **Тестирование**: JUnit, Mockito, AssertJ.
- **Контейнеризация**: Docker / Docker Compose.

---

## 📂 Структура проекта
```plaintext
LastBet
├── .github/
│   └── workflows/               # Конфигурации для CI/CD (GitHub Actions)
│       └── pipeline.yml         # Пайплайн CI/CD
├── .idea/                       # Конфигурационные файлы для IntelliJ IDEA
├── src/
│    ├── main/
│    │    ├── java/com/example/casino/
│    │    │    ├── config/       # Конфигурационные файлы (Dotenv, OpenAPI)
│    │    │    ├── controller/   # REST-контроллеры (например, UserController)
│    │    │    ├── dto/          # Data Transfer Objects (DTO)
│    │    │    ├── model/        # Сущности данных (JPA Entities)
│    │    │    ├── repository/   # Data Access Layer, репозитории (Spring Data JPA)
│    │    │    ├── service/      # Бизнес-логика и основные сервисы
│    │    │    └── CasinoApplication.java  # Главный класс запуска Spring Boot приложения
│    │    ├── resources/
│    │    │    ├── db.changelog/           # Файлы миграции базы данных для Liquibase
│    │    │    ├── application.yml         # Основной конфигурационный файл Spring Boot
│    │    │    └── liquibase.properties    # Конфигурация Liquibase
│    ├── test/
│    │    ├── java/com/example/casino/
│    │    │    ├── service/
│    │    │    │    └── UserServiceTest    # Юнит-тестирование сервисов
│    │    └── resources/
│    │         └── application-test.yml    # Настройки тестовой среды
├── target/                      # Скомпилированные файлы и артефакты проекта (после сборки Maven)
├── .env                         # Файл с переменными окружения (credentials для базы данных)
├── .gitignore                   # Список файлов и папок, исключённых из репозитория
├── database_availability_check.sh # Скрипт проверки доступности базы данных
├── docker-compose.yml           # Конфигурация Docker для запуска сервисов
├── pom.xml                      # Конфигурация Maven, зависимости проекта
├── README.md                    # Документация проекта
```
---

## 🔧 Установка и настройка

### 1. Склонируйте проект:
```bash
git clone https://github.com/BonzerNick/LastBet.git
cd LastBet
```

### 2. Запустите базу данных через Docker:
Убедитесь, что установлен Docker. Выполните команду:
```bash
docker-compose up -d
```

### 3. Настройте конфигурацию:
Создайте файл `.env` в корне проекта и добавьте значения:
```env
POSTGRES_USER=casino_user
POSTGRES_PASSWORD=casino_pass
POSTGRES_DB=casino_db
```

### 4. Соберите и запустите приложение:
```bash
mvn clean install
mvn spring-boot:run
```

### 5. Просмотрите API-документацию:
После запуска приложения документация доступна по адресу:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📖 Основной функционал

### Просмотр игр:
- Доступен список игр с описаниями, минимальными и максимальными размерами ставок.

### Игровой процесс:
- Все игровые механики основаны на генераторе случайных чисел (RNG) для гарантии честности.

### Пополнение счета:
- Поддерживается внесение средств через популярные платежные системы (банковские карты, электронные и криптовалютные кошельки).

### Вывод средств:
- Пользователи могут выводить средства на подтвержденные платежные инструменты.

### Личный кабинет:
- Регистрация и авторизация через email.
- Двухфакторная аутентификация для повышения безопасности.
- Просмотр истории игр и финансовых операций.

---

## ⚙️ Тестирование

Для запуска тестов выполните:
```bash
mvn test
```

Основные тесты расположены в директории:  
`src/test/java/com.example.casino`

---

## 🗂 Миграции базы данных

Миграции управляются с помощью **Liquibase**. Они описаны в файле:
- `src/main/resources/db.changelog-master.xml`

Для применения миграций используйте:
```bash
mvn liquibase:update
```

---

## 🧩 CI/CD Workflow

Проект использует GitHub Actions для автоматизации разработки:

1. Сборка проекта.
2. Запуск юнит-тестов.
3. Проверка качества кода (можно подключить SonarQube).

Пример конфигурации находится в файле `.github/workflows/pipeline.yml`.

---

## 🌍 Локализация и многоязычность

Приложение поддерживает русский, английский и немецкий языки. При запуске язык устанавливается автоматически на основе региона клиента.

---

## 📜 Лицензия

Да, её нет

---

## 👥 Авторы

- [BonzerNick](https://github.com/BonzerNick) — Разработчик.