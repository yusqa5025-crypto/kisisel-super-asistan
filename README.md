# Kişisel Süper Asistan

Türkçe Android asistanı: AI sohbeti, yerel sohbet geçmişi, notlar, görevler ve Google hesabı giriş altyapısı.

## APK derleme
GitHub Actions sekmesindeki Android APK iş akışını çalıştır. `KisiselSuperAsistan-APK` artifact dosyasını indir.

## Groq API
API anahtarını APK içine koyma. `backend/` klasörünü Python web servisi olarak dağıt:
- Kurulum: `pip install -r requirements.txt`
- Başlatma: `gunicorn app:app --bind 0.0.0.0:$PORT`
- Ortam değişkeni: `GROQ_API_KEY`
- İsteğe bağlı: `GROQ_MODEL`

Ardından uygulamada Ayarlar > AI bağlantısı bölümüne sunucunun HTTPS adresini gir.

## Google hesabıyla giriş
Google Cloud Console'da bir OAuth 2.0 Web Client ID oluştur ve
`app/src/main/res/values/strings.xml` dosyasındaki `default_web_client_id`
değerine yaz. Google OAuth ayarları tamamlanmadan Google girişi çalışmaz.

## Gizlilik
Notlar, görevler ve sohbet geçmişi cihazın yerel depolamasında tutulur.
Groq anahtarı Android APK'ya eklenmez.
