import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Book } from '../../types';
import BookCard from './BookCard';
import apiService from '../../services/api';

interface BookListProps {
  books?: Book[];
  onBookAction?: (action: string, bookId: number) => void;
  showFilters?: boolean;
}

const BookList: React.FC<BookListProps> = ({ 
  books: initialBooks, 
  onBookAction,
  showFilters = true 
}) => {
  const navigate = useNavigate();
  const [books, setBooks] = useState<Book[]>(Array.isArray(initialBooks) ? initialBooks : []);
  const [loading, setLoading] = useState(!initialBooks);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [error, setError] = useState('');

  const genres = Array.from(new Set((books || []).map(book => book.genre)));

  useEffect(() => {
    if (!initialBooks) {
      loadBooks();
    }
  }, [initialBooks]);

  const loadBooks = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('Loading books...');
      const fetchedBooks = await apiService.getBooks();
      console.log('Fetched books:', fetchedBooks);
      setBooks(Array.isArray(fetchedBooks) ? fetchedBooks : []);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load books';
      setError(`Failed to load books: ${errorMessage}`);
      console.error('Error loading books:', err);
      setBooks([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (query: string) => {
    setSearchQuery(query);
    if (query.trim()) {
      try {
        setLoading(true);
        const searchResults = await apiService.searchBooks(query);
        setBooks(Array.isArray(searchResults) ? searchResults : []);
      } catch (err) {
        setError('Search failed');
        console.error('Search error:', err);
        setBooks([]);
      } finally {
        setLoading(false);
      }
    } else {
      loadBooks();
    }
  };

  const handleEdit = (bookId: number) => {
    navigate(`/books/edit/${bookId}`);
  };

  const handleDelete = async (bookId: number) => {
    if (!window.confirm('Are you sure you want to delete this book?')) {
      return;
    }
    
    try {
      await apiService.deleteBook(bookId);
      alert('Book deleted successfully!');
      loadBooks(); // Reload the book list
    } catch (err) {
      alert('Failed to delete book');
      console.error('Delete error:', err);
    }
  };

  const filteredBooks = (books || []).filter(book => {
    const matchesSearch = book.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         book.author.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesGenre = !selectedGenre || book.genre === selectedGenre;
    return matchesSearch && matchesGenre;
  });

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-library-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600 mb-4">{error}</p>
        <button onClick={loadBooks} className="btn-primary">
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Search and filters */}
      {showFilters && (
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex flex-col sm:flex-row gap-4">
            {/* Search bar */}
            <div className="flex-1 relative">
              <input
                type="text"
                placeholder="Search books by title or author..."
                value={searchQuery}
                onChange={(e) => handleSearch(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>

            {/* Genre filter */}
            <select
              value={selectedGenre}
              onChange={(e) => setSelectedGenre(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All Genres</option>
              {genres.map(genre => (
                <option key={genre} value={genre}>{genre}</option>
              ))}
            </select>
          </div>
        </div>
      )}

      {/* Results count */}
      <div className="flex justify-between items-center">
        <p className="text-gray-600">
          {filteredBooks.length} book{filteredBooks.length !== 1 ? 's' : ''} found
        </p>
        {searchQuery && (
          <button
            onClick={() => {
              setSearchQuery('');
              loadBooks();
            }}
            className="text-library-600 hover:text-library-700 text-sm"
          >
            Clear search
          </button>
        )}
      </div>

      {/* Books grid/list */}
      {filteredBooks.length === 0 ? (
        <div className="text-center py-12">
          <span className="text-6xl text-gray-400 mb-4 block">📚</span>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No books found</h3>
          <p className="text-gray-500">
            {searchQuery ? 'Try adjusting your search terms' : 'No books available at the moment'}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredBooks.map(book => (
            <BookCard
              key={book.id}
              book={book}
              onReserve={(bookId) => onBookAction?.('reserve', bookId)}
              onJoinWaitingList={(bookId) => onBookAction?.('waitlist', bookId)}
              onCancelWaiting={(bookId) => onBookAction?.('cancel-waiting', bookId)}
              onEdit={handleEdit}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default BookList;

