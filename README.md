# Parking Management System (Java + Swing + SQLite)

## Requirements
- JDK 17 or newer (the project uses Java text blocks)
- Visual Studio Code with the Extension Pack for Java
- `lib/sqlite-jdbc-3.53.4.0.jar` (already included)

## Project structure
- `src/com/parkingmanagement/Main.java` — application entry point
- `config/Database.java` — SQLite connection and table creation
- `model/` — Vehicle, Car, Bike, ParkingSession, Reservation
- `repository/` — database access interfaces and SQLite implementations
- `service/` — parking and reservation business logic
- `view/MainDashboardView.java` — Swing dashboard
- `controller/MainGuiController.java` — dashboard actions

## Run from the project folder in PowerShell
```powershell
javac -cp "lib\sqlite-jdbc-3.53.4.0.jar" -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "out;lib\sqlite-jdbc-3.53.4.0.jar" com.parkingmanagement.Main
```

The SQLite database file `parkingmanagement.db` is created in the current working directory on first run. Run the commands from the project root so the database is created in the project folder.

## Features
- Register and list cars and bikes
- View parking slot availability
- Record vehicle entry and exit
- Calculate parking fee (₹30 per hour for cars and ₹15 per hour for bikes; minimum one hour)
- Create and cancel reservations

## Notes
- Vehicle registration is required before entry or reservation.
- Reservations use `YYYY-MM-DD` for dates and `HH:MM` for times.
- The older console controller was removed because its `ConsoleView` was absent and its method calls did not match the current service APIs. The project now starts through the Swing dashboard.
