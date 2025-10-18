import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const Navbar: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const isLoggedIn = isAuthenticated && Boolean(user?.username);

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const toggleProfile = () => {
    setIsProfileOpen(!isProfileOpen);
  };

  return (
      <nav className="bg-white shadow-lg border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            {/* Logo and main navigation */}
            <div className="flex items-center">
              <Link to="/" className="flex items-center space-x-2">
                <span className="text-xl font-bold text-gray-900">LibraryHub</span>
              </Link>

              {/* Desktop navigation */}
              <div className="hidden md:ml-10 md:flex md:space-x-8">
                <Link
                    to="/books"
                    className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Books
                </Link>
                {isLoggedIn ? (
                    <>
                      <Link
                          to="/dashboard"
                          className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                      >
                        My Dashboard
                      </Link>
                      {user?.role === 'ADMIN' && (
                          <Link
                              to="/admin/dashboard"
                              className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                          >
                            Admin Panel
                          </Link>
                      )}
                      <Link
                          to="/chatbot"
                          className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                      >
                        🤖 AI Assistant
                      </Link>
                    </>
                ) : (
                    <>
                      <Link
                          to="/register"
                          className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                      >
                        Register
                      </Link>
                      <Link
                          to="/login"
                          className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                      >
                        Sign In
                      </Link>
                    </>
                )}
              </div>
            </div>

            {/* Right side - Profile */}
            <div className="flex items-center space-x-4">

              {/* Profile dropdown */}
              {isLoggedIn ? (
                  <div className="relative">
                    <button
                        onClick={toggleProfile}
                        className="flex items-center space-x-2 text-gray-700 hover:text-gray-900 transition-colors"
                    >
                      <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                    <span className="text-blue-600 text-sm font-medium">
                      {user?.username?.charAt(0).toUpperCase()}
                    </span>
                      </div>
                      <span className="hidden md:block text-sm font-medium">
                    {user?.username}
                  </span>
                    </button>

                    {/* Profile dropdown menu */}
                    {isProfileOpen && (
                        <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200">
                          <div className="px-4 py-2 text-sm text-gray-700 border-b border-gray-100">
                            <p className="font-medium">{user?.username}</p>
                            <p className="text-gray-500 capitalize">{user?.role?.toLowerCase()}</p>
                          </div>
                          <Link
                              to="/dashboard"
                              className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                              onClick={() => setIsProfileOpen(false)}
                          >
                            My Dashboard
                          </Link>
                          <Link
                              to="/profile"
                              className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                              onClick={() => setIsProfileOpen(false)}
                          >
                            Profile
                          </Link>
                          {user?.role === 'ADMIN' && (
                              <Link
                                  to="/admin/dashboard"
                                  className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                  onClick={() => setIsProfileOpen(false)}
                              >
                                Admin Panel
                              </Link>
                          )}
                          <button
                              onClick={handleLogout}
                              className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                          >
                            Sign out
                          </button>
                        </div>
                    )}
                  </div>
              ) : (
                  <div className="flex items-center space-x-2">
                    <Link
                        to="/login"
                        className="text-gray-500 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium transition-colors"
                    >
                      Sign in
                    </Link>
                    <Link
                        to="/register"
                        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
                    >
                      Sign up
                    </Link>
                  </div>
              )}

              {/* Mobile menu button */}
              <button
                  onClick={toggleMenu}
                  className="md:hidden p-2 text-gray-400 hover:text-gray-500 transition-colors"
              >
                {isMenuOpen ? '✕' : '☰'}
              </button>
            </div>
          </div>

          {/* Mobile menu */}
          {isMenuOpen && (
              <div className="md:hidden">
                <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3 border-t border-gray-200">
                  <Link
                      to="/books"
                      className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                      onClick={() => setIsMenuOpen(false)}
                  >
                    Books
                  </Link>
                  {isLoggedIn ? (
                      <>
                        <Link
                            to="/dashboard"
                            className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                            onClick={() => setIsMenuOpen(false)}
                        >
                          My Dashboard
                        </Link>
                        <Link
                            to="/chatbot"
                            className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                            onClick={() => setIsMenuOpen(false)}
                        >
                          AI Assistant
                        </Link>
                        {user?.role === 'ADMIN' && (
                            <Link
                                to="/admin/dashboard"
                                className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                                onClick={() => setIsMenuOpen(false)}
                            >
                              Admin Panel
                            </Link>
                        )}
                      </>
                  ) : (
                      <>
                        <Link
                            to="/register"
                            className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                            onClick={() => setIsMenuOpen(false)}
                        >
                          Register
                        </Link>
                        <Link
                            to="/login"
                            className="text-gray-500 hover:text-gray-900 block px-3 py-2 rounded-md text-base font-medium"
                            onClick={() => setIsMenuOpen(false)}
                        >
                          Sign In
                        </Link>
                      </>
                  )}
                </div>
              </div>
          )}
        </div>
      </nav>
  );
};

export default Navbar;
