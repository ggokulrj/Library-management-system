# Library Management System

Java 17 + Spring Boot 3 + MySQL, with a simple HTML/CSS/JavaScript frontend.

## Run it
1. Install Java 17, Maven and MySQL. Install the Lombok plugin in your IDE (IntelliJ: Settings > Plugins; also enable annotation processing).
2. Open `src/main/resources/application.properties` and put your MySQL password in `spring.datasource.password`.
3. In the project folder run: `mvn spring-boot:run`
4. Open http://localhost:8080 in your browser. The database `library_db` and all tables are created automatically.

## Folder map (what each layer does)
| Folder | Job | Think of it as |
|---|---|---|
| `entity` | Java classes that become database tables | The shape of your data |
| `repository` | Interfaces that read/write the database (Spring writes the SQL for you) | The storeroom clerk |
| `service` | Business rules: can this book be issued? what is the fine? | The librarian's brain |
| `controller` | REST endpoints the frontend calls, e.g. `GET /api/books` | The front desk |
| `exception` | Turns errors into friendly JSON messages | The complaint desk |
| `dto` | Small classes for request data (IssueRequest) | A request slip |
| `static/index.html` | The frontend (HTML + CSS + JS, calls the REST API) | The screen |

## How one request flows
Browser (index.html) -> Controller -> Service -> Repository -> MySQL, and the result travels back the same way.
Example: clicking "Issue book" calls `POST /api/transactions/issue` -> `TransactionController.issue` -> `TransactionService.issueBook` (checks rules, reduces available copies, saves a transaction) -> repositories -> database.

## Your 5 workflow steps, mapped to code
1. Book management: `Book`, `BookService`, `BookController`
2. Issue and return: `TransactionService.issueBook` / `returnBook`
3. Database: entities + repositories (tables: books, members, transactions)
4. Reports: `ReportController`, `TransactionService.getSummary`, `getOverdue`, Reports tab
5. Validation: annotations like `@NotBlank` / `@Min` in entities, rule checks in services, `GlobalExceptionHandler`

## Rules built in (change the constants in TransactionService)
Loan period 14 days, maximum 3 books per member, fine 5 per late day.

## API quick list
- Books: GET /api/books?q=, POST, PUT /api/books/{id}, DELETE /api/books/{id}
- Members: GET, POST, PUT /api/members/{id}, DELETE /api/members/{id}
- Transactions: GET /api/transactions?status=ISSUED, POST /api/transactions/issue, POST /api/transactions/{id}/return
- Reports: GET /api/reports/summary, GET /api/reports/overdue

## Ideas to extend it yourself
Add pagination to the book list, a login with Spring Security, a book-cover URL field, email reminders for overdue books, and unit tests for `TransactionService`.
