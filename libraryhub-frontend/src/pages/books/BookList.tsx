import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/api';
import type { Book } from '../../types';

const BookList: React.FC = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [books, setBooks] = useState<Book[]>([]);
    const [recommendedBooks, setRecommendedBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedGenre, setSelectedGenre] = useState<string>('');

    useEffect(() => {
        loadBooks();
    }, []);

    useEffect(() => {
        if (user && books.length > 0) {
            loadRecommendations();
        }
    }, [user, books]);

    const loadBooks = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await apiService.getBooks();
            setBooks(data);
        } catch (err: any) {
            console.error('Failed to load books:', err);
            // Don't show error for unauthenticated users, just show empty list
            if (err.response?.status === 401) {
                setBooks([]);
            } else {
                setError('Failed to load books. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    const loadRecommendations = async () => {
        try {
            const data = await apiService.getRecommendations();

            // If backend returns empty, use available books as fallback
            if (data.length === 0) {
                const availableBooks = books.filter(book => book.available).slice(0, 4);
                setRecommendedBooks(availableBooks);
            } else {
                setRecommendedBooks(data.slice(0, 4));
            }
        } catch (err: any) {
            // Fallback to available books on error
            const availableBooks = books.filter(book => book.available).slice(0, 4);
            setRecommendedBooks(availableBooks);
        }
    };

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
    };

    const handleDeleteClick = async (book: Book) => {
        const confirmed = window.confirm(
            `Are you sure you want to delete "${book.title}"?\n\n` +
            `Warning: This will permanently delete:\n` +
            `• The book: ${book.title}\n` +
            `• All reviews for this book\n` +
            `• All reservations for this book\n\n` +
            `This action cannot be undone.`
        );

        if (!confirmed) return;

        try {
            await apiService.deleteBook(book.id);
            setBooks(prevBooks => prevBooks.filter(b => b.id !== book.id));
            setSuccessMessage(`"${book.title}" has been deleted successfully!`);
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Failed to delete book';
            setError(errorMsg);
            setTimeout(() => setError(null), 5000);
        }
    };

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/login');
        } catch (err) {
            console.error('Logout failed:', err);
        }
    };

    const filteredBooks = books.filter(book => {
        const matchesSearch = searchTerm
            ? book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
            book.genre?.toLowerCase().includes(searchTerm.toLowerCase())
            : true;

        const matchesGenre = selectedGenre
            ? book.genre?.toLowerCase() === selectedGenre.toLowerCase()
            : true;

        return matchesSearch && matchesGenre;
    });

    const genres = Array.from(new Set(books.map(book => book.genre).filter(Boolean))) as string[];

    const isAdmin = user?.role === 'ADMIN';
    const isCustomer = user?.role === 'CUSTOMER';

    if (loading) {
        return (
            <div className="container mt-5">
                <div className="text-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="bg-light min-vh-100">
            <div className="container py-4">
                <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
                    <h1 className="h2 fw-bold text-primary mb-0">📚 Library Books</h1>
                    <div className="d-flex flex-wrap gap-2">
                        {isAdmin && (
                            <button
                                onClick={() => navigate('/books/add')}
                                className="btn btn-success"
                            >
                                Add Book
                            </button>
                        )}

                        {isCustomer && (
                            <button
                                onClick={() => navigate('/dashboard')}
                                className="btn btn-info"
                            >
                                My Reservations
                            </button>
                        )}

                        {user && (
                            <>
                                <button
                                    onClick={() => navigate('/chatbot')}
                                    className="btn btn-success"
                                >
                                    AI Assistant
                                </button>

                                {isAdmin && (
                                    <button
                                        onClick={() => navigate('/admin/dashboard')}
                                        className="btn btn-warning"
                                    >
                                        Admin Dashboard
                                    </button>
                                )}

                                <button
                                    onClick={handleLogout}
                                    className="btn btn-outline-danger"
                                >
                                    Logout
                                </button>
                            </>
                        )}

                        {!user && (
                            <>
                                <button
                                    onClick={() => navigate('/login')}
                                    className="btn btn-primary"
                                >
                                    Sign In
                                </button>
                                <button
                                    onClick={() => navigate('/register')}
                                    className="btn btn-outline-primary"
                                >
                                    Sign Up
                                </button>
                            </>
                        )}
                    </div>
                </div>

                {successMessage && (
                    <div className="alert alert-success alert-dismissible fade show" role="alert">
                        {successMessage}
                        <button
                            type="button"
                            className="btn-close"
                            onClick={() => setSuccessMessage(null)}
                            aria-label="Close"
                        ></button>
                    </div>
                )}

                {error && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        {error}
                        <button
                            type="button"
                            className="btn-close"
                            onClick={() => setError(null)}
                            aria-label="Close"
                        ></button>
                    </div>
                )}

                <form onSubmit={handleSearch} className="mb-4">
                    <div className="row g-2">
                        <div className="col-md-8">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Search by title, author, or genre..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                        <div className="col-md-2">
                            <select
                                className="form-select"
                                value={selectedGenre}
                                onChange={(e) => setSelectedGenre(e.target.value)}
                            >
                                <option value="">All Genres</option>
                                {genres.sort().map((genre) => (
                                    <option key={genre} value={genre}>
                                        {genre}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="col-md-2">
                            <button type="submit" className="btn btn-primary w-100">
                                Search
                            </button>
                        </div>
                    </div>
                    {(searchTerm || selectedGenre) && (
                        <div className="mt-2">
                            <small className="text-muted">
                                Showing {filteredBooks.length} of {books.length} books
                                {searchTerm && ` matching "${searchTerm}"`}
                                {selectedGenre && ` in ${selectedGenre}`}
                            </small>
                            <button
                                type="button"
                                className="btn btn-sm btn-link text-danger ms-2"
                                onClick={() => {
                                    setSearchTerm('');
                                    setSelectedGenre('');
                                }}
                            >
                                Clear filters
                            </button>
                        </div>
                    )}
                </form>

                {/* Recommended Books Section */}
                {user && recommendedBooks.length > 0 && (
                    <div className="card mb-4 shadow-sm border-primary" style={{ borderWidth: '2px', borderRadius: '0.375rem', overflow: 'hidden' }}>
                        <div className="card-header bg-primary text-white" style={{ borderRadius: '0' }}>
                            <h5 className="mb-0">Recommended for You</h5>
                        </div>
                        <div className="card-body" style={{ backgroundColor: '#f8f9fa' }}>
                            <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4">
                                {recommendedBooks.map((book) => (
                                    <div className="col" key={book.id}>
                                        <div className="card shadow-sm h-100">
                                            <div className="card-body d-flex flex-column">
                                                <h4 className="card-title fw-bold mb-2" title={book.title}>
                                                    <a
                                                        href="#"
                                                        onClick={(e) => {
                                                            e.preventDefault();
                                                            navigate(`/books/${book.id}`);
                                                        }}
                                                        className="text-decoration-none text-dark"
                                                    >
                                                        {book.title}
                                                    </a>
                                                </h4>
                                                <p className="card-text mb-1 small">
                                                    <strong>Author:</strong> {book.author}
                                                </p>
                                                <p className="card-text mb-2 small">
                                                    <strong>Genre:</strong> {book.genre || 'N/A'}
                                                </p>
                                                <div className="mt-auto">
                                                    <span className={`badge ${
                                                        book.available ? 'bg-success' : 'bg-warning'
                                                    }`}>
                                                        {book.available ? 'Available' : 'Reserved'}
                                                    </span>
                                                </div>
                                            </div>
                                            <div className="card-footer bg-transparent border-0 pt-0">
                                                <div className="d-flex justify-content-between align-items-center gap-2">
                                                    <button
                                                        onClick={() => navigate(`/books/${book.id}`)}
                                                        className="btn btn-outline-primary btn-sm flex-grow-1"
                                                    >
                                                        View Details
                                                    </button>

                                                    {isAdmin && (
                                                        <>
                                                            <button
                                                                onClick={() => navigate(`/books/edit/${book.id}`)}
                                                                className="btn btn-outline-warning btn-sm"
                                                            >
                                                                Edit
                                                            </button>
                                                            <button
                                                                onClick={() => handleDeleteClick(book)}
                                                                className="btn btn-outline-danger btn-sm"
                                                            >
                                                                Delete
                                                            </button>
                                                        </>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                )}

                {filteredBooks.length === 0 ? (
                    <div className="alert alert-info text-center">
                        {searchTerm
                            ? `No books match your search: "${searchTerm}"`
                            : 'No books available.'}
                    </div>
                ) : (
                    <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4">
                        {filteredBooks.map((book) => (
                            <div className="col" key={book.id}>
                                <div className="card shadow-sm h-100">
                                    <div className="card-body d-flex flex-column">
                                        <h4 className="card-title fw-bold mb-2" title={book.title}>
                                            <a
                                                href="#"
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    navigate(`/books/${book.id}`);
                                                }}
                                                className="text-decoration-none text-dark"
                                            >
                                                {book.title}
                                            </a>
                                        </h4>
                                        <p className="card-text mb-1 small">
                                            <strong>Author:</strong> {book.author}
                                        </p>
                                        <p className="card-text mb-2 small">
                                            <strong>Genre:</strong> {book.genre || 'N/A'}
                                        </p>
                                        <div className="mt-auto">
                                            <span className={`badge ${
                                                book.available ? 'bg-success' : 'bg-warning'
                                            }`}>
                                                {book.available ? 'Available' : 'Reserved'}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="card-footer bg-transparent border-0 pt-0">
                                        <div className="d-flex justify-content-between align-items-center gap-2">
                                            <button
                                                onClick={() => navigate(`/books/${book.id}`)}
                                                className="btn btn-outline-primary btn-sm flex-grow-1"
                                            >
                                                View Details
                                            </button>

                                            {isAdmin && (
                                                <>
                                                    <button
                                                        onClick={() => navigate(`/books/edit/${book.id}`)}
                                                        className="btn btn-outline-warning btn-sm"
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteClick(book)}
                                                        className="btn btn-outline-danger btn-sm"
                                                    >
                                                        Delete
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default BookList;