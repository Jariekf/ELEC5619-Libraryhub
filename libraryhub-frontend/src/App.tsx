import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Navbar from './components/layout/Navbar';
import LoginForm from './components/auth/LoginForm';
import RegisterForm from './components/auth/RegisterForm';
import Chatbot from './components/chatbot/Chatbot';
import Dashboard from './pages/admin/Dashboard';
import ManageUsers from './pages/admin/Customers';
import Reservations from './pages/admin/Reservations';
import ManageWaitingList from './pages/admin/WaitingList';
import BookList from './pages/books/BookList';
import BookDetails from './pages/books/BookDetails';
import AddBook from './pages/books/AddBook';
import EditBook from './pages/books/EditBook';
import CustomerDashboard from './pages/customer/CustomerDashboard';
import ProfilePage from './components/profile/ProfilePage';

// Load Bootstrap JS dynamically
const loadBootstrap = () => {
    if ((window as any).bootstrap) {
        return Promise.resolve();
    }

    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js';
        script.integrity = 'sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL';
        script.crossOrigin = 'anonymous';
        script.onload = () => {
            console.log('Bootstrap JS loaded');
            resolve(undefined);
        };
        script.onerror = reject;
        document.body.appendChild(script);
    });
};

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: 1,
            refetchOnWindowFocus: false,
        },
    },
});

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
};

const AdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { user, isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (user?.role !== 'ADMIN') {
        return <Navigate to="/books" replace />;
    }

    return <>{children}</>;
};

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
                                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-8 rounded-lg transition-colors duration-200 text-lg"
                                >
                                    Browse Books
                                </a>
                                <a
                                    href="/chatbot"
                                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-3 px-8 rounded-lg transition-colors duration-200 text-lg"
                                >
                                    AI Assistant
                                </a>
                            </>
                        ) : (
                            <>
                                <a
                                    href="/register"
                                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-8 rounded-lg transition-colors duration-200 text-lg"
                                >
                                    Get Started
                                </a>
                                <a
                                    href="/login"
                                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-3 px-8 rounded-lg transition-colors duration-200 text-lg"
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

const AppContent: React.FC = () => {
    const location = window.location.pathname;
    const hideNavbar = location === '/login' || location === '/register';

    return (
        <Router>
            <div className="min-h-screen bg-gray-50">
                {!hideNavbar && <Navbar />}
                <Routes>
                    {/* Public Routes */}
                    <Route path="/" element={<HomePage />} />
                    <Route path="/login" element={<LoginForm />} />
                    <Route path="/register" element={<RegisterForm />} />

                    {/* Book Routes - BookList public, BookDetails protected */}
                    <Route path="/books" element={<BookList />} />
                    <Route
                        path="/books/:id"
                        element={
                            <ProtectedRoute>
                                <BookDetails />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/books/add"
                        element={
                            <AdminRoute>
                                <AddBook />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/books/edit/:id"
                        element={
                            <AdminRoute>
                                <EditBook />
                            </AdminRoute>
                        }
                    />

                    {/* User Dashboard - Customer Reservations */}
                    <Route
                        path="/dashboard"
                        element={
                            <ProtectedRoute>
                                <CustomerDashboard />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/profile"
                        element={
                            <ProtectedRoute>
                                <ProfilePage />
                            </ProtectedRoute>
                        }
                    />

                    {/* Chatbot - Public access */}
                    <Route
                        path="/chatbot"
                        element={
                            <div className="min-h-screen bg-gray-50 py-8">
                                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                                    <Chatbot />
                                </div>
                            </div>
                        }
                    />

                    {/* Admin Routes - All protected */}
                    <Route
                        path="/admin"
                        element={
                            <AdminRoute>
                                <Navigate to="/admin/dashboard" replace />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/admin/dashboard"
                        element={
                            <AdminRoute>
                                <Dashboard />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/admin/users"
                        element={
                            <AdminRoute>
                                <ManageUsers />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/admin/reservations"
                        element={
                            <AdminRoute>
                                <Reservations />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/admin/waitinglist"
                        element={
                            <AdminRoute>
                                <ManageWaitingList />
                            </AdminRoute>
                        }
                    />

                    {/* Catch-all redirect */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </div>
        </Router>
    );
};

const App: React.FC = () => {
    const [bootstrapLoaded, setBootstrapLoaded] = React.useState(false);

    React.useEffect(() => {
        loadBootstrap()
            .then(() => setBootstrapLoaded(true))
            .catch(err => console.error('Failed to load Bootstrap:', err));
    }, []);

    if (!bootstrapLoaded) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
            </div>
        );
    }

    return (
        <QueryClientProvider client={queryClient}>
            <AuthProvider>
                <AppContent />
            </AuthProvider>
        </QueryClientProvider>
    );
};

export default App;
