#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-$PREFIX/lib/jvm/java-17-openjdk}"
export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
gradle --no-daemon assembleDebug
mkdir -p "$HOME/storage/downloads"
cp app/build/outputs/apk/debug/app-debug.apk "$HOME/storage/downloads/KisiselSuperAsistan.apk"
echo "APK: İndirilenler/KisiselSuperAsistan.apk"
