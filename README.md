# 🚗 Car Selling Application

A robust JavaFX desktop application for buying and selling cars. This application features a self-initializing database system, secure authentication, and a responsive user interface.

## ✨ Key Features

* **⚡ Auto-Database Setup:** The application automatically creates all necessary tables and indexes upon the first launch. No manual SQL scripts required!

* **⚙️ External Configuration:** Easily configure database credentials using a simple text file (`db_config.properties`) without touching the code.

* **🔒 User Authentication:** Secure registration and login system with BCrypt password hashing.

* **🚙 Car Management:** Add, edit, delete, and view car listings with image support.

* **🔍 Search & Filter:** Advanced filtering by brand, model, year, and price.

* **⭐ Watchlist:** Save favorite cars to a personal watchlist.

## 🛠️ Technology Stack

* **Language:** Java 11+

* **UI Framework:** JavaFX

* **Database:** MySQL (8.0 Recommended)

* **Build Tool:** Maven

* **Security:** BCrypt

## 🚀 Quick Start Guide

### Prerequisites

1. **Java 11 or higher** installed on your machine.

2. **MySQL Server** installed and running.

### 1. Clone the Repository

Open your terminal or command prompt and run:

```bash
git clone https://github.com/SamHambunan/Java-Optional-Car.git
cd Java-Optional-Car
```

### Step 2: Create the Database

Open your MySQL Workbench or Terminal and run this single command to create the empty container:

```sql
CREATE DATABASE car_selling_db;
```
Note: You do NOT need to create tables. The app does this for you automatically.

### Step 3: Configure the App
Create a file named `db_config.properties` in the same folder as the application JAR file (or use the one in `src/main/resources`).

Paste the following content into it and update with your MySQL password:
```sql
db.url=jdbc:mysql://localhost:3306/car_selling_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=YOUR_MYSQL_USERNAME_HERE
db.password=YOUR_MYSQL_PASSWORD_HERE
```
### Step 3: Run the Application
You can run the application directly from the terminal with this command:

#### NOTE: Make sure you have the directory on the root of the source code
```
java -jar target/car-selling-app-1.0.0.jar
```
<i>Or simply double click `run.bat` script</i>

## 📂 Project Structure
```bash
java/
├── src/
│   ├── main/
│   │   ├── java/com/carselling/
│   │   │   ├── database/         # Auto-initialization & Connection logic
│   │   │   ├── utils/            # ConfigLoader for reading properties
│   │   │   ├── controllers/      # JavaFX Controllers
│   │   │   ├── models/           # Data Models (User, Car)
│   │   │   └── Main.java         # Entry Point
│   │   ├── resources/
│   │   │   ├── fxml/             # UI Views
│   │   │   ├── images/           # Default images
│   │   │   └── db_config.properties # Default configuration
├── target/                       # Compiled JAR files
├── pom.xml                       # Maven Dependencies & Shade Plugin
└── README.md                     # Documentation
```

## ❓ Troubleshooting
#### "Connection Failed" or App Crashes on Start

* Check that your MySQL server is running.

* Ensure `db_config.properties` exists and has the correct password.

* Make sure you created the database car_selling_db.

#### "JavaFX runtime components are missing"

* Ensure you are running the Shaded JAR (the one created by mvn package), not the original small JAR.

* If running from IDE, ensure your JavaFX SDK path is configured correctly.

#### Images not showing?

* Ensure the application has write permissions to the images/ folder in the working directory.

## 📄 License
This project is provided as-is for educational purposes.

