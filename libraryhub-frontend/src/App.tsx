import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Navbar from './components/layout/Navbar';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import BookList from './components/books/BookList';
import BookDetails from './components/books/BookDetails';
import EditBook from './components/books/EditBook';
import AddBook from './components/books/AddBook';
import Chatbot from './components/chatbot/Chatbot';

// Create a query client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// Protected Route component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-library-600"></div>
      </div>
    );
  }

  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

// Admin Route component
const AdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-library-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (user?.role !== 'ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

// Home page component
const HomePage: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            Welcome to LibraryHub
          </h1>
          <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
            Discover, reserve, and review books in our digital library.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            {isAuthenticated ? (
              <>
                <a
                  href="/books"
                  className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-lg px-8 py-3"
                >
                  Browse Books
                </a>
                <a
                  href="/chatbot"
                  className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-lg px-8 py-3"
                >
                  AI Assistant
                </a>
              </>
            ) : (
              <>
                <a
                  href="/register"
                  className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-lg px-8 py-3"
                >
                  Get Started
                </a>
                <a
                  href="/login"
                  className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-lg px-8 py-3"
                >
                  Sign In
                </a>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Dashboard component - different views for Customer and Admin
const Dashboard: React.FC = () => {
  const { user } = useAuth();
  
  // Redirect admin users to admin panel
  if (user?.role === 'ADMIN') {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Admin Dashboard</h1>
          <p className="text-gray-600 mb-8">Welcome, {user.username}!</p>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Manage Books</h2>
              <p className="text-gray-600 mb-4">Add, edit, or remove books from the library collection.</p>
              <a href="/books" className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg inline-block">
                View Books
              </a>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">User Management</h2>
              <p className="text-gray-600 mb-4">View and manage library users and their accounts.</p>
              <a href="/admin" className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg inline-block">
                Manage Users
              </a>
            </div>
            <div className="bg-white rounded-lg shadow-md p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Reservations</h2>
              <p className="text-gray-600 mb-4">View and manage all book reservations in the system.</p>
              <a href="/admin" className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg inline-block">
                View All
              </a>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Customer dashboard
  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">My Dashboard</h1>
        <p className="text-gray-600 mb-8">Welcome back, {user?.username}!</p>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">My Reservations</h2>
            <p className="text-gray-600 mb-4">Your currently reserved books will appear here.</p>
            <a href="/books" className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg inline-block">
              Browse Books
            </a>
          </div>
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Recommendations</h2>
            <p className="text-gray-600 mb-4">Personalized book recommendations based on your reading history.</p>
            <a href="/chatbot" className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg inline-block">
              Get Recommendations
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

// Admin Panel component (placeholder)
const AdminPanel: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Admin Panel</h1>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Book Management</h2>
            <p className="text-gray-600">Manage the library's book collection.</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">User Management</h2>
            <p className="text-gray-600">Manage library users and their accounts.</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">System Settings</h2>
            <p className="text-gray-600">Configure library settings and policies.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

// Main App component
const AppContent: React.FC = () => {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginForm />} />
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/books" element={<BookList />} />
          <Route path="/books/:id" element={<BookDetails />} />
          <Route 
            path="/books/add" 
            element={
              <ProtectedRoute>
                <AddBook />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/books/edit/:id" 
            element={
              <ProtectedRoute>
                <EditBook />
              </ProtectedRoute>
            } 
          />
          
          {/* Protected routes */}
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/chatbot" 
            element={
              <ProtectedRoute>
                <div className="min-h-screen bg-gray-50 py-8">
                  <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <Chatbot />
                  </div>
                </div>
              </ProtectedRoute>
            } 
          />
          
          {/* Admin routes */}
          <Route 
            path="/admin" 
            element={
              <AdminRoute>
                <AdminPanel />
              </AdminRoute>
            } 
          />
          
          {/* Catch all route */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
};

// Root App component with providers
const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </QueryClientProvider>
  );
};

export default App;