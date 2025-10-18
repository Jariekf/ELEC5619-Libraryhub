import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/api';
import type { Book, Review, WaitingList } from '../../types';

const BookDetails: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [book, setBook] = useState<Book | null>(null);
    const [reviews, setReviews] = useState<Review[]>([]);
    const [waitingList, setWaitingList] = useState<WaitingList[]>([]);
    const [reservationInfo, setReservationInfo] = useState<any>(null);
    const [loading, setLoading] = useState(true);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const [newReview, setNewReview] = useState<{
        rating: number;
        comment: string;
    }>({
        rating: 0,
        comment: '',
    });

    useEffect(() => {
        if (id) {
            loadBookDetails();
        }
    }, [id]);

    const loadBookDetails = async () => {
        try {
            setLoading(true);
            const bookData = await apiService.getBookById(Number(id));
            setBook(bookData);

            const reviewsData = await apiService.getReviewsForBook(Number(id));
            setReviews(reviewsData);

            // Load waiting list
            try {
                const waitingListData = await apiService.getWaitingListForBook(Number(id));
                setWaitingList(waitingListData);
            } catch (waitingErr) {
                console.log('Waiting list not available');
                setWaitingList([]);
            }

            // Load reservation info if admin and book is not available
            if (user?.role === 'ADMIN' && !bookData.available) {
                try {
                    const response = await fetch(`http://localhost:8080/api/books/${id}/reservation`, {
                        credentials: 'include'
                    });
                    if (response.ok) {
                        const reservationData = await response.json();
                        setReservationInfo(reservationData);
                    }
                } catch (err) {
                    console.log('No reservation info available');
                    setReservationInfo(null);
                }
            } else {
                setReservationInfo(null);
            }
        } catch (err: any) {
            console.error('Failed to load book details:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to load book details';
            setErrorMessage(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const handleReserve = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await apiService.reserveBook(Number(id));
            setSuccessMessage('Book reserved successfully!');
            setErrorMessage(null);
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to reserve book:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to reserve book';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const handleJoinWaitingList = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await apiService.joinWaitingList(Number(id));
            setSuccessMessage('Joined waiting list successfully!');
            setErrorMessage(null);
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to join waiting list:', err);
            // Extract backend error message from response
            const errorMsg = err.response?.data?.message || err.message || 'Failed to join waiting list';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const handleDeleteWaitingEntry = async (entryId: number, username: string) => {
        if (!confirm(`Are you sure you want to remove ${username} from the waiting list?`)) {
            return;
        }

        try {
            await apiService.deleteWaitingEntry(entryId);
            setSuccessMessage(`${username} has been removed from the waiting list.`);
            setErrorMessage(null);
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to delete waiting entry:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to remove from waiting list';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const handleCancelWaiting = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await apiService.cancelWaiting(Number(id));
            setSuccessMessage('Cancelled waiting list successfully!');
            setErrorMessage(null);
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to cancel waiting:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to cancel waiting';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const handleSubmitReview = async (e: React.FormEvent) => {
        e.preventDefault();

        if (newReview.rating === 0 || !newReview.comment.trim()) {
            setErrorMessage('Please fill in all fields');
            return;
        }

        try {
            const bookIdNum: number = Number(id);
            const ratingNum: number = newReview.rating;
            const commentStr: string = newReview.comment;

            await apiService.submitReview(bookIdNum, ratingNum, commentStr);
            setSuccessMessage('Review submitted successfully!');
            setErrorMessage(null);
            setNewReview({ rating: 0, comment: '' });
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to submit review:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to submit review';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const handleDeleteReview = async (reviewId: number) => {
        if (!confirm('Are you sure you want to delete this review?')) {
            return;
        }

        try {
            await apiService.deleteReview(reviewId);
            setSuccessMessage('Review deleted successfully!');
            setErrorMessage(null);
            loadBookDetails();
        } catch (err: any) {
            console.error('Failed to delete review:', err);
            const errorMsg = err.response?.data?.message || err.message || 'Failed to delete review';
            setErrorMessage(errorMsg);
            setSuccessMessage(null);
        }
    };

    const calculateAverageRating = (): number => {
        if (reviews.length === 0) return 0;
        const sum = reviews.reduce((acc, review) => acc + review.rating, 0);
        return sum / reviews.length;
    };

    const renderStars = (rating: number) => {
        return Array.from({ length: 5 }, (_, i) => (
            <span
                key={i}
                className={i < rating ? 'text-warning' : 'text-muted'}
            >
        ★
      </span>
        ));
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

    const isAdmin = user?.role === 'ADMIN';
    const isUserInWaitingList = waitingList.some((entry: any) =>
        entry.username === user?.username
    );

    if (loading) {
        return (
            <div className="container mt-4">
                <div className="text-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        );
    }

    if (!book) {
        return (
            <div className="container mt-4">
                <div className="alert alert-danger">Book not found</div>
            </div>
        );
    }

    const averageRating = calculateAverageRating();

    return (
        <div className="container mt-4">
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

            {/* Book Details Card */}
            <div className="card">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h1 className="display-4 fw-bold mb-0">{book.title}</h1>
                    <div className="d-flex gap-2">
                        <button
                            onClick={() => navigate('/books')}
                            className="btn btn-secondary"
                        >
                            Back to Book List
                        </button>
                        {isAdmin && (
                            <button
                                onClick={() => navigate('/admin/dashboard')}
                                className="btn btn-warning"
                            >
                                Admin Dashboard
                            </button>
                        )}
                    </div>
                </div>
                <div className="card-body">
                    <p className="card-text">
                        <strong>Author:</strong> {book.author}
                    </p>
                    <p className="card-text">
                        <strong>Genre:</strong> {book.genre}
                    </p>
                    <p className="card-text">
                        <strong>Status:</strong>
                        {book.available ? (
                            <span className="text-success fw-bold"> Available</span>
                        ) : (
                            <span className="text-danger fw-bold"> Not Available</span>
                        )}
                    </p>

                    {/* Admin: Show Reservation Info */}
                    {isAdmin && reservationInfo && (
                        <div className="alert alert-info mt-3">
                            <h5 className="alert-heading">Current Reservation</h5>
                            <hr />
                            <p className="mb-1">
                                <strong>Reserved by:</strong> {reservationInfo.username}
                            </p>
                            <p className="mb-1">
                                <strong>Reservation Date:</strong> {formatDate(reservationInfo.reservationDate)}
                            </p>
                            <p className="mb-0">
                                <strong>Reservation ID:</strong> {reservationInfo.id}
                            </p>
                        </div>
                    )}

                    {/* Waiting List Info */}
                    {!book.available && (
                        <div className={`alert ${waitingList.length > 0 ? 'alert-info' : 'alert-warning'} mt-3`}>
                            <strong>Waiting List:</strong> {' '}
                            {waitingList.length === 0
                                ? 'No one is waiting yet. Be the first to join!'
                                : `${waitingList.length} ${waitingList.length === 1 ? 'person is' : 'people are'} waiting for this book`
                            }
                        </div>
                    )}

                    {/* Reservation Button for Customers and Admins */}
                    {book.available && (
                        <form onSubmit={handleReserve} className="mt-3 mb-3">
                            <button type="submit" className="btn btn-primary">
                                Reserve this Book
                            </button>
                        </form>
                    )}

                    {/* Waiting List Controls - Available for both Admin and Customer */}
                    {!book.available && (
                        <div className="mt-3 mb-3">
                            {!isUserInWaitingList ? (
                                <form onSubmit={handleJoinWaitingList}>
                                    <button type="submit" className="btn btn-warning">
                                        Join Waiting List
                                    </button>
                                </form>
                            ) : (
                                <div>
                                    <div className="alert alert-success mb-2">
                                        ✓ You are on the waiting list for this book
                                    </div>
                                    <form onSubmit={handleCancelWaiting}>
                                        <button type="submit" className="btn btn-danger">
                                            Cancel Waiting
                                        </button>
                                    </form>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Average Rating Display */}
                    {averageRating > 0 && (
                        <div className="mt-4">
                            <h5>
                                Average Rating: {averageRating.toFixed(1)}{' '}
                                <span className="text-warning">★</span>
                            </h5>
                        </div>
                    )}
                </div>
            </div>

            {/* Waiting List Details (Admin and when list has users) */}
            {waitingList.length > 0 && (
                <div className="card mt-4">
                    <div className="card-header">
                        <h4>
                            Waiting List <span className="badge bg-warning text-dark">{waitingList.length}</span>
                        </h4>
                    </div>
                    <div className="card-body">
                        <div className="table-responsive">
                            <table className="table table-hover table-striped">
                                <thead className="table-light">
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Username</th>
                                    <th scope="col">Joined Date</th>
                                    {isAdmin && <th scope="col">Status</th>}
                                    <th scope="col">Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                {waitingList.map((entry: any, index) => (
                                    <tr key={entry.id}>
                                        <th scope="row">{index + 1}</th>
                                        <td>
                                            {entry.username || 'Unknown'}
                                            {entry.username === user?.username && (
                                                <span className="badge bg-success ms-2">You</span>
                                            )}
                                        </td>
                                        <td>
                                            {entry.joinedDate
                                                ? new Date(entry.joinedDate).toLocaleDateString('en-US', {
                                                    year: 'numeric',
                                                    month: 'short',
                                                    day: 'numeric',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                })
                                                : 'N/A'}
                                        </td>
                                        {isAdmin && (
                                            <td>
                                                {index === 0 ? (
                                                    <span className="badge bg-primary">Next in line</span>
                                                ) : (
                                                    <span className="text-muted">Position {index + 1}</span>
                                                )}
                                            </td>
                                        )}
                                        <td>
                                            {isAdmin ? (
                                                <button
                                                    onClick={() => handleDeleteWaitingEntry(entry.id, entry.username || 'User')}
                                                    className="btn btn-sm btn-outline-danger"
                                                >
                                                    Remove
                                                </button>
                                            ) : (
                                                entry.username === user?.username && (
                                                    <button
                                                        onClick={() => handleCancelWaiting({ preventDefault: () => {} } as React.FormEvent)}
                                                        className="btn btn-sm btn-outline-danger"
                                                    >
                                                        Cancel
                                                    </button>
                                                )
                                            )}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}

            {/* Review Submission Form */}
            <div className="card mt-4">
                <div className="card-header">
                    <h4>Write a Review</h4>
                </div>
                <div className="card-body">
                    <form onSubmit={handleSubmitReview}>
                        <div className="form-group mb-3">
                            <label htmlFor="rating">Rating (1-5 stars):</label>
                            <select
                                className="form-control"
                                id="rating"
                                value={newReview.rating.toString()}
                                onChange={(e) =>
                                    setNewReview({ ...newReview, rating: parseInt(e.target.value, 10) || 0 })
                                }
                                required
                            >
                                <option value="0">Select Rating</option>
                                <option value="1">1 Star</option>
                                <option value="2">2 Stars</option>
                                <option value="3">3 Stars</option>
                                <option value="4">4 Stars</option>
                                <option value="5">5 Stars</option>
                            </select>
                        </div>
                        <div className="form-group mb-3">
                            <label htmlFor="comment">Review Comment:</label>
                            <textarea
                                className="form-control"
                                id="comment"
                                rows={4}
                                placeholder="Share your thoughts about this book..."
                                value={newReview.comment}
                                onChange={(e) =>
                                    setNewReview({ ...newReview, comment: e.target.value })
                                }
                                required
                            />
                        </div>
                        <button type="submit" className="btn btn-success">
                            Submit Review
                        </button>
                    </form>
                </div>
            </div>

            {/* Reviews Display */}
            <div className="card mt-4">
                <div className="card-header">
                    <h4>
                        Reviews <span className="badge bg-secondary">{reviews.length}</span>
                    </h4>
                </div>
                <div className="card-body">
                    {reviews.length === 0 ? (
                        <div className="text-muted">
                            <p>No reviews yet. Be the first to review this book!</p>
                        </div>
                    ) : (
                        reviews.map((review) => (
                            <div key={review.id} className="border-bottom pb-3 mb-3">
                                <div className="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h6 className="mb-1">{review.username || 'Anonymous'}</h6>
                                        <div className="mb-2">
                                            {renderStars(review.rating)}
                                            <span className="ms-2">{review.rating} stars</span>
                                        </div>
                                        <p className="mb-0">{review.comment}</p>
                                    </div>
                                    <div>
                                        {(isAdmin || user?.username === review.username) && (
                                            <button
                                                onClick={() => handleDeleteReview(review.id)}
                                                className="btn btn-sm btn-outline-danger"
                                            >
                                                Delete
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default BookDetails;