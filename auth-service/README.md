# AuthService

**AuthService** — микросервис для аутентификации, управления пользователями и их профилями, а также работы с документами в рамках системы Armenia Bank.

---

## 📌 Описание

Этот микросервис предоставляет REST API для:

- Регистрации и аутентификации пользователей
- Обновления профиля пользователя
- Загрузки и получения документов
- Удаления пользователей
- Взаимодействия с Keycloak для OAuth2
- Отправки событий в Kafka

Сервис разработан для обеспечения безопасного управления пользователями и интеграции с другими компонентами системы **Armenia Bank**.

---

## 🚀 Технологии

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Lombok
- MapStruct
- Maven
- Keycloak (OAuth2)
- Kafka
- Docker (опционально)

---

## 📋 Требования

Для работы с проектом необходимо установить:

- Java 17 или выше
- Maven 3.8+
- PostgreSQL 13+
- Keycloak 20.0.0+
- Kafka 3.3.1+
- Docker (опционально, для контейнеризации)

---

## ⚙️ Установка и запуск (через Docker Compose)

Для удобного запуска всех сервисов (PostgreSQL, Redis, Kafka, Zookeeper, Keycloak, AuthService и др.) используется `docker-compose`.

### Шаги:

1. Клонируйте репозиторий и перейдите в каталог с инфраструктурой:

```bash
git clone https://github.com/kirakosyan-david/Armenia_Bank.git
cd Armenia_Bank/infra

