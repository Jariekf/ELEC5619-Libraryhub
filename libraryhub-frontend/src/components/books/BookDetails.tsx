import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import type { Book, Review } from '../../types';
import apiService from '../../services/api';

const BookDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [book, setBook] = useState<Book | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [averageRating, setAverageRating] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Review form state
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [newReview, setNewReview] = useState({ rating: 5, comment: '' });
  
  useEffect(() => {
    if (id) {
      loadBookDetails();
    }
  }, [id]);

  const loadBookDetails = async () => {
    try {
      setLoading(true);
      // In a real app, you would fetch book details, reviews, etc. from the backend
      // For now, we'll use the API we have
      const bookData = await apiService.getBookById(Number(id));
      setBook(bookData);
      
      // TODO: Fetch reviews when the endpoint is available
      // const reviewsData = await apiService.getReviewsForBook(Number(id));
      // setReviews(reviewsData);
      
    } catch (err) {
      setError('Failed to load book details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReserve = async () => {
    if (!book) return;
    try {
      await apiService.reserveBook(book.id);
      alert('Book reserved successfully!');
      loadBookDetails();
    } catch (err) {
      alert('Failed to reserve book');
    }
  };

  const handleReturn = async () => {
    if (!book) return;
    try {
      await apiService.returnBook(book.id);
      alert('Book returned successfully!');
      loadBookDetails();
    } catch (err) {
      alert('Failed to return book');
    }
  };

  const handleJoinWaitingList = async () => {
    if (!book) return;
    try {
      await apiService.joinWaitingList(book.id);
      alert('Joined waiting list successfully!');
      loadBookDetails();
    } catch (err) {
      alert('Failed to join waiting list');
    }
  };

  const handleCancelWaiting = async () => {
    if (!book) return;
    try {
      await apiService.cancelWaiting(book.id);
      alert('Cancelled waiting list successfully!');
      loadBookDetails();
    } catch (err) {
      alert('Failed to cancel waiting list');
    }
  };

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!book) return;
    
    try {
      await apiService.submitReview(book.id, newReview);
      alert('Review submitted successfully!');
      setShowReviewForm(false);
      setNewReview({ rating: 5, comment: '' });
      loadBookDetails();
    } catch (err) {
      alert('Failed to submit review');
    }
  };

  const handleDeleteReview = async (reviewId: number) => {
    if (!book || !window.confirm('Are you sure you want to delete this review?')) return;
    
    try {
      await apiService.deleteReview(book.id, reviewId);
      alert('Review deleted successfully!');
      loadBookDetails();
    } catch (err) {
      alert('Failed to delete review');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error || 'Book not found'}</p>
          <button onClick={() => navigate('/books')} className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg">
            Back to Books
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Book Details Card */}
        <div className="bg-white border border-gray-300 mb-6">
          {/* Title */}
          <div className="border-b border-gray-300 px-6 py-4">
            <h1 className="text-3xl font-bold text-gray-900">{book.title}</h1>
          </div>

          {/* Book Info */}
          <div className="px-6 py-4 space-y-3">
            <div>
              <span className="font-semibold">Author:</span> {book.author}
            </div>
            <div>
              <span className="font-semibold">Genre:</span> {book.genre}
            </div>
            <div>
              <span className="font-semibold">Status:</span>{' '}
              <span className={book.available ? 'text-green-600 font-semibold' : 'text-red-600 font-semibold'}>
                {book.available ? 'Available' : 'Checked Out'}
              </span>
            </div>
          </div>

          {/* Waiting List Section */}
          <div className="border-t border-gray-300 px-6 py-4">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Waiting List</h2>
            <table className="w-full border-collapse border border-gray-300">
              <thead>
                <tr className="bg-gray-100">
                  <th className="border border-gray-300 px-4 py-2 text-left">#</th>
                  <th className="border border-gray-300 px-4 py-2 text-left">User</th>
                  <th className="border border-gray-300 px-4 py-2 text-left">Joined At</th>
                  <th className="border border-gray-300 px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td colSpan={4} className="border border-gray-300 px-4 py-3 text-gray-600">
                    No one is in the waiting list yet.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Action Buttons */}
          <div className="border-t border-gray-300 px-6 py-4 flex gap-2">
            <button
              onClick={() => navigate('/books')}
              className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded"
            >
              Back to Book List
            </button>
            
            {user?.role === 'ADMIN' && (
              <>
                <button
                  onClick={() => navigate(`/books/edit/${book.id}`)}
                  className="bg-yellow-500 hover:bg-yellow-600 text-white font-medium py-2 px-4 rounded"
                >
                  Edit Book
                </button>
                <button
                  onClick={async () => {
                    if (window.confirm('Are you sure you want to delete this book?')) {
                      try {
                        await apiService.deleteBook(book.id);
                        alert('Book deleted successfully!');
                        navigate('/books');
                      } catch (err) {
                        alert('Failed to delete book');
                      }
                    }
                  }}
                  className="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded"
                >
                  Delete Book
                </button>
              </>
            )}

            {user?.role === 'CUSTOMER' && (
              <>
                {book.available && (
                  <button
                    onClick={handleReserve}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded"
                  >
                    Reserve Book
                  </button>
                )}
                
                {!book.available && (
                  <button
                    onClick={handleJoinWaitingList}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded"
                  >
                    Join Waiting List
                  </button>
                )}
                
                <button
                  onClick={handleReturn}
                  className="bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded"
                >
                  Return Book
                </button>
              </>
            )}
          </div>
        </div>

        {/* Write a Review Section */}
        {user && (
          <div className="bg-white border border-gray-300 mb-6">
            <div className="border-b border-gray-300 px-6 py-4">
              <h2 className="text-2xl font-bold text-gray-900">Write a Review</h2>
            </div>
            
            {showReviewForm ? (
              <form onSubmit={handleSubmitReview} className="px-6 py-4">
                <div className="mb-4">
                  <label className="block font-semibold text-gray-700 mb-2">
                    Rating (1-5 stars):
                  </label>
                  <select
                    value={newReview.rating}
                    onChange={(e) => setNewReview({ ...newReview, rating: Number(e.target.value) })}
                    className="w-full px-3 py-2 border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="">Select Rating</option>
                    <option value="1">1 Star</option>
                    <option value="2">2 Stars</option>
                    <option value="3">3 Stars</option>
                    <option value="4">4 Stars</option>
                    <option value="5">5 Stars</option>
                  </select>
                </div>

                <div className="mb-4">
                  <label className="block font-semibold text-gray-700 mb-2">
                    Review Comment:
                  </label>
                  <textarea
                    value={newReview.comment}
                    onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                    rows={4}
                    placeholder="Share your thoughts about this book..."
                    required
                  />
                </div>

                <button
                  type="submit"
                  className="bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded"
                >
                  Submit Review
                </button>
              </form>
            ) : (
              <div className="px-6 py-4">
                <button
                  onClick={() => setShowReviewForm(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded"
                >
                  Write a Review
                </button>
              </div>
            )}
          </div>
        )}

        {/* Reviews List Section */}
        <div className="bg-white border border-gray-300">
          <div className="border-b border-gray-300 px-6 py-4 flex items-center gap-2">
            <h2 className="text-2xl font-bold text-gray-900">Reviews</h2>
            <span className="bg-gray-500 text-white text-sm font-medium px-2 py-1 rounded">
              {reviews.length}
            </span>
          </div>
          
          <div className="px-6 py-4">
            {reviews.length > 0 ? (
              <div className="space-y-4">
                {reviews.map((review) => (
                  <div key={review.id} className="border-b border-gray-200 pb-4 last:border-0">
                    <div className="flex justify-between items-start mb-2">
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <span className="font-semibold text-gray-900">{review.username}</span>
                          <div className="flex">
                            {[...Array(5)].map((_, i) => (
                              <span
                                key={i}
                                className={`text-lg ${
                                  i < review.rating ? 'text-yellow-400' : 'text-gray-300'
                                }`}
                              >
                                ★
                              </span>
                            ))}
                          </div>
                        </div>
                        <p className="text-sm text-gray-500">
                          {new Date(review.createdAt).toLocaleDateString()}
                        </p>
                      </div>
                      {(user?.username === review.username || user?.role === 'ADMIN') && (
                        <button
                          onClick={() => handleDeleteReview(review.id)}
                          className="bg-red-600 hover:bg-red-700 text-white text-sm font-medium py-1 px-3 rounded"
                        >
                          Delete
                        </button>
                      )}
                    </div>
                    <p className="text-gray-700">{review.comment}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 py-4">
                No reviews yet. Be the first to review this book!
              </p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetails;

