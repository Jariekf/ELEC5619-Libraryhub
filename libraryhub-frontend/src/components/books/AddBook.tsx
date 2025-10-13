import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '../../services/api';

const AddBook: React.FC = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    genre: '',
    available: true
  });
  
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      await apiService.addBook(formData);
      alert('Book added successfully!');
      navigate('/books');
    } catch (err) {
      setError('Failed to add book');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Title */}
        <h1 className="text-4xl font-bold text-gray-900 mb-8 flex items-center gap-2">
          ➕ Add New Book
        </h1>

        {/* Add Form */}
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
                placeholder="Enter book title"
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
                placeholder="Enter author name"
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
                placeholder="Enter genre (e.g., Fiction, Science, Mystery)"
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

            {/* Error Message */}
            {error && (
              <div className="rounded-md bg-red-50 p-4">
                <div className="text-sm text-red-700">{error}</div>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex gap-3 pt-4">
              <button
                type="submit"
                disabled={isLoading}
                className="bg-blue-600 hover:bg-blue-700 text-white text-lg font-medium py-3 px-6 rounded disabled:opacity-50"
              >
                {isLoading ? 'Saving...' : 'Save Book'}
              </button>
              <button
                type="button"
                onClick={() => navigate('/books')}
                className="bg-gray-500 hover:bg-gray-600 text-white text-lg font-medium py-3 px-6 rounded"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddBook;

