# Smart Hostel Mess & Attendance Management System

A complete desktop application built with **Java Swing** and **Oracle SQL** to automate hostel mess operations for engineering college students.

This project was developed to solve real problems faced in hostels — manual attendance marking, messy bill calculations, and complaint handling.

## ✨ Key Features

- **Student Management Module** – Full CRUD operations (Add, View, Update, Delete & Search students)
- **Mess Menu Planner** – Admin can update daily breakfast, lunch, and dinner menu
- **Daily Attendance Marking** – Mark attendance for Breakfast, Lunch, or Dinner with date selection
- **Automatic Billing System** – Generates monthly bills based on meals attended (₹30 per meal)
- **Fine Calculation** – Automatic fines applied for low attendance
- **Complaints Module** – Students can raise complaints, Admin can mark them as Resolved
- **Monthly Reports** – Summary report showing total meals, revenue, fines, and average attendance
- **Clean & Professional Swing GUI** with easy-to-use dashboard

## 🛠️ Technologies Used

- Java (Core)
- Swing GUI (for beautiful desktop interface)
- Oracle SQL Database
- JDBC for database connectivity
- OOP Principles (Inheritance, Encapsulation, Abstraction)
- CRUD Operations & Aggregate Queries

## 🚀 How to Run the Project

1. Clone or download the repository
2. Create the database tables by running the SQL script (available in repository)
3. Update your Oracle username and password in `DBConnection.java`
4. Compile all Java files:
   ```bash
   javac *.java
