import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit, Trash2 } from 'lucide-react';
import { apiService } from '../../services/api';
import type { Book } from '../../types';

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
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        loadBook();
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
            console.error('Failed to load book:', err);
            setError('Failed to load book');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSaving(true);
        setError(null);
        setSuccess(null);

        try {
            await apiService.updateBook(Number(id), formData);
            setSuccess('Book updated successfully!');
            setTimeout(() => {
                navigate('/books');
            }, 1500);
        } catch (err: any) {
            console.error('Failed to update book:', err);
            setError(err.message || 'Failed to update book. Please try again.');
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async () => {
        try {
            await apiService.deleteBook(Number(id));
            alert('Book deleted successfully');
            navigate('/books');
        } catch (err) {
            console.error('Failed to delete book:', err);
            alert('Failed to delete book');
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8">
            <div className="max-w-2xl mx-auto px-4">
                <h2 className="text-3xl font-bold mb-6 flex items-center gap-2">
                    <Edit size={32} />
                    Edit Book
                </h2>

                {success && (
                    <div className="mb-4 p-4 bg-green-50 border border-green-200 rounded-lg">
                        <p className="text-green-800">{success}</p>
                    </div>
                )}

                {error && (
                    <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg">
                        <p className="text-red-800">{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-lg p-6">
                    <div className="mb-4">
                        <label className="block font-medium mb-2">Title</label>
                        <input
                            type="text"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block font-medium mb-2">Author</label>
                        <input
                            type="text"
                            name="author"
                            value={formData.author}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block font-medium mb-2">Genre</label>
                        <input
                            type="text"
                            name="genre"
                            value={formData.genre}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="mb-6">
                        <label className="flex items-center gap-2">
                            <input
                                type="checkbox"
                                name="available"
                                checked={formData.available}
                                onChange={handleChange}
                                className="w-4 h-4"
                            />
                            <span className="font-medium">Available for reservation</span>
                        </label>
                    </div>

                    <div className="flex gap-2">
                        <button
                            type="submit"
                            disabled={saving}
                            className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:bg-gray-400"
                        >
                            {saving ? 'Updating...' : 'Update Book'}
                        </button>
                        <button
                            type="button"
                            onClick={() => navigate('/books')}
                            className="px-6 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition flex items-center gap-2"
                        >
                            <ArrowLeft size={18} />
                            Cancel
                        </button>
                        <button
                            type="button"
                            onClick={() => setShowDeleteModal(true)}
                            className="px-6 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition flex items-center gap-2"
                        >
                            <Trash2 size={18} />
                            Delete Book
                        </button>
                    </div>
                </form>
            </div>

            {showDeleteModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
                        <h3 className="text-xl font-semibold mb-4">Confirm Book Deletion</h3>
                        <p className="mb-4">Are you sure you want to delete this book?</p>
                        <div className="bg-red-50 border border-red-200 rounded p-4 mb-4">
                            <p className="text-red-800 font-semibold mb-2">Warning: This action will permanently delete:</p>
                            <ul className="text-red-700 list-disc list-inside">
                                <li>The book: <strong>{formData.title}</strong></li>
                                <li>All reviews for this book</li>
                                <li>All reservations for this book</li>
                            </ul>
                            <p className="text-red-800 mt-2">This action cannot be undone.</p>
                        </div>
                        <div className="flex gap-2 justify-end">
                            <button
                                onClick={() => setShowDeleteModal(false)}
                                className="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400 transition"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
                                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
                            >
                                Delete Book
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default EditBook;