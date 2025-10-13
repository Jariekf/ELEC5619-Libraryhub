import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { Book } from '../../types';
import apiService from '../../services/api';

const EditBook: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    genre: '',
    available: true
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (id) {
      loadBook();
    }
  }, [id]);

  const loadBook = async () => {
    try {
      setLoading(true);
      const book = await apiService.getBookById(Number(id));
      setFormData({
        title: book.title,
        author: book.author,
        genre: book.genre,
        available: book.available
      });
    } catch (err) {
      setError('Failed to load book');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await apiService.updateBook(Number(id), formData);
      alert('Book updated successfully!');
      navigate(`/books/${id}`);
    } catch (err) {
      alert('Failed to update book');
      console.error(err);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this book? This action cannot be undone.')) {
      return;
    }
    
    try {
      await apiService.deleteBook(Number(id));
      alert('Book deleted successfully!');
      navigate('/books');
    } catch (err) {
      alert('Failed to delete book');
      console.error(err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 mb-4">{error}</p>
          <button onClick={() => navigate('/books')} className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded">
            Back to Books
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Title */}
        <h1 className="text-4xl font-bold text-gray-900 mb-8 flex items-center gap-2">
          ✏️ Edit Book
        </h1>

        {/* Edit Form */}
        <div className="bg-white border border-gray-300 rounded-lg p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Title Field */}
            <div>
              <label htmlFor="title" className="block text-lg font-semibold text-gray-900 mb-2">
                Title
              </label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleChange}
                className="w-full px-4 py-3 text-lg border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            {/* Author Field */}
            <div>
              <label htmlFor="author" className="block text-lg font-semibold text-gray-900 mb-2">
                Author
              </label>
              <input
                type="text"
                id="author"
                name="author"
                value={formData.author}
                onChange={handleChange}
                className="w-full px-4 py-3 text-lg border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            {/* Genre Field */}
            <div>
              <label htmlFor="genre" className="block text-lg font-semibold text-gray-900 mb-2">
                Genre
              </label>
              <input
                type="text"
                id="genre"
                name="genre"
                value={formData.genre}
                onChange={handleChange}
                className="w-full px-4 py-3 text-lg border border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            {/* Available Checkbox */}
            <div className="flex items-center gap-3">
              <input
                type="checkbox"
                id="available"
                name="available"
                checked={formData.available}
                onChange={handleChange}
                className="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label htmlFor="available" className="text-lg font-medium text-gray-900">
                Available for reservation
              </label>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-3 pt-4">
              <button
                type="submit"
                className="bg-blue-600 hover:bg-blue-700 text-white text-lg font-medium py-3 px-6 rounded"
              >
                Update Book
              </button>
              <button
                type="button"
                onClick={() => navigate(`/books/${id}`)}
                className="bg-gray-500 hover:bg-gray-600 text-white text-lg font-medium py-3 px-6 rounded"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleDelete}
                className="bg-red-600 hover:bg-red-700 text-white text-lg font-medium py-3 px-6 rounded"
              >
                Delete Book
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditBook;

