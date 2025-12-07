# RoomieRadar

RoomieRadar is a **web application** designed to streamline household management for individuals living in shared spaces. It simplifies communication, task tracking, and financial responsibilities among roommates—helping reduce conflicts and improve organization.

---

## 📌 Table of Contents
- [Project Overview](#project-overview)  
- [Target Audience](#target-audience)  
- [Technology Stack](#technology-stack)  
- [Key Features](#key-features)  
- [Project Timeline](#project-timeline)  
- [Functional Requirements](#functional-requirements)  
- [Storyboard](#storyboard)  
- [Architecture & Components](#architecture--components)  
- [Team Roles](#team-roles)  
- [GitHub Links](#github-links)  
- [Milestones](#milestones)  
- [License](#license)  

---

## 🚀 Project Overview
**Project Title:** RoomieRadar  

**Purpose:**  
RoomieRadar helps roommates coordinate shared living by managing chores, splitting bills, scheduling events, and organizing responsibilities in one centralized platform.  

---

## 🧑‍🤝‍🧑 Target Audience
- Young adults in **dormitories**, **apartments**, or **shared housing**  
- Groups such as **fraternities/sororities** that need a centralized management system  

---

## 🛠️ Technology Stack
- **Frontend:** Thymeleaf, Bootstrap 5.3, Vanilla JavaScript  
- **Backend:** Spring Boot 3.5.7, Spring Security, Spring Data JPA  
- **Database:** MySQL 9.5 with Flyway migrations  
- **Build Tool:** Maven  
- **Template Engine:** Thymeleaf 3.x  
- **ORM:** Hibernate 6.6  
- **Authentication:** Spring Security with form-based login  


### ✅ Implemented Features
- **Bill Management & Splitting**
  - Create bills with automatic split calculation
  - Split equally among all household members OR select specific members
  - Track individual split amounts with precision (BigDecimal)
  - View all household bills with amounts and split counts
  - Delete bills with confirmation
  - Due dates and descriptions for bills
## 🔐 Authentication & Security
RoomieRadar uses **Spring Security 6.5** for comprehensive user authentication and authorization:
- **User Authentication & Authorization**
### Authentication Flow
  - Secure signup and login with Spring Security
  - Username must be unique
  - Email validation
  - Optional: Create or join household during signup
  - Session-based authentication
  - Form-based authentication
  - Session creation on successful login
  - Redirects to home page or last attempted page
- **Logout:** Accessible via `/logout` link in navigation on all pages
  - Clears session and redirects to login page

### Security Features
- **Protected Routes:** All application pages require authentication
  - Unauthenticated users are automatically redirected to login
  - Public endpoints: `/login`, `/signup`, `/css/**`, `/js/**`
- **Household-Based Authorization:** Many features require household membership
  - Bills, events, chores, calendar pages check for household
  - Users without household see warnings and limited functionality
- **CSRF Protection:** Currently disabled for development (can be re-enabled)
- **Password Management:** 
  - Legacy-compatible encoder for migration support
  - BCrypt ready for future implementation
  - Secure password storage
- **Session Management:** 
  - HTTP sessions maintained until logout
  - Automatic timeout after inactivity
  - Concurrent session control available
  - Logout functionality
### Security Configuration
- **Spring Security Filter Chain** handles all authentication/authorization
- **Custom UserDetailsService** loads users from database
- **Method-level security** available via `@PreAuthorize` annotations
- **SQL injection prevention** via JPA parameterized queries

**Configuration:** Hibernate physical naming strategy preserves database column names (camelCase) to match MySQL schema exactly.
- **Household Management**
  - Join existing household with name and password
  - Create new household as admin
  - View all household members
  - Single household per user model
  - Household-based access control
  

### Frontend Architecture
- **Template Engine:** Thymeleaf for server-side rendering
- **CSS Framework:** Bootstrap 5.3 with custom styles
- **JavaScript:** Vanilla JS with Fetch API for AJAX
- **Components:** Modular templates with reusable fragments
- **Responsive Design:** Mobile-first approach with responsive utilities

### Backend Architecture
- **API Layer:** Spring Boot REST controllers
- **Service Layer:** Business logic separation (BillService, UserService, HouseholdService, etc.)
- **Repository Layer:** Spring Data JPA repositories with custom queries
- **Entity Layer:** JPA entities with relationships (User, Household, Bill, BillSplit, Event, Chore, etc.)
- **DTO Layer:** Data Transfer Objects for clean API contracts
- **Security Layer:** Spring Security filter chain

### Database Schema
**Core Tables:**
- `user` - User accounts with authentication credentials
- `household` - Household groups with name and password
- `bill` - Bills with amount, due date, description, creator
- `bill_split` - Individual user portions of bills with status
- `event` - Household events with date/time
- `event_attendees` - Many-to-many relationship for event participants
- `chore` - Assigned chores with due dates and status
- `calendar_item` - Shared calendar entries

**Migrations:** Managed with Flyway (V1 through V10)
- V1: Initial schema (user, household base)
- V2: Household table
- V3: Calendar table
- V4: Event, chore, bill tables
- V5: Bill-user relationship updates
- V6: Bill split table with due dates and descriptions
- V7: Added createdById to bill table
- V8: Changed bill_split isPaid to status enum
- V9: Event date updates
- V10: Fixed event_attendees structure

### Key Design Patterns
- **Repository Pattern:** Data access abstraction
- **Service Pattern:** Business logic encapsulation
- **DTO Pattern:** Separation of API and domain models
- **MVC Pattern:** Model-View-Controller separation
- **Builder Pattern:** Complex object construction (DTOs)
  
- **Calendar Integration**
  - Shared household calendar
  - View events and chores in calendar format
  - Month/week/day views
  
## 🏠 Household Management Flow
  - Mobile-first responsive design
### Initial Setup
1. **User Registration** at `/signup`
   - Provide username, email, password
   - Account created but no household assigned yet
   
2. **First Login**
   - User logs in successfully
   - If no household membership, sees warnings on feature pages

## 🔌 API Endpoints

### Authentication Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/login` | Display login page | No |
| POST | `/login` | Authenticate user | No |
| GET | `/signup` | Display signup page | No |
| POST | `/register` | Create new user account | No |
| GET | `/logout` | Logout and clear session | Yes |

### Page Endpoints
| Method | Endpoint | Description | Household Required |
|--------|----------|-------------|-------------------|
| GET | `/` | Home/Dashboard page | No |
| GET | `/bills` | Bills management page | No* |
| GET | `/chores` | Chores management page | No* |
| GET | `/events` | Events management page | No* |
| GET | `/calendar` | Shared calendar page | No* |
| GET | `/household` | Household/People page | No |

*Accessible but functionality limited without household membership

### Bill API Endpoints
| Method | Endpoint | Description | Returns |
|--------|----------|-------------|---------|
| GET | `/bills` | View bills page (HTML) | Thymeleaf template |
| POST | `/bills/add` | Create new bill | `"success"` or `"error: message"` |
| DELETE | `/bills/{id}` | Delete bill by ID | `"success"` or `"error: message"` |
| GET | `/bills/{id}/splits` | Get bill split details | JSON array of BillSplit objects |
| POST | `/bills/split/{id}/mark-paid` | Mark split as paid | `"success"` or `"error: message"` |

### Household API Endpoints
| Method | Endpoint | Description | Returns |
|--------|----------|-------------|---------|
| GET | `/household` | View household page | Thymeleaf template |
| POST | `/household/join` | Join existing household | Redirect with status |
| POST | `/household/create` | Create new household | Redirect with status |

### Event API Endpoints
| Method | Endpoint | Description | Returns |
|--------|----------|-------------|---------|
| GET | `/events` | View events page | Thymeleaf template |
| POST | `/events/add` | Create new event | Redirect |
| GET | `/events/{id}` | View event details | Thymeleaf template |
| POST | `/events/{id}/edit` | Edit event | Redirect |
| DELETE | `/events/{id}` | Delete event | Redirect |

### Calendar API Endpoints
| Method | Endpoint | Description | Returns |
|--------|----------|-------------|---------|
| GET | `/calendar` | View calendar page | Thymeleaf template |
| GET | `/calendar/events` | Get calendar events (JSON) | JSON array |

### Request/Response Examples

#### Create Bill (POST /bills/add)
**Request Body (form-encoded):**
```
name=Electric Bill
amount=120.50
dueDate=2025-12-15T00:00:00
description=November electric usage
splitEqually=true
```

**Success Response:**
```
success
```

**Error Response:**
```
error: User must be in a household to create bills
```

#### Get Bill Splits (GET /bills/{id}/splits)
**Response (JSON):**
```json
[
  {
    "id": 1,
    "billId": 5,
    "userId": 2,
    "splitAmount": 30.13,
    "status": "UNPAID"
  },
  {
    "id": 2,
    "billId": 5,
    "userId": 3,
    "splitAmount": 30.13,
    "status": "PAID"
  }
]
```

---

## 💰 Bill Splitting Feature (NEW)
This project is developed for **academic purposes** (University of Cincinnati, IT 4045C). License terms can be updated if the project is extended beyond coursework.
The bill splitting feature is a core component of RoomieRadar, allowing roommates to fairly divide expenses and track payments.

### How It Works
## 📝 Recent Updates (December 2025)

### Version 10.0 - UI Enhancement & Bill Splitting
**Date:** December 6, 2025

**Major Changes:**
- ✅ Complete bill splitting system with approval workflow
- ✅ Enhanced UI/UX with Bootstrap 5 and responsive design
- ✅ Fixed bottom navigation with logout option
- ✅ Modal-based forms for bills, events, and chores
- ✅ Household management improvements (join/create flow)
- ✅ Security enhancements (Spring Security 6.5)
- ✅ Database migrations V6-V10 (bill_split table, status enum, event fixes)
- ✅ Bug fixes (lazy loading issues, modal handlers, port conflicts)
- ✅ Comprehensive documentation and testing guides

**Files Changed:** 56 files (2,666 additions, 1,200 deletions)

**See:** [BRANCH_SUMMARY_ui-enhancement-and-cleanup.md](BRANCH_SUMMARY_ui-enhancement-and-cleanup.md) for complete details.

---

## 🤝 Contributing
This is an academic project, but contributions from team members are welcome:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

**Coding Standards:**
- Follow Java naming conventions (camelCase for variables/methods, PascalCase for classes)
- Write meaningful commit messages
- Add comments for complex logic
- Test your changes before committing
- Update documentation when adding features

---

## 📞 Support & Contact
- **Project Board:** [GitHub Project Board](https://github.com/users/krishsah01/projects/4/views/1)
- **Repository:** [FinalProject_4045](https://github.com/krishsah01/FinalProject_4045.git)
- **Team Email:** (available in project documentation)
- **University:** University of Cincinnati, IT 4045C

For issues and bug reports, please use the GitHub Issues tab in the repository.  

---

## 🚀 How to Run the Application

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+** (or use included Maven wrapper `./mvnw`)
- **MySQL 9.5** (or compatible version like 8.x)
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Step 1: Database Setup
1. **Install MySQL** if not already installed
2. **Start MySQL server** (ensure it's running on port 3306)
3. **Create MySQL user** (if using custom credentials):
   ```sql
   CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON *.* TO 'your_username'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Step 2: Configure Database Credentials
Update MySQL username and password in these files:

**File 1: `pom.xml`** (lines 83-84)
```xml
<user>your_username</user>
<password>your_password</password>
```
   - **Bill Name** (required) - e.g., "Electric Bill", "Netflix", "Groceries"
**File 2: `src/main/resources/application.properties`** (lines 4-5)
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```
   - **Due Date** (optional) - When payment is due
### Step 3: Run Flyway Migrations
The database will be created automatically, but you need to run migrations:
   - **Split Method**:
**Option A: Using Maven**
```bash
./mvnw flyway:migrate
```

**Option B: Using IDE**
- Open Maven tool window
- Navigate to `Plugins` → `flyway` → `flyway:migrate`
- Double-click to run

**Verify:** Check that `roomieRadarData` database was created with 10 tables.

### Step 4: Connect to Database (Optional)
In your IDE's database manager:
1. Add new Data Source → MySQL
2. Host: `localhost`, Port: `3306`
3. Database: `roomieRadarData`
4. Username/Password: (your credentials)
5. Test Connection → OK

### Step 5: Run the Application

**Option A: Using IDE**
1. Open `RoomieRadarApplication.java`
2. Click Run button (or right-click → Run)
3. Wait for "Started RoomieRadarApplication" message

**Option B: Using Maven Command**
```bash
./mvnw spring-boot:run
```

**Option C: Using JAR**
```bash
./mvnw clean package
java -jar target/RoomieRadar-0.0.1-SNAPSHOT.jar
```

### Step 6: Access the Application
1. Open browser
2. Navigate to: **http://localhost:8080**
3. You'll be redirected to login page
4. Click "Sign Up" to create an account
5. After signup, login with your credentials

### Troubleshooting

#### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process (replace PID with actual process ID)
kill -9 <PID>
```

#### Flyway Migration Errors
- Ensure MySQL is running
- Verify database credentials are correct
- Check that no other instance has locked the database
- Try `./mvnw flyway:clean` then `./mvnw flyway:migrate` (WARNING: deletes all data)

#### Application Won't Start
- Check Java version: `java -version` (should be 21+)
- Verify MySQL connection in application.properties
- Look for error messages in console output
- Check that all migrations ran successfully

#### Can't Login
- Verify user was created (check `user` table in database)
- Ensure Spring Security is not misconfigured
- Check browser console for JavaScript errors
- Try incognito/private browsing mode

### Default Test Users
After running the application, you can create test users via signup page. Example:
- Username: `alice`
- Email: `alice@test.com`
- Password: `password123`

### Development Mode
For easier development, you can:
1. Enable auto-restart with Spring Boot DevTools (already in pom.xml)
2. Access H2 console if using H2 database (currently MySQL)
3. Enable debug logging in application.properties:
   ```properties
   logging.level.com.group5final.roomieradar=DEBUG
   ```

### Production Deployment
For production:
1. Enable CSRF protection in SecurityConfig
2. Use BCrypt password encoder (already available)
3. Set secure session cookies
4. Configure HTTPS
5. Use environment variables for sensitive data
6. Enable SQL logging only for debugging
7. Set appropriate CORS policies
     - **Custom Split** (uncheck to select) - Choose specific members to split with

4. If Custom Split selected:
   - Checkboxes appear with all household members
   - Select one or more members to include in split
   - Must select at least one member

5. Click **"Add Bill"** in modal
   - Button shows "Adding..." during submission
   - Page reloads on success
   - New bill appears in list

#### Viewing Bills
Bills are displayed in card format showing:
- **Bill Name** (large, bold)
- **Due Date** (if provided) - formatted as "Due: MMM DD, YYYY"
- **Description** (if provided)
- **Total Amount** - Blue badge showing total bill amount
- **Delete Button** - Red trash icon (with confirmation)

**Example Display:**
```
┌─────────────────────────────────────┐
│ Electric Bill              [Trash]  │
│ Due: Dec 15, 2025                   │
│ November electric usage             │
│ Total: $120.00                      │
└─────────────────────────────────────┘
```

#### Split Calculation
**Split Equally Example:**
- Bill: $120.00
- Household: 4 members (Alice, Bob, Charlie, Dave)
- **Each person owes: $30.00**
- System creates 4 bill_split records, one per member

**Custom Split Example:**
- Bill: $20.00 (Netflix subscription)
- Selected: 2 members (Alice, Bob only)
- **Each person owes: $10.00**
- System creates 2 bill_split records
- Charlie and Dave are not included

#### Deleting Bills
1. Click red trash icon on any bill
2. Confirmation dialog appears: "Are you sure you want to delete this bill?"
3. Click OK to confirm
4. Bill and all associated splits are deleted (cascade delete)
5. Page reloads to show updated list

### Technical Details

#### Data Model
```
Bill
├── id (auto-increment)
├── name (varchar 255)
├── amount (decimal 10,2)
├── dueDate (datetime, nullable)
├── description (varchar 500, nullable)
├── householdId (foreign key)
├── createdById (foreign key to user)
└── splits (one-to-many relationship)

BillSplit
├── id (auto-increment)
├── billId (foreign key, cascade delete)
├── userId (foreign key, cascade delete)
├── splitAmount (decimal 10,2)
└── status (enum: UNPAID, PAID, APPROVED)
```

#### Precision & Accuracy
- All monetary calculations use **BigDecimal** (no floating-point errors)
- Rounding mode: **HALF_UP** (standard financial rounding)
- Scale: 2 decimal places (cents precision)

#### Backend Endpoints
- `GET /bills` - View bills page
- `POST /bills/add` - Create new bill (returns "success" or "error: message")
- `DELETE /bills/{id}` - Delete bill
- `GET /bills/{id}/splits` - Get bill split details (JSON)
- `POST /bills/split/{id}/mark-paid` - Mark split as paid (future UI integration)

#### Frontend Technology
- **Modal:** Bootstrap 5 modal component
- **Form Validation:** HTML5 required fields + JavaScript validation
- **AJAX:** Fetch API for form submission
- **Dynamic UI:** JavaScript toggle for split method selection
- **User Checkboxes:** Dynamically populated from household members

### Future Enhancements
- [ ] Mark individual splits as paid/unpaid
- [ ] Approve payments (by bill creator)
- [ ] View split details per bill
- [ ] Filter bills by status
- [ ] Edit existing bills
- [ ] Recurring bills
- [ ] Bill reminders/notifications
- [ ] Payment history tracking
- [ ] Export to PDF/CSV
- [ ] Unequal split amounts (custom percentages)

---

### Joining or Creating Household

#### Option 1: Join Existing Household
1. Navigate to `/household` (or click "People" in navigation)
2. Click "Join Existing Household"
3. Enter:
   - Household name
   - Household password
4. System validates credentials
5. User added to household
6. Full access to all features granted

#### Option 2: Create New Household
1. Navigate to `/household`
2. Click "Create New Household"
3. Enter:
   - Unique household name
   - Household password (for others to join)
4. System creates household with user as first member
5. User becomes household member
6. Can share name/password with roommates

### Household Page Features
Once in a household, the household page shows:
- **Household Information**
  - Household name
  - Member count
  
- **Members List**
  - All users in the household
  - Sorted alphabetically by username
  - Real-time updates as members join

### Feature Access Control
**Without Household:**
- ❌ Cannot create/view bills
- ❌ Cannot create/view events
- ❌ Cannot create/view chores
- ❌ Cannot access shared calendar
- ✅ Can view household page (to join/create)
- ✅ Can view navigation

**With Household:**
- ✅ Full access to all bills (view, create, delete)
- ✅ Full access to all events
- ✅ Full access to all chores
- ✅ Full calendar access
- ✅ Can see all household members
- ✅ Logout available

> **Note:** The single-household-per-user model means each user can only be in one household at a time. To switch households, they must leave current one first (future feature).
  - Fixed bottom navigation
  - Modal-based forms
  - Clean card-based layouts
  - Conditional rendering based on household membership

### 🚧 Planned Features
- Email notifications and reminders
- Recurring bills
- Bill payment history
- Chore rotation automation
- Google/Outlook Calendar API integration
- File sharing
- Shopping list management  
- ✅ Shared household calendar  
- ✅ Notifications and reminders  
- ✅ Dashboard with an overview of bills, chores, and events  

---

## 🔐 Authentication & Login
RoomieRadar uses **Spring Security** for user authentication:

- **Signup:** New users register at `/signup` with username, email, and password
- **Login:** Users authenticate at `/login` using their username and password
- **Security:** All application pages require authentication; unauthenticated users are redirected to login
- **Session Management:** Users remain logged in until they explicitly logout at `/logout`

**Configuration:** Hibernate naming strategy preserves database column names (camelCase) to match MySQL schema.

---

## 📅 Project Timeline
| Task | Deadline |
|------|----------|
| Design Document & UML | 09/27/25 |
| Database Design & UI/UX Mockup | 10/11/25 |
| Backend Implementation | 10/25/25 |
| Frontend Integration | 11/01/25 |
| Testing & Debugging | 11/22/25 |
| Final Submission & Presentation | 12/01/25 |

---

## 📋 Functional Requirements

| Feature | Given (Prerequisite) | When (Steps) | Then (Expected Result) | Priority | Acceptance Criteria |
|---------|----------------------|--------------|-------------------------|----------|---------------------|
| **Bill Splitting** | User is part of a household | User enters a bill, amount, and selects participants | Each roommate’s share is calculated, balances updated, notifications sent | High | Each participant sees correct amount owed; payments update correctly |
| **Event Management** | User is part of a household | User creates an event, sets date/time, invites participants | Event added to shared calendar, notifications sent | High | Event appears on all calendars; canceled events removed |
| **Chore Distribution** | Household has multiple roommates | Admin assigns chores manually/automatically | Each roommate sees tasks, can mark as complete | High | Tasks visible; completion updates logs |
| **Responsibility Distribution** | Household has shared responsibilities | User assigns responsibility to a roommate | System tracks responsible user, sends reminders | Medium | Reminders sent; dashboard updates |
| **Calendar Integration** | Household has events/chores/responsibilities | User schedules task/event | App syncs with Google/Outlook Calendar | High | All items appear in calendars; reminders work |
| **Notifications** | Users have pending tasks/events/payments | Event occurs or deadline nears | System sends reminders | High | Notifications sent on time with correct content |
| **Dashboard Overview** | User logs in | User views dashboard | Shows events, unpaid bills, chores | Medium | Dashboard is real-time, clear |
| **User Management** | Admin/household creator exists | Admin adds/removes users | Household members updated | Medium | New users can log in; removed users lose access |

---

## 🎨 Storyboard
Storyboard mockups will be created using **Figma/InVision**.  
👉 [View Mockups Here](https://stitch.withgoogle.com/projects/899605140695144859)

---

## 🏗️ Architecture & Components
- **Frontend:** React components with Bootstrap styling  
- **Backend:** Spring Boot REST APIs  
- **Database:** MySQL/PostgreSQL with normalized schema  
- **Integrations:** Google/Outlook Calendar APIs  
- **Notifications:** Reminder & task alerts  

<img width="1231" height="834" alt="image" src="https://github.com/user-attachments/assets/ed6d459e-bbf4-4966-8640-a90bd752466b" />

---

## 👥 Team Roles
- **Scrum Master:** Zach  
- **Scrum Developers:** Krish, Sydney, Anthony  

**Individual Responsibilities:**  
- **Krish:** Building endpoints, Project Management, Frontend  
- **Zach:** Scrum Master, Calendar API Integration  
- **Anthony:** Database, UML Diagrams  
- **Sydney:** UX/UI Design, UI Interfacing, Testing  

---

## 🔗 GitHub Links
- **Project Board:** [GitHub Project Board](https://github.com/users/krishsah01/projects/4/views/1)  
- **Repository:** [FinalProject_4045](https://github.com/krishsah01/FinalProject_4045.git)  

---

## 🏁 Milestones
### Milestone #1  
- Create initial codebase  
- Implement database connection  
- Create initial frontend version  
- Set placeholders for Milestone #2 & #3  

### Milestone #2  
- Backend code to integrate frontend and DB  
- Create ADD, EDIT, DELETE methods  

### Milestone #3  
- Final integration of database with UI  

---

## 📋 Scrum Process
- **Sprint Planning:** Scrum Master decides stories for each sprint  
- **Sprint Tasks:** Tracked in GitHub Project Board  
- **Milestone Linking:** Each task linked to GitHub milestones  

---

## 🏠 Household Flow (Current Implementation)
- After login, if you have no household you can visit `/household` (or any gated page will redirect you there) to Join or Create.
- Join: supply existing household name + password.
- Create: choose a unique household name + password.
- Once joined, the household page shows basic info and a member list (all users sharing that household).
- Core feature pages (bills, chores, events, calendar) are gated until membership exists.

> Note: Legacy `users` management pages were deprecated in favor of the consolidated `/household` page and the single-household-per-user model.

---

## 📜 License
This project is developed for **academic purposes** (University of Cincinnati, IT 4045C). License terms can be updated if the project is extended beyond coursework.  

---

## To run
In intellij, or whatever IDE you use change the username, and password of the MYSQL user sections to your local username and password.
These files are: 
    pom.xml lines 83, and 84
    application.properties lines 4 and 5

Once complete, use the flyway:migrate tool in maven to build the database.

If not already, connect to the MYSQL localhost database in the database manager.

Run the program from the IDE run option.
