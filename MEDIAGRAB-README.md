# MediaGrab — How to ship it

This file is for **you** (the repo owner). It explains how to push this source to GitHub,
get an APK, and install it.

## 1. Push the source

You have three files from the assistant:

- `MediaGrab-complete.zip` — the full, ready-to-build source tree
- `upload_to_repo.sh` — one script that unpacks + commits + force-pushes to `main`
- `MEDIAGRAB-README.md` — this file

In **Termux** (or any Linux/macOS shell with git):

```bash
bash upload_to_repo.sh MediaGrab-complete.zip
```

What it does:
1. Unzips into `~/MediaGrab/` (keeps your existing `.git/`).
2. `git add -A`, commits, and `git push -f origin main`.
3. Prints the Actions URL to watch the build.

> First time? The script will `git init` and set the remote to
> `https://github.com/omersusin/MediaGrab` if no repo exists yet.
> Override with env vars, e.g. `REPO_DIR=~/foo REMOTE_URL=git@github.com:me/x.git bash upload_to_repo.sh app.zip`.

## 2. Get the APK

1. Open <https://github.com/omersusin/MediaGrab/actions>
2. Wait ~2-5 min for **MediaGrab CI** to finish (first run is slower).
3. Open the run → **Artifacts** → download **MediaGrab-debug**.
4. Unzip it → install `app-debug.apk` on your phone.
   (Enable "Install unknown apps" for your file manager / browser.)

## 3. (Optional) Signed release builds

Add these repository **Secrets** (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | `base64 -w0 my-release-key.jks` output |
| `KEY_ALIAS` | your key alias |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_PASSWORD` | key password |

Create a keystore once:

```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA \
  -keysize 2048 -validity 10000 -alias mediagrab
base64 -w0 my-release-key.jks   # paste into KEYSTORE_BASE64
```

With secrets set, the workflow also produces **MediaGrab-release-signed**.

## 4. Using the app

- **Paste tab** → paste a link → pick **quality/format** (Best / 1080p / 720p / 480p /
  Audio M4A / Audio MP3) → **Download**. Progress shows on the Downloads tab + a notification.
- **Share** a link from any app → pick **MediaGrab** → Download (or Audio only).
- **Floating bubble**: Settings → enable Accessibility + "Display over other apps".
  A small bubble docks to the screen edge and goes semi-transparent. While browsing:
  - On apps that show the link on screen (X/Twitter, Reddit…) tap the bubble → it downloads.
  - On Instagram/TikTok (which hide the URL): tap the post's **••• → Copy link** first,
    then tap the bubble → MediaGrab opens and downloads the copied link.
- **Keep the engine fresh**: Settings → Download engine → **Update engine (yt-dlp)**.
  The app also auto-updates yt-dlp on launch so site changes keep working.
- Files land in **Download/MediaGrab/**.

## Notes

- The first download initializes the yt-dlp/python runtime — it can take a few extra
  seconds once, then it's fast.
- YouTube and most video sites download full video+audio merged with ffmpeg at the chosen quality.
- Some platforms change frequently; if one fails, the app automatically falls back to a
  metadata scraper, which still grabs thumbnails/images.

## License

GPL-3.0. Personal use. Respect platform ToS and copyright.
