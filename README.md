# Queue Management System

A simple queue system for clinics, fast food shops, banks, etc.
Built with Spring Boot, Thymeleaf and MySQL (Java 21).

## What it does
- **Kiosk** (`/`) - customer picks a service and gets a ticket number like A001
- **Ticket page** (`/ticket/{id}`) - shows how many people are ahead, refreshes every 10 seconds
- **Staff panel** (`/staff?counter=1`) - call next, mark done, skip no-shows

## Accounts (staff only)
The kiosk and ticket pages are public. The staff panel needs a login.
- `/register` - create an account
- `/login` - log in (Spring Security, passwords are BCrypt hashed)
- `/forgot-password` - enter your email, a reset link is sent to it
- `/reset-password?token=...` - choose a new password (link lasts 30 minutes, one use)

### Gmail setup for the reset email
1. Turn on 2-Step Verification on your Google account
2. Google Account > Security > App passwords > make one for "Mail"
3. Put your Gmail and the 16 letter app password in `application.properties`
   (`spring.mail.username` and `spring.mail.password`). Your normal Gmail password will NOT work.

While testing, the reset link is also printed in the console.

## How to run
1. Install Java 21, Maven and MySQL
2. Open `src/main/resources/application.properties` and put your MySQL username/password and Gmail details
3. Run `mvn spring-boot:run`
4. Open http://localhost:8080 (kiosk) and http://localhost:8080/staff (staff, asks you to log in)

The database `queue_db` and its tables are created automatically, and three
starter services are added (see `DataLoader.java` - change them for your business).

## Ideas for later
- Public display board ("Now serving" screen)
- Admin page to add/remove services and counters
- Wait-time reports
