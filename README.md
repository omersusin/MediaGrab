# MediaGrab

> A personal archiving tool for downloading media from social apps. On-device, no telemetry, fully open source.

## Features

- **8 platforms**: Instagram, TikTok, X/Twitter, Facebook, YouTube, Pinterest, Reddit, Telegram
- **3 access modes**: Accessibility, Shizuku, Root (or paste-URL fallback)
- **Material Design 3**: Light, Dark, AMOLED, Material You
- **Fully on-device**: No analytics, no cloud, no accounts
- **GPL-3.0**: Open source, audit-friendly

## Build

```bash
# Clone
git clone https://github.com/omersusin/MediaGrab.git
cd MediaGrab

# Initialize gradle wrapper
gradle wrapper

# Build
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

The project is configured for GitHub Actions CI. Every push builds a Debug + signed Release APK.

## Architecture

Multi-module Clean Architecture:
- `:app` — UI, navigation, DI graph
- `:core:designsystem` — Material 3 theme, components
- `:core:data` — Room, DataStore, repositories
- `:core:network` — OkHttp, interceptors
- `:core:extractor` — platform extractors + yt-dlp fallback
- `:core:common` — Logger, dispatchers, utils
- `:feature:overlay` — Accessibility service + floating button

## License

GPL-3.0. See [LICENSE](LICENSE).

## Disclaimer

This tool is for personal archiving. Respect copyright and platform ToS.
