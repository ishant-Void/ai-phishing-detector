from fastapi import FastAPI
from pydantic import BaseModel, ConfigDict
import joblib
from train import extract_features

app = FastAPI()
model = joblib.load("phishing_model.pkl")

class URLRequest(BaseModel):
    model_config = ConfigDict(extra="ignore")
    url: str

@app.post("/predict")
def predict(req: URLRequest):
    feats = extract_features(req.url)
    prob = model.predict_proba([feats])[0][1]
    is_phishing = bool(prob >= 0.5)
    reasons = []
    if feats[5] == 1: reasons.append("Raw IP address used in URL")
    if feats[3] > 0: reasons.append("Contains '@' symbol redirection")
    if feats[4] == 0: reasons.append("Unencrypted HTTP connection")
    if feats[6] > 0: reasons.append("Suspicious phishing keywords detected")
    if feats[0] > 65: reasons.append("Abnormally long URL")
    if not reasons:
        reasons.append("Clean URL structure" if not is_phishing else "Anomalous lexical pattern")

    return {
        "url": req.url,
        "isPhishing": is_phishing,
        "riskScore": round(float(prob) * 100, 2),
        "reasons": reasons
    }