# Kharchaaa v3.0 — Har rupaya, hisaab ke saath.

Kharchaaa expense tracker ka naya version. Original app **Muhammad Abdullah Khalid (ByteBuddy Devs)** ki banayi hui hai — credit unhi ka hai. Ye v3.0 ek fresh, standalone rebuild hai (kuch code copy nahi kiya gaya).

## Kya naya hai (v3.0)

- **Dashboard** — Kul Balance / Kul Aya / Kul Gaya / Lists cards, Aya vs Gaya bar chart (aakhri 6 mahine, canvas), is mahine ke kharchay ka category donut, aur Insights (sab se zyada kharcha kis category mein, is mahina vs pichla mahina).
- **Budgets** — har list ka budget + progress bar (% used; 80%+ amber, 100%+ red aur warning).
- **Categories** — Khana, Kiraya, Bijli/Gas, Transport, Shopping, Sehat, Taleem, Dukaan, Salary, Other — har entry ki category + icon.
- **Entry edit** — entries ko edit ya delete kiya ja sakta hai; har entry ki date hoti hai.
- **History** — text search + List / Category / Mahina / Aya-Gaya filters; PDF, JSON export/import aur **CSV export**.
- **PIN lock** — optional 4-digit PIN; lagao to app khulte hi PIN maangegi.
- **Naya theme — Emerald & Gold (default)** — gehra emerald green (#064E3B / #065F46) + gold (#D4AF37). Theme picker mein 4 themes: Emerald & Gold, Royal Navy & Gold, Midnight Violet, Light.
- **Refresh animation** — Parchi / Tarazu / Band.
- **Migration** — purani v2.1.0 web app ka data (`kharchaaa-data-v2`) pehli dafa khud-ba-khud import ho jata hai (budget 0 = no budget set, categories type ke hisaab se).
- **Balance hide/show** (eye button), dark/light nahi — theme picker se 4 themes.

## Original APK (v1.9) se li gayi cheezein

Lists (naam + rang + quick chips Ghar/Personal/Salary), Aya/Gaya entries, balance hide/show, History filters, PDF download, monthly totals, pull-refresh animation (Parchi/Tarazu), delete with confirm.

## Chalayein

Koi build step nahi — static site hai:

```bash
python3 -m http.server
# phir browser mein http://localhost:8000 kholein
```

Ya Vercel par deploy karein: repo root mein `index.html` hai, framework "Other", no build command.

## Data

Saara data browser ki `localStorage` mein save hota hai (`kharchaaa-data-v3` key). Backup ke liye History tab se JSON export karein.

## Credit

- Developer: **Muhammad Abdullah Khalid**
- Team: ByteBuddy Devs
- Support: abdullahkhaliduk7@gmail.com
- Version: 3.0
