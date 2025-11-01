# ELEC5619_Practical02_Group_11

## Library Reservation and Review Hub
Project Overview: This project addresses the problem of managing the status of different books within a library and provides a book management platform for libraries to use so that they can handle book reservations, waiting lists, and allows users to add community book reviews.

### Key Features
- Book Management – Search, reserve, and manage reservations for books online.

- Waiting List Management – Automatic queue tracking with notifications for users when books are available.

- Community Book Reviews – Write, rate, and read reviews to foster engagement.

- Search & Filters – Find books by title, author, or genre.

- Book Recommendations - Recommends books that users might like to reserve.

### High Level Goals
- **User Account and Profile Mangement** - Create user accounts, and let users borrow,  tract current reservations, see waiting list status and review history. 

- **Streamlined Book Reservation System** - Allow users to search and reserve available books online and also provide real-time updates on book availability. 

- **Intelligent Waiting List Management** - Let users join a waiting list for checked-out book. Also notify the user when the book is available. 

- **Engaging Community Review Platform** - Alow users to post and rate reviews for books, also display average ratings and highlight top reviews.


## Roles & Responsibilities
### Project Manager - Tinghui Chen
Arranges meeting time, plans and oversee the code integration, and develops a deep understanding of the library requirements.

### Technical Leader - Leon Lei
Designs the overall technical architecture for reservation management and reviews systems. Also ensures the code quality is high and mentors other developers.

### Frontend Developer - Ruijia Jia
Builds a responsive web interface, reusable UI components, and ennsures the platform meets accessibility standards.

### Backend Developer - Charity Wang
Designs and optimises databases for book catalogs, user data, reviews, and reservation tracking. Also ensures the system handles peak loads during popular book releases.

### DevOps Engineer - Shuchita Prakash
Sets up and maintains cloud infrastructure as well as setting up automated testing, deployment, and monitoring processes. Also implements robust data backup and disaster recovery procedures.


## Libraries

### Backend (Spring Boot)

#### Core Framework
| Library | Version | Description |
|---------|---------|-------------|
| Spring Boot | 3.5.5 | Core framework |
| Java | 17 | Programming language |
| Maven | 3.x | Build tool |

#### AI/Machine Learning
| Library | Version | Description |
|---------|---------|-------------|
| DJL API | 0.26.0 | Deep Java Library |
| DJL PyTorch Engine | 0.26.0 | PyTorch integration |
| PyTorch Native CPU | 2.1.2 | PyTorch native binaries (Apple Silicon) |
| DJL HuggingFace Tokenizers | 0.26.0 | Text tokenization |
| DJL Model Zoo | 0.26.0 | Pre-trained models |

#### Database
| Library | Version | Description |
|---------|---------|-------------|
| MySQL Connector | Latest | MySQL database driver |
| H2 Database | Latest | In-memory test database |
| Hibernate | 6.6.26.Final | JPA implementation |

#### Testing & Quality
| Library | Version | Description                |
|---------|---------|----------------------------|
| JUnit 5 | Latest | Unit testing               |
| Mockito | Latest | Mocking framework          |
| JaCoCo | 0.8.12 | Code coverage (93% target) |
| Maven Surefire | 3.5.3 | Test execution             |

### Frontend (React + TypeScript)

#### Core Framework
| Library | Version | Description |
|---------|---------|-------------|
| React | 19.1.1 | UI library |
| React DOM | 19.1.1 | React rendering |
| TypeScript | 5.9.3 | Type-safe JavaScript |

#### Routing & State
| Library | Version | Description |
|---------|---------|-------------|
| React Router DOM | 7.9.4 | Client-side routing |
| @tanstack/react-query | 5.90.2 | Server state management |

#### HTTP & API
| Library | Version | Description |
|---------|---------|-------------|
| Axios | 1.12.2 | HTTP client |

#### UI & Styling
| Library | Version | Description |
|---------|---------|-------------|
| Tailwind CSS | 3.4.18 | Utility-first CSS |
| @tailwindcss/forms | 0.5.10 | Form styling |
| Bootstrap | 5.3.2 | UI components (CDN) |
| Lucide React | 0.545.0 | Icon library |
| PostCSS | 8.5.6 | CSS processing |
| Autoprefixer | 10.4.21 | CSS vendor prefixing |

## Working Functionalities

| ID        | Functionality | Description |
|-----------|---------------|-------------|
| **FR-1**  | **User Registration** | The system allows new users to register for an account by providing a username, password, and email |
| **FR-2**  | **Login** | The system authenticates users based on their credentials to allow them to log in |
| **FR-3**  | **Log Out** | The system allows logged-in users to log out, terminating their session |
| **FR-4**  | **Manage Profile** | Logged-in users can update their personal profile details |
| **FR-5**  | **Browse Books** | The system displays a browsable list of books, filterable by title, author, and genre |
| **FR-6**  | **Search Books** | The system provides a search function to find books based on keywords |
| **FR-7**  | **Make Reservations** | The system allows users to reserve books that are currently available |
| **FR-8**  | **Cancel Reservation** | The system allows users to cancel reservations they have made |
| **FR-9**  | **Join Waiting List** | The system allows users to join a waiting list for books that are currently checked out |
| **FR-10** | **Quit Waiting List** | The system allows users to remove themselves from a waiting list |
| **FR-11** | **Write Reviews** | The system allows users to write and submit textual reviews and star ratings for books |
| **FR-12** | **View Reviews** | The system displays all user-submitted reviews and average ratings on book detail pages |
| **FR-13** | **Delete Reviews** | Users can delete any review they have personally submitted |
| **FR-14** | **Book Recommendations** | The system generates book recommendations based on users' past reviews |
| **FR-15** | **Add Books** | Administrators can add new books with details (title, author, genre, etc.) to the system inventory |
| **FR-16** | **Update Books** | Administrators can update the details of any existing book |
| **FR-17** | **Remove Books** | Administrators can remove books from the system |
| **FR-18** | **Delete Book Lists Automatically** | When an administrator deletes a book, the system automatically removes all users from that book's waiting list |
| **FR-19** | **Register Users** | Administrators can create new user accounts and assign roles (User or Administrator) |
| **FR-20** | **Manage Profiles** | Administrators can update any user's profile information |
| **FR-21** | **Delete Account** | Administrators can delete any user account from the system |
| **FR-22** | **Delete Reviews** | Administrators can delete any review from any user |
| **FR-23** | **Chat Interface** | The system provides a chat interface where users can interact with an AI bot using natural language |
| **FR-24** | **Library FAQ** | The AI bot answers questions about library policies (borrowing rules, fines, opening hours, reservation hold periods) |
| **FR-25** | **Series Recommendations** | The AI bot identifies and recommends book series based on user histories |
| **FR-26** | **Natural Language Search** | The AI bot parses natural-language queries into structured filters |
