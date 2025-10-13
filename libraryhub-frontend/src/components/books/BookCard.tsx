import React from 'react';
import type { Book } from '../../types';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

interface BookCardProps {
  book: Book;
  onReserve?: (bookId: number) => void;
  onJoinWaitingList?: (bookId: number) => void;
  onCancelWaiting?: (bookId: number) => void;
  onEdit?: (bookId: number) => void;
  onDelete?: (bookId: number) => void;
  isReserved?: boolean;
  isInWaitingList?: boolean;
  waitingListPosition?: number;
  averageRating?: number;
  reviewCount?: number;
}

const BookCard: React.FC<BookCardProps> = ({
  book,
  onReserve,
  onJoinWaitingList,
  onCancelWaiting,
  onEdit,
  onDelete,
  isReserved = false,
  isInWaitingList = false,
  waitingListPosition,
  averageRating = 0,
  reviewCount = 0,
}) => {
  const { user } = useAuth();
  const getAvailabilityStatus = () => {
    if (isReserved) {
      return {
        text: 'Reserved by you',
        color: 'text-blue-600',
        bgColor: 'bg-blue-100',
      };
    }
    if (book.available) {
      return {
        text: 'Available',
        color: 'text-green-600',
        bgColor: 'bg-green-100',
      };
    }
    return {
      text: 'Checked out',
      color: 'text-red-600',
      bgColor: 'bg-red-100',
    };
  };

  const status = getAvailabilityStatus();

  return (
    <div className="card hover:shadow-lg transition-shadow duration-200">
      <div className="flex flex-col h-full">
        {/* Book cover placeholder */}
        <div className="w-full h-48 bg-gradient-to-br from-blue-100 to-blue-200 rounded-lg mb-4 flex items-center justify-center">
          <span className="text-4xl text-blue-400">📚</span>
        </div>

        {/* Book info */}
        <div className="flex-1 flex flex-col">
          <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
            {book.title}
          </h3>
          <p className="text-gray-600 mb-2">by {book.author}</p>
          <p className="text-sm text-gray-500 mb-3">{book.genre}</p>

          {/* Rating */}
          {averageRating > 0 && (
            <div className="flex items-center mb-3">
              <div className="flex items-center">
                {[...Array(5)].map((_, i) => (
                  <span
                    key={i}
                    className={`text-lg ${
                      i < Math.floor(averageRating)
                        ? 'text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  >
                    ★
                  </span>
                ))}
              </div>
              <span className="ml-2 text-sm text-gray-600">
                {averageRating.toFixed(1)} ({reviewCount} reviews)
              </span>
            </div>
          )}

          {/* Availability status */}
          <div className="mb-4">
            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${status.bgColor} ${status.color}`}>
              {status.text}
            </span>
            {isInWaitingList && waitingListPosition && (
              <span className="ml-2 text-sm text-gray-600">
                Position #{waitingListPosition} in queue
              </span>
            )}
          </div>

          {/* Action buttons */}
          <div className="mt-auto space-y-2">
            <Link
              to={`/books/${book.id}`}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 text-center block"
            >
              View Details
            </Link>

            {/* Admin buttons */}
            {user?.role === 'ADMIN' && (
              <div className="flex gap-2">
                <button
                  onClick={() => onEdit?.(book.id)}
                  className="flex-1 bg-yellow-500 hover:bg-yellow-600 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center"
                  title="Edit Book"
                >
                  ✏️ Edit
                </button>
                <button
                  onClick={() => onDelete?.(book.id)}
                  className="flex-1 bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200 flex items-center justify-center"
                  title="Delete Book"
                >
                  🗑️ Delete
                </button>
              </div>
            )}

            {/* Customer buttons */}
            {user?.role === 'CUSTOMER' && (
              <>
                {book.available && !isReserved && (
                  <button
                    onClick={() => onReserve?.(book.id)}
                    className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
                  >
                    Reserve Book
                  </button>
                )}

                {!book.available && !isInWaitingList && (
                  <button
                    onClick={() => onJoinWaitingList?.(book.id)}
                    className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded-lg transition-colors duration-200"
                  >
                    Join Waiting List
                  </button>
                )}

                {isInWaitingList && (
                  <button
                    onClick={() => onCancelWaiting?.(book.id)}
                    className="w-full bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
                  >
                    Cancel Waiting
                  </button>
                )}

                {isReserved && (
                  <div className="text-center text-sm text-blue-600">
                    ⏰ Reserved
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookCard;

