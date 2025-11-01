import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
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
import './HomePage.css';

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
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="homepage-background">
                <div className="homepage-overlay">
                    <div className="min-h-screen flex items-center justify-center">
                        <div className="spinner-border text-primary" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="homepage-background">
            <div className="homepage-overlay">
                <div className="homepage-content">
                    <div className="homepage-hero">
                        <h1 className="homepage-title">
                            Welcome to LibraryHub
                        </h1>
                        <p className="homepage-subtitle">
                            Discover, reserve, and review books in our library.
                        </p>
                        <div className="homepage-buttons">
                            {isAuthenticated ? (
                                <>
                                    <a
                                        href="/books"
                                        className="homepage-btn homepage-btn-primary"
                                    >
                                        Browse Books
                                    </a>
                                    <a
                                        href="/chatbot"
                                        className="homepage-btn homepage-btn-secondary"
                                    >
                                        AI Assistant
                                    </a>
                                </>
                            ) : (
                                <>
                                    <a
                                        href="/register"
                                        className="homepage-btn homepage-btn-primary"
                                    >
                                        Sign Up
                                    </a>
                                    <a
                                        href="/login"
                                        className="homepage-btn homepage-btn-secondary"
                                    >
                                        Sign In
                                    </a>
                                </>
                            )}
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

const AppRoutes: React.FC = () => {
    const location = useLocation();
    const hideNavbar = location.pathname === '/login' || location.pathname === '/register' || location.pathname === '/';

    return (
        <div className="min-h-screen bg-gray-50">
            {!hideNavbar && <Navbar />}
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginForm />} />
                <Route path="/register" element={<RegisterForm />} />

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

                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </div>
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
                <Router>
                    <AppRoutes />
                </Router>
            </AuthProvider>
        </QueryClientProvider>
    );
};

export default App;