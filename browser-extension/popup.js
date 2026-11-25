document.addEventListener("DOMContentLoaded", () => {
  const urlInput = document.getElementById("urlInput");
  const scanBtn = document.getElementById("scanBtn");
  const resultBox = document.getElementById("result");
  const statusTitle = document.getElementById("statusTitle");
  const metaInfo = document.getElementById("metaInfo");
  const reasonsList = document.getElementById("reasonsList");

  // Auto-fill active browser tab URL
  chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
    if (tabs[0] && tabs[0].url) {
      urlInput.value = tabs[0].url;
    }
  });

  scanBtn.addEventListener("click", async () => {
    const targetUrl = urlInput.value.trim();
    if (!targetUrl) return;

    scanBtn.disabled = true;
    scanBtn.textContent = "Scanning...";
    resultBox.style.display = "none";

    try {
      const response = await fetch("http://localhost:8080/api/v1/scan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ url: targetUrl })
      });

      if (!response.ok) throw new Error("Backend server error");
      const data = await response.json();

      resultBox.className = data.isPhishing ? "danger" : "safe";
      statusTitle.textContent = data.isPhishing
        ? "🚨 PHISHING DETECTED"
        : "✅ SAFE WEBSITE";

      metaInfo.textContent = `Risk Score: ${data.riskScore}% | Source: ${
        data.cached ? "MongoDB Cache" : "Live AI Model"
      }`;

      reasonsList.innerHTML = "";
      (data.reasons || []).forEach((reason) => {
        const li = document.createElement("li");
        li.textContent = reason;
        reasonsList.appendChild(li);
      });

      resultBox.style.display = "block";
    } catch (err) {
      resultBox.className = "danger";
      statusTitle.textContent = "⚠️ Connection Error";
      metaInfo.textContent = "Ensure Spring Boot (:8080) & Python (:8000) are running.";
      reasonsList.innerHTML = "";
      resultBox.style.display = "block";
    } finally {
      scanBtn.disabled = false;
      scanBtn.textContent = "Scan Website";
    }
  });
});