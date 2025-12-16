🚆 Railway Ticket Booking System (Java + MySQL)
📌 Project Description

The Railway Ticket Booking System is a console-based Java application developed to manage train details and railway ticket booking operations.
This system allows users to view available trains, book tickets, cancel tickets, and view booked tickets using a MySQL database for data storage.

The project demonstrates the use of Core Java, JDBC, and MySQL in building a real-world database-driven application.

🎯 Objectives

To understand Java database connectivity (JDBC)

To perform CRUD operations using MySQL

To implement transaction management in Java

To build a simple menu-driven application

🛠️ Technologies Used

Programming Language: Java

Database: MySQL

Concepts Used:

JDBC

OOP (Classes & Objects)

Exception Handling

SQL Queries

Transactions

✨ Features

View available trains

Book railway tickets

Cancel booked tickets

View all booked tickets

Automatic seat count update

Secure database transactions

📂 Project Structure
Railway-Ticket-Booking-System
│
├── RailwaySystem.java
├── README.md

🗄️ Database Details

Database Name: railway_station

Tables Used:

trains

tickets

The system automatically:

Creates tables if not available

Inserts default train records on first run

⚙️ How to Run the Project
1️⃣ Prerequisites

Java JDK installed

MySQL Server installed

MySQL Connector/J (JDBC Driver)

2️⃣ Database Setup
CREATE DATABASE railway_station;


Update database credentials in the code:

static final String DB_URL = "jdbc:mysql://localhost:3306/railway_station";
static final String USER = "root";
static final String PASS = "your_password";

3️⃣ Compile & Run
javac RailwaySystem.java
java RailwaySystem

📸 Sample Menu Output
=== Railway Ticket Booking System ===
1. View Trains
2. Book Ticket
3. Cancel Ticket
4. View Booked Tickets
5. Exit

🔐 Important Note

⚠️ Do not upload real database passwords to GitHub.
Use environment variables or remove credentials before public upload.

📚 Learning Outcomes

Hands-on experience with Java & MySQL

Understanding database transactions

Real-world ticket booking workflow

Improved problem-solving skills

👨‍🎓 Author

Name: Vikram Vishwanath Kute
Course: B.Tech (Computer Science Engineering)
College: MGM College of Engineering, Nanded
Year: Second Year
