import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, BookOpen, MessageSquare, Calendar } from 'lucide-react';

const Dashboard: React.FC = () => {
    const navigate = useNavigate();

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

                {/* Dashboard Content */}
                <h2 className="text-2xl font-semibold mb-6">Welcome to the Admin Panel</h2>
                <div className="space-y-3">
                    <button
                        onClick={() => navigate('/admin/reservations')}
                        className="w-full p-6 bg-white rounded-lg shadow hover:shadow-md transition text-left border border-gray-200 hover:border-blue-400"
                    >
                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-blue-100 rounded-lg">
                                <Calendar className="text-blue-600" size={24} />
                            </div>
                            <div>
                                <h5 className="text-lg font-semibold mb-2 text-gray-900">View All Reservations</h5>
                                <p className="text-gray-600">See a complete list of all books that are currently reserved.</p>
                            </div>
                        </div>
                    </button>

                    <button
                        onClick={() => navigate('/admin/customers')}
                        className="w-full p-6 bg-white rounded-lg shadow hover:shadow-md transition text-left border border-gray-200 hover:border-blue-400"
                    >
                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-green-100 rounded-lg">
                                <Users className="text-green-600" size={24} />
                            </div>
                            <div>
                                <h5 className="text-lg font-semibold mb-2 text-gray-900">View All Customers</h5>
                                <p className="text-gray-600">Browse through the list of all registered customers in the system.</p>
                            </div>
                        </div>
                    </button>

                    <button
                        onClick={() => navigate('/books')}
                        className="w-full p-6 bg-white rounded-lg shadow hover:shadow-md transition text-left border border-gray-200 hover:border-blue-400"
                    >
                        <div className="flex items-start gap-4">
                            <div className="p-3 bg-purple-100 rounded-lg">
                                <BookOpen className="text-purple-600" size={24} />
                            </div>
                            <div>
                                <h5 className="text-lg font-semibold mb-2 text-gray-900">Manage Books</h5>
                                <p className="text-gray-600">Add, edit, or remove books from the library collection.</p>
                            </div>
                        </div>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;