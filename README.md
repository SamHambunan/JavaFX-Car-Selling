<<<<<<< HEAD
# Java-Optional-Car
=======
# Car Selling Application

A JavaFX-based desktop application for buying and selling cars. Users can register, login, add/edit/delete their car listings, search for cars, view detailed car information, and save cars to a watchlist.

## Features

- **User Authentication**: Registration and login system with password hashing
- **Car Management**: Add, edit, delete, and view car listings
- **Image Upload**: Upload and display car images
- **Search Functionality**: Search cars by brand, model, year range, or price range
- **Public Homepage**: Display all available cars
- **Car Details Page**: View detailed information about each car
- **Watchlist**: Save cars to a personal watchlist

## Technology Stack

- **Java**: Core programming language
- **JavaFX**: UI framework
- **MySQL**: Database for data storage
- **JDBC**: Database connectivity
- **Maven**: Dependency management
- **BCrypt**: Password hashing

## Prerequisites

- Java 11 or higher
- MySQL Server 5.7 or higher
- Maven 3.6 or higher
- JavaFX SDK (included via Maven dependency)

## Setup Instructions

### 1. Database Setup

1. Start your MySQL server
2. Open MySQL command line or MySQL Workbench
3. Run the SQL schema file to create the database and tables:

```bash
mysql -u root -p < src/main/sql/schema.sql
```

Or manually execute the SQL commands from `src/main/sql/schema.sql`

### 2. Database Configuration

Edit the database connection settings in `src/main/java/com/carselling/database/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/car_selling_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

Update `DB_USER` and `DB_PASSWORD` to match your MySQL credentials.

### 3. Build the Project

Navigate to the project directory and build using Maven:

```bash
mvn clean compile
```

### 4. Run the Application

#### Option 1: Using Maven JavaFX Plugin

```bash
mvn javafx:run
```

#### Option 2: Using Java directly

First, compile and package:

```bash
mvn clean package
```

Then run:

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp target/classes:target/dependency/* com.carselling.Main
```

#### Option 3: Using an IDE

1. Import the project as a Maven project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Ensure JavaFX is properly configured
3. Run the `Main.java` class

## Project Structure

```
java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── carselling/
│   │   │           ├── Main.java                    # Application entry point
│   │   │           ├── models/                      # Data models
│   │   │           ├── database/                    # Database connection and DAOs
│   │   │           ├── controllers/                 # FXML controllers
│   │   │           ├── utils/                       # Utility classes
│   │   │           └── fxml/                        # FXML UI files
│   │   ├── resources/
│   │   │   ├── images/                              # Car images storage
│   │   │   └── styles.css                           # Application styles
│   │   └── sql/
│   │       └── schema.sql                           # Database schema
├── pom.xml                                           # Maven dependencies
└── README.md                                         # This file
```

## Usage

### Registration

1. Launch the application
2. Click "Register" on the login screen
3. Fill in username, email, and password
4. Click "Register" to create your account

### Adding a Car

1. Login to your account
2. Click "Add Car" button
3. Fill in car details (brand, model, year, price, etc.)
4. Optionally upload a car image
5. Click "Save"

### Searching for Cars

1. On the homepage, use the search filters:
   - Brand
   - Model
   - Year range (min - max)
   - Price range (min - max)
2. Click "Search" to filter results

### Viewing Car Details

1. Click on any car card or "View Details" button
2. View complete car information
3. If logged in, you can:
   - Add car to watchlist
   - Edit/Delete (if you own the car)

### Watchlist

1. Login to your account
2. Click "Watchlist" in the navigation bar
3. View all your saved cars
4. Remove cars from watchlist as needed

## Database Schema

### Users Table
- Stores user account information (username, email, hashed password)

### Cars Table
- Stores car listings with details (brand, model, year, price, mileage, color, description, image path)
- Linked to users via `user_id` foreign key

### Watchlist Table
- Stores user's saved cars
- Links users to cars via `user_id` and `car_id` foreign keys

## Troubleshooting

### Database Connection Issues

- Ensure MySQL server is running
- Verify database credentials in `DatabaseConnection.java`
- Check that the database `car_selling_db` exists
- Ensure MySQL connector dependency is properly downloaded

### JavaFX Issues

- Ensure JavaFX SDK is included in your classpath
- For Java 11+, JavaFX needs to be added as a module
- Check that all JavaFX dependencies are downloaded via Maven

### Image Upload Issues

- Ensure the `src/main/resources/images/` directory exists
- Check file permissions for the images directory
- Verify image file formats are supported (PNG, JPG, JPEG, GIF, BMP)

## Security Notes

- Passwords are hashed using BCrypt before storage
- SQL injection prevention using PreparedStatements
- Input validation on all forms
- Users can only edit/delete their own car listings

## Future Enhancements

- Admin panel for managing all listings
- Email notifications for watchlist updates
- Advanced filtering options
- Car comparison feature
- User profiles and ratings
- Messaging system between buyers and sellers

## License

This project is provided as-is for educational purposes.

## Support

For issues or questions, please check:
1. Database connection settings
2. MySQL server status
3. Java and Maven versions
4. JavaFX dependencies

>>>>>>> ef2d379 (initial commit)
