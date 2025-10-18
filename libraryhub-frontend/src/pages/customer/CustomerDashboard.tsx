import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface Reservation {
    id: number;
    bookId: number;
    bookTitle: string;
    bookAuthor: string;
    reservationDate: string;
    username: string;
}

const CustomerDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [loading, setLoading] = useState(true);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    useEffect(() => {
        loadReservations();
    }, []);

    const loadReservations = async () => {
        try {
            setLoading(true);
            // Try the endpoint that should exist
            const response = await fetch('http://localhost:8080/api/user/reservations', {
                credentials: 'include'
            });

            if (response.status === 404) {
                // Backend endpoint doesn't exist yet
                setErrorMessage('The reservations feature is not yet available. Please contact your administrator.');
                setReservations([]);
                setLoading(false);
                return;
            }

            if (!response.ok) {
                throw new Error('Failed to load reservations');
            }

            const data = await response.json();
            setReservations(Array.isArray(data) ? data : []);
        } catch (err: any) {
            const errorMsg = err.response?.data?.message || err.message || 'Failed to load reservations';
            setErrorMessage(errorMsg);
            setReservations([]);
        } finally {
            setLoading(false);
        }
    };

    const handleReturnBook = async (bookId: number, bookTitle: string) => {
        const confirmed = window.confirm(
            `Are you sure you want to return "${bookTitle}"?`
        );

        if (!confirmed) return;

        try {
            // Call the return book API endpoint
            const response = await fetch(`http://localhost:8080/api/books/${bookId}/return`, {
                method: 'POST',
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to return book');
            }

            setSuccessMessage(`"${bookTitle}" has been returned successfully!`);
            setErrorMessage(null);
            loadReservations();
            setTimeout(() => setSuccessMessage(null), 3000);
        } catch (err: any) {
            const errorMsg = err.message || 'Failed to return book';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

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
        <div className="container mt-4">
            <h1 className="h2 fw-bold mb-4">My Reserved Books</h1>

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

            {errorMessage && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                    {errorMessage}
                    <button
                        type="button"
                        className="btn-close"
                        onClick={() => setErrorMessage(null)}
                        aria-label="Close"
                    ></button>
                </div>
            )}

            {reservations.length === 0 ? (
                <div className="alert alert-info mt-3">
                    <p className="mb-0">You have no reserved books.</p>
                </div>
            ) : (
                <div className="list-group mt-3">
                    {reservations.map((reservation) => (
                        <div
                            key={reservation.id}
                            className="list-group-item d-flex justify-content-between align-items-center"
                        >
                            <div>
                                <h5 className="mb-1">{reservation.bookTitle}</h5>
                                <p className="mb-1">
                                    by <span>{reservation.bookAuthor}</span>
                                </p>
                                <small className="text-muted">
                                    Reserved on: {formatDate(reservation.reservationDate)}
                                </small>
                            </div>

                            <div className="text-nowrap">
                                <button
                                    onClick={() => navigate(`/books/${reservation.bookId}`)}
                                    className="btn btn-outline-primary btn-sm me-2"
                                >
                                    View
                                </button>
                                <button
                                    onClick={() => handleReturnBook(reservation.bookId, reservation.bookTitle)}
                                    className="btn btn-success btn-sm"
                                >
                                    Return Book
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <div className="mt-3">
                <button
                    onClick={() => navigate('/books')}
                    className="btn btn-primary me-2"
                >
                    Browse More Books
                </button>
                <button
                    onClick={() => navigate('/chatbot')}
                    className="btn btn-secondary"
                >
                    Chat with AI Assistant
                </button>
            </div>
        </div>
    );
};

export default CustomerDashboard;