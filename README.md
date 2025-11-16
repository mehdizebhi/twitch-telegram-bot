# 🎮 Twinify (Twitch & Kick Telegram Bot)

A Telegram bot for sending live stream notifications from Twitch and Kick 
Built with **Java 21**, **Spring Boot**, and **SQLite**

## ✨ About the Project

This bot connects to the Twitch API and sends Telegram notifications when streamers go live.  
Currently, it supports **notifying users when followed streamers start streaming**, but more features are planned for the future.

## ⚙️ Technologies Used

- Java 21
- Spring Boot 3.3.4
- Spring WebFlux
- Spring Data JPA
- [Twitch4J](https://github.com/twitch4j/twitch4j) – Twitch API integration
- [TelegramBots](https://github.com/rubenlagus/TelegramBots) – Telegram Java library
- SQLite (with Hibernate dialect)
- Docker

## 🐳 Running with Docker

To build and run the bot using Docker:

```bash
docker build -t twinify .
docker run -d \
  -e TELEGRAM_BOT_TOKEN=<your-telegram-bot-token> \
  -e TELEGRAM_BOT_USERNAME=<your-telegram-bot-username>
  -e TWITCH_CLIENT_ID=<your-twitch-client-id> \
  -e TWITCH_CLIENT_SECRET=<your-twitch-client-secret> \
  twinify
```

Replace the environment variables with your actual credentials.

## 🚀 Planned Features

- Support for tracking multiple streamers per user
- Notifications for other events (e.g., stream ended)
- Streamer stats and summaries
- Telegram UI enhancements (buttons, menus, etc.)

## 📦 Installation (Non-Docker)

If you prefer to run the bot locally without Docker:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-DTELEGRAM_BOT_TOKEN=<your-token> -DTELEGRAM_BOT_USERNAME=<your-bot-username> -DTWITCH_CLIENT_ID=<your-id> -DTWITCH_CLIENT_SECRET=<your-secret>"
```

