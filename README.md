# Kişisel Süper Asistan

## Android APK (GitHub Actions)
1. Bu klasörü ayrı bir GitHub deposuna (`kisisel-super-asistan`) yükle.
2. Actions sekmesinde `Android APK` iş akışını çalıştır veya `main` dalına push et.
3. Çıktıdaki `KisiselSuperAsistan-APK` artifact'ini indirip ZIP'ten çıkar.

## Groq backend
APK, Groq API anahtarını içermez. `backend/` klasörünü Render gibi bir Python web servisine ayrı olarak dağıt:
- Build/install komutu: `pip install -r requirements.txt`
- Start komutu: `gunicorn app:app --bind 0.0.0.0:$PORT`
- Environment variable: `GROQ_API_KEY` = Groq API anahtarın
- İsteğe bağlı `GROQ_MODEL` = hesabında erişimin olan model adı
Sonra APK içindeki Ayarlar bölümüne backend'in HTTPS adresini yaz.

Önce backend'i dağıtmadan da görevler ve notlar yerel olarak çalışır. Gerçek hatırlatıcı bildirimleri bu başlangıç sürümünde henüz ekli değil.
