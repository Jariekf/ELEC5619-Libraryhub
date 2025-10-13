# LibraryHub Frontend

A modern React frontend for the LibraryHub application, built with TypeScript, Vite, and Tailwind CSS.

## Features

- 🔐 **Authentication** - Login and registration with Spring Security integration
- 📚 **Book Management** - Browse, search, and reserve books
- 🤖 **AI Chatbot** - Intelligent assistant powered by Llama 3.2
- 👥 **User Dashboard** - Personal reservations and recommendations
- ⚙️ **Admin Panel** - Book and user management (admin only)
- 📱 **Responsive Design** - Works on desktop and mobile devices
- 🎨 **Modern UI** - Clean, accessible interface with Tailwind CSS

## Tech Stack

- **React 18** with TypeScript
- **Vite** for fast development and building
- **React Router** for client-side routing
- **TanStack Query** for server state management
- **Axios** for API communication
- **Tailwind CSS** for styling
- **Lucide React** for icons

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn
- Running LibraryHub backend (Spring Boot)

### Installation

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```

3. Open your browser and navigate to `http://localhost:5173`

### Building for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

## Project Structure

```
src/
├── components/          # React components
│   ├── auth/           # Authentication components
│   ├── books/          # Book-related components
│   ├── chatbot/        # AI chatbot components
│   └── layout/         # Layout components (Navbar, etc.)
├── contexts/           # React contexts (AuthContext)
├── services/           # API service layer
├── types/              # TypeScript type definitions
└── App.tsx             # Main application component
```

## API Integration

The frontend communicates with the Spring Boot backend through:

- **Authentication**: Session-based auth with Spring Security
- **Books**: CRUD operations for book management
- **Reservations**: Book reservation and return functionality
- **Reviews**: Community book reviews
- **Waiting Lists**: Queue management for unavailable books
- **Chatbot**: AI assistant integration

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Environment Configuration

The frontend is configured to connect to the backend at `http://localhost:8080`. To change this, update the `baseURL` in `src/services/api.ts`.

## Features in Detail

### Authentication
- Secure login/registration forms
- Session management with Spring Security
- Protected routes for authenticated users
- Role-based access (Customer/Admin)

### Book Management
- Responsive book grid/list view
- Advanced search and filtering
- Book details with reviews and ratings
- Reservation and waiting list functionality

### AI Chatbot
- Real-time chat interface
- Integration with Llama 3.2 AI model
- Fallback to rule-based responses
- Example queries and conversation history

### User Dashboard
- Personal book reservations
- Reading recommendations
- Review history
- Account management

### Admin Panel
- Book collection management
- User account administration
- System monitoring and settings

## Development

### Adding New Components

1. Create component files in appropriate directories
2. Export components from index files
3. Import and use in parent components
4. Add TypeScript types as needed

### API Integration

1. Add new endpoints to `src/services/api.ts`
2. Define TypeScript types in `src/types/index.ts`
3. Use TanStack Query for data fetching
4. Handle loading and error states

### Styling

- Use Tailwind CSS utility classes
- Create custom components in `src/index.css`
- Follow the design system with library color palette
- Ensure responsive design for all screen sizes

## Contributing

1. Follow the existing code structure
2. Use TypeScript for type safety
3. Write clean, readable code
4. Test components thoroughly
5. Follow the established naming conventions

## License

This project is part of the ELEC5619 Practical 02 assignment.