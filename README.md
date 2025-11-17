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
- **Frontend:** React, Bootstrap  
- **Backend:** Spring Boot  
- **Database:** MySQL / PostgreSQL  
- **Integration:** Google/Outlook Calendar API  

---

## ✨ Key Features
- ✅ Bill splitting and payment tracking  
- ✅ Event and task management  
- ✅ Chore and responsibility distribution  
- ✅ Shared household calendar  
- ✅ Notifications and reminders  
- ✅ Dashboard with an overview of bills, chores, and events  

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