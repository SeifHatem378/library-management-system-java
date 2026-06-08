# Advanced Library Management System

A robust desktop-based backend application designed to streamline library operations, manage inventories, and track borrowing transactions efficiently.

## Features
- **User Management**: Authentication and role-based tracking for admins and regular users.
- **Book Inventory**: Add, view, and track books with dynamic availability counting.
- **Borrowing System**: Process loan transactions with secure relational integrity and status tracking ('borrowed' / 'returned').
- **Automatic Date Handling**: Generates borrow and return timestamps automatically via the database.

## Tech Stack
- **Language**: Java
- **Database**: Microsoft SQL Server
- **Driver**: Microsoft JDBC Driver for SQL Server (v13.4)

## Database Schema
The project utilizes a highly normalized relational structure consisting of 3 interconnected tables:
1. **users**: Tracks member credentials and access roles ('admin' / 'user').
2. **books**: Manages titles, authors, categories, and real-time stock availability.
3. **borrowings**: Acts as a junction table linking users and books with foreign keys to log active and completed rentals.

## Setup and Execution
1. Execute the provided SQL script in SQL Server Management Studio (SSMS) to initialize `library_db`.
2. Add the `mssql-jdbc` JAR file to your project's reference libraries.
3. Compile and execute `Main.java` from your preferred IDE.
