# CodeCash v2

CodeCash v2 is a comprehensive personal finance management application built for Android. It allows users to track their income and expenses, set budget goals, and visualize their financial health through interactive charts.

##Youtube Link:

## 🚀 Features

- **User Authentication:** Secure signup and login system.
- **Transaction Management:** Record daily income and expenses with descriptions and categories.
- **Budgeting:** Set minimum and maximum budget goals for different categories (e.g., Food, Transport, Rent).
- **Visual Analytics:** View spending patterns and budget progress using MPAndroidChart.
- **Photo Attachments:** Attach photos to transactions for digital receipt keeping.
- **Filtering:** View transactions for specific periods using date filters.
- **Performance:** Optimized data handling using parallel arrays, binary search for user lookups, and bubble sort for transaction history.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Android XML with ViewBinding and Material Design Components.
- **Architecture:** Local DataStore with object-oriented data models.
- **Libraries:**
  - `MPAndroidChart` for statistical visualization.
  - `Jetpack Core/AppCompat` for modern Android features.
  - `ConstraintLayout` for responsive UI design.

## 📱 Requirements

- **Minimum SDK:** API Level 24 (Android 7.0 Nougat)
- **Target SDK:** API Level 35
- **IDE:** Android Studio Ladybug or newer

## 🏗️ Project Structure

- `com.codecash.data`: Data models (`Transaction`, `BudgetGoal`, `User`) and the `DataStore` singleton.
- `com.codecash.utils`: Helper classes for navigation and image processing.
- `com.codecash.ui`: Activities and adapters for the user interface.

## 👥 Development Team

This project was developed by:
- Tshiamo Keefelakae Lentswe (st10448558)
- Yinhla Maringa (st10441743)
- Matshidiso Nthebe (st10449727)
- Mzamo Richmond Ndlovu (st10455453)

## 📄 License

This project is for educational purposes as part of the mobile development curriculum.
