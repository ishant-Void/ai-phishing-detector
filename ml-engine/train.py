import os, re, joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier

def extract_features(url: str):
    url_lower = url.lower()
    suspicious_kw = ['login', 'verify', 'update', 'secure', 'bank', 'account', 'free', 'signin', 'confirm']
    return [
        len(url),
        url.count('.'),
        url.count('-'),
        url.count('@'),
        1 if url_lower.startswith('https://') else 0,
        1 if re.search(r'\d+\.\d+\.\d+\.\d+', url_lower) else 0,
        sum(1 for kw in suspicious_kw if kw in url_lower),
        url.count('/')
    ]

if os.path.exists("phishing_urls.csv"):
    df = pd.read_csv("phishing_urls.csv")
else:
    data = [
        ("https://www.google.com", 0), ("https://github.com/ishant-Void", 0),
        ("https://www.amazon.in/dp/B09G9BL5CP", 0), ("https://www.microsoft.com/en-in", 0),
        ("https://stackoverflow.com/questions", 0), ("https://www.hdfcbank.com", 0),
        ("http://secure-login-update-account.xyz/auth", 1), ("http://192.168.1.45/bank/verify.php", 1),
        ("http://paypal-security-confirm@evil.com/signin", 1), ("http://free-gift-claim-now.tk/login", 1),
        ("http://hdfc-kyc-update-alert.info/verify", 1), ("http://amazon-account-suspended-login.net", 1)
    ] * 50
    df = pd.DataFrame(data, columns=["url", "label"])

X = [extract_features(u) for u in df["url"]]
y = df["label"]

model = RandomForestClassifier(n_estimators=100, random_state=42)
model.fit(X, y)
joblib.dump(model, "phishing_model.pkl")
print("Model trained & saved as phishing_model.pkl!")