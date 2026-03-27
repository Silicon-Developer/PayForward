# 🚀 PayForward Pro

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Security](https://img.shields.io/badge/Security-Local_Processing-success?style=for-the-badge)

**PayForward Pro** is an advanced, secure, and automated Android application designed to intercept financial notifications and SMS messages (UPI, PhonePe, Paytm, etc.) and forward them to customized endpoints like Webhooks or Telegram Bots in real-time.

Built with a premium "Anti-Gravity" UI and an emphasis on absolute privacy, this app processes all financial data 100% locally on the user's device without relying on third-party intermediary servers.

---

## ✨ Core Features

* **🧠 Smart Regex Data Extraction:** Doesn't just forward raw text. The app intelligently parses incoming messages to extract the `amount`, `sender_name`, and `payment_mode`, compiling them into clean, structured JSON payloads.
* **🛡️ Anti-Spoofing Filter:** Protects against fake payment scams by verifying the 6-character TRAI Sender ID (e.g., `VK-PAYTM`, `AD-HDFCBK`). Messages from generic 10-digit numbers are automatically blocked.
* **🔀 Dynamic Multi-Routing:** Forward your intercepted data exactly where you need it:
    * **Webhook Endpoint:** Send POST requests with JSON payloads to your own server.
    * **Telegram Bot API:** Get instant alerts directly in your Telegram chat.
    * **SMS Forwarder:** Bounce the message to another mobile number.
* **🔋 24/7 Background Persistence:** Engineered with a Foreground Service and automated battery-optimization bypass to ensure the listener never sleeps, even when swiped away.
* **🎨 Premium 'Anti-Gravity' UI:** Built entirely with Jetpack Compose featuring a custom, glowing dark/light mode theme, interactive data-flow animations, and a sleek side-navigation drawer.
* **🔒 Privacy First:** All API keys, bot tokens, and webhook URLs are encrypted and stored locally using Android's secure `EncryptedSharedPreferences`. 

---

## 📸 Screenshots

| Dashboard (Anti-Gravity UI) | Keyword Engine | Security Hub |
| :---: | :---: | :---: |
| <img src="https://i.rj1.dev/tNZgh.png" width="250"/> | <img src="https://i.rj1.dev/uHQRz.png" width="250"/> | <img src="https://i.rj1.dev/JSvaJ.png" width="250"/> |

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Asynchrony:** Kotlin Coroutines & Flow (`Dispatchers.IO` for seamless network calls)
* **Background Work:** Foreground Services & Broadcast Receivers (`READ_SMS`, `BIND_NOTIFICATION_LISTENER_SERVICE`)
* **Security:** AndroidX Security Crypto

---

## 🚀 Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/silicon-developer/PayForward.git](https://github.com/silicon-developer/PayForward.git)
    ```
2.  **Open in Android Studio:**
    Open the project in Android Studio (Iguana or newer recommended).
3.  **Sync & Build:**
    Sync the Gradle files to download the required dependencies.
4.  **Run:**
    Deploy the app to a physical Android device. *(Note: SMS and Notification interception features cannot be fully tested on an emulator).*

### ⚠️ Important Permissions Note
Upon first launch, you must grant the app the following permissions for it to function:
* `Read SMS` & `Receive SMS`
* `Notification Access` (via System Settings)
* `Ignore Battery Optimizations` (Crucial for uninterrupted background monitoring)

---

## 💡 Sample Webhook Payload

When the app intercepts a valid payment, it forwards a clean JSON object to your configured endpoint:

```json
{
  "timestamp": "2024-03-27T10:30:00Z",
  "gateway": "PhonePe",
  "amount": "1500.00",
  "sender": "Rahul Kumar",
  "raw_message": "Rxvd Rs. 1500.00 from Rahul Kumar via PhonePe. UPI Ref: 1234567890"
}

### 👨‍💻 Developed By

# Silicon Developer

< \ > I developed this app for payment verification directly through the user's phone, empowering businesses to verify transactions automatically without paying hefty fees to third-party services.

# 📫 Connect with me:

Telegram: t.me/Silicon_Official

GitHub: github.com/Silicon-Developer

# Made with ❤️ for the community.
