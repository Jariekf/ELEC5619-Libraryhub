import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronDown, ChevronUp, BookOpen, MessageSquare, ArrowLeft } from 'lucide-react';
import { apiService } from '../../services/api';
import type { Reservation } from '../../types';

const Reservations: React.FC = () => {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [expandedIndex, setExpandedIndex] = useState<number | null>(null);

    useEffect(() => {
        loadReservations();
    }, []);

    const loadReservations = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await apiService.getAllReservations();
            console.log('Loaded reservations:', data);
            setReservations(data);
        } catch (err: any) {
            console.error('Failed to load reservations:', err);
            setError(err.message || 'Failed to load reservations');
        } finally {
            setLoading(false);
        }
    };

    const toggleExpand = (index: number) => {
        setExpandedIndex(expandedIndex === index ? null : index);
    };

    const formatDate = (dateString: string) => {
        try {
            return new Date(dateString).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
            });
        } catch {
            return dateString;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 py-6">
                {/* Header */}
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Admin Panel</h1>
                    <div className="flex gap-3">
                        <button
                            onClick={() => navigate('/books')}
                            className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition flex items-center gap-2"
                        >
                            <BookOpen size={18} />
                            Books
                        </button>
                        <button
                            onClick={() => navigate('/chatbot')}
                            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition flex items-center gap-2"
                        >
                            <MessageSquare size={18} />
                            AI Assistant
                        </button>
                    </div>
                </div>

                {/* Back Button */}
                <button
                    onClick={() => navigate('/admin/dashboard')}
                    className="mb-6 flex items-center gap-2 text-blue-600 hover:text-blue-800 transition"
                >
                    <ArrowLeft size={20} />
                    Back to Admin Panel
                </button>

                {/* Content */}
                <h2 className="text-2xl font-semibold mb-6">All Reservations</h2>

                {loading && (
                    <div className="flex justify-center items-center py-12">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
                    </div>
                )}

                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                        <p className="text-red-800">{error}</p>
                        <button
                            onClick={loadReservations}
                            className="mt-2 text-red-600 hover:text-red-800 underline"
                        >
                            Try again
                        </button>
                    </div>
                )}

                {!loading && !error && reservations.length === 0 && (
                    <div className="bg-gray-50 border border-gray-200 rounded-lg p-8 text-center">
                        <p className="text-gray-600">No reservations found in the system.</p>
                    </div>
                )}

                {!loading && !error && reservations.length > 0 && (
                    <div className="space-y-3">
                        {reservations.map((reservation, index) => (
                            <div
                                key={reservation.id}
                                className="bg-white border border-gray-200 rounded-lg overflow-hidden"
                            >
                                <button
                                    onClick={() => toggleExpand(index)}
                                    className="w-full p-4 flex justify-between items-center hover:bg-gray-50 transition"
                                >
                                    <div className="text-left">
                                        <h5 className="font-semibold text-gray-900">
                                            {reservation.book?.title || 'Unknown Book'}
                                        </h5>
                                        <p className="text-sm text-gray-600">
                                            Reserved by: {reservation.user?.username || 'Unknown User'}
                                        </p>
                                    </div>
                                    {expandedIndex === index ? (
                                        <ChevronUp className="text-gray-500" size={20} />
                                    ) : (
                                        <ChevronDown className="text-gray-500" size={20} />
                                    )}
                                </button>

                                {expandedIndex === index && (
                                    <div className="p-4 bg-gray-50 border-t border-gray-200">
                                        <div className="space-y-3">
                                            <div>
                                                <span className="font-medium text-gray-700">Reservation ID:</span>
                                                <span className="ml-2 text-gray-900">{reservation.id}</span>
                                            </div>

                                            <div className="border-t border-gray-200 pt-3">
                                                <h6 className="font-medium text-gray-700 mb-2">Book Details:</h6>
                                                <div className="ml-4 space-y-1">
                                                    <div>
                                                        <span className="text-gray-600">Title:</span>
                                                        <span className="ml-2 text-gray-900">{reservation.book?.title}</span>
                                                    </div>
                                                    <div>
                                                        <span className="text-gray-600">Author:</span>
                                                        <span className="ml-2 text-gray-900">{reservation.book?.author}</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="border-t border-gray-200 pt-3">
                                                <h6 className="font-medium text-gray-700 mb-2">User Details:</h6>
                                                <div className="ml-4 space-y-1">
                                                    <div>
                                                        <span className="text-gray-600">Username:</span>
                                                        <span className="ml-2 text-gray-900">{reservation.user?.username}</span>
                                                    </div>
                                                    <div>
                                                        <span className="text-gray-600">Email:</span>
                                                        <span className="ml-2 text-gray-900">{reservation.user?.email}</span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="border-t border-gray-200 pt-3">
                                                <div>
                                                    <span className="font-medium text-gray-700">Reservation Date:</span>
                                                    <span className="ml-2 text-gray-900">
                            {reservation.reservationDate ? formatDate(reservation.reservationDate) : 'N/A'}
                          </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Reservations;