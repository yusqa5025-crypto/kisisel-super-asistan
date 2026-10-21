import os
from flask import Flask, request, jsonify
from flask_cors import CORS
from groq import Groq

app = Flask(__name__)
CORS(app)
MODEL = os.getenv("GROQ_MODEL", "openai/gpt-oss-120b")

@app.get("/")
def health():
    return {"ok": True, "service": "Kisisel Super Asistan API"}

@app.post("/chat")
def chat():
    key = os.getenv("GROQ_API_KEY")
    if not key:
        return jsonify(error="Sunucuda GROQ_API_KEY ayarlanmamış."), 503
    data = request.get_json(silent=True) or {}
    messages = data.get("messages", [])
    if not isinstance(messages, list) or not messages:
        return jsonify(error="Mesaj bulunamadı."), 400
    safe = [{"role": "system", "content": "Türkçe konuşan, güvenli, kısa ve yardımcı bir kişisel asistansın. Kullanıcıya Türkçe cevap ver."}]
    for m in messages[-16:]:
        role = m.get("role")
        content = m.get("content")
        if role in ("user", "assistant") and isinstance(content, str):
            safe.append({"role": role, "content": content[:6000]})
    try:
        client = Groq(api_key=key)
        result = client.chat.completions.create(model=MODEL, messages=safe, temperature=0.6)
        return jsonify(reply=result.choices[0].message.content or "")
    except Exception as e:
        app.logger.exception("Groq isteği başarısız")
        return jsonify(error="AI isteği başarısız oldu. Sunucu loglarını kontrol et."), 502

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.getenv("PORT", "8080")))
