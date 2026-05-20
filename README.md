# Daily Expenditure Tracker 💰

A modern, clean, and intuitive Android application built using **Jetpack Compose** and **Clean Architecture** to help users track their daily expenses, monitor monthly budgets, and analyze spending habits seamlessly.

---

## 📸 App Screenshots & UI Flow

Here is a visual breakdown of the application flow from onboarding to advanced expense analytics:

### 🚀 Onboarding & Setup
| 1. Intro Screen | 2. Name Setup |
| :---: | :---: |
| <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189308/Screenshot_20260518_005605_Daily_Expenditure_colajb.jpg" width="280"> | <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189319/Screenshot_20260518_005616_Daily_Expenditure_wt0zlk.jpg" width="280"> |

### 📊 Dashboard & Analytics
| 3. Dashboard Screen | 4. Analysis Screen |
| :---: | :---: |
| <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189321/Screenshot_20260519_143908_Daily_Expenditure_me2bsg.jpg" width="280"> | <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189347/Screenshot_20260519_143916_Daily_Expenditure_a4qnpn.jpg" width="280"> |

### 📄 Export & PDF Reports
| 5. Download Report | 6. PDF Document View |
| :---: | :---: |
| <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189329/Screenshot_20260519_143930_Daily_Expenditure_fwdpas.jpg" width="280"> | <img src="https://res.cloudinary.com/dxbpzj3mh/image/upload/v1779189345/Screenshot_20260519_144027_Drive_g4kr9z.jpg" width="280"> |

---

## ✨ Features

- **Personalized Dashboard:** Welcomes the user with a dynamic greeting and displays an overview of their financial health.
- **Dual Expense Summary Card:** High-fidelity gradient card showing a side-by-side comparison of **This Month's Total Spend** versus **Today's Expenses**.
- **Visual Analytics:** Interactive charts (PieChart/BarChart) and progress indicators leveraging specialized data modeling (`CategoryShare`) for easy spending breakdown.
- **Dynamic Content Updates:** Instant state management using Kotlin Coroutines and Flows (`StateFlow`) syncing UI dynamically with repository deletions and additions.
- **Persistent Preferences:** Secure and lightweight local storage to maintain user data and customized preferences throughout the app session.
- **Data Portability & PDF Export:** Custom Android `Canvas` implementation to generate highly polished expense sheets complete with category bar charts and professional faded watermarks.

---

## 🛠️ Tech Stack & Tools

- **UI Framework:** 100% Native [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern declarative UI rendering.
- **Language:** [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow, Advanced Collection Filtering).
- **Design System:** Material Design 3 (M3) components featuring dynamic colors, gradient brushes, shapes, and responsive typography.
- **Architecture Pattern:** MVVM (Model-View-ViewModel) paired with clean package separation (Data, Domain, Presentation).

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps:

1. Clone the repository:
   ```bash
   git clone [https://github.com/codingindia87/DailyExpenditure.git](https://github.com/codingindia87/DailyExpenditure.git)
