import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { Book, WaitingList } from '../../types';
import { apiService } from '../../services/api';
import { ArrowLeft, Loader2, Users, Trash2 } from 'lucide-react';

const ManageWaitingList: React.FC = () => {
  const navigate = useNavigate();
  const [books, setBooks] = useState<Book[]>([]);
  const [waitingEntries, setWaitingEntries] = useState<WaitingList[]>([]);
  const [selectedBookId, setSelectedBookId] = useState<number | null>(null);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const [loadingEntries, setLoadingEntries] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    const loadBooks = async () => {
      try {
        setLoadingBooks(true);
        const data = await apiService.getBooks();
        setBooks(data);
        if (data.length > 0) {
          setSelectedBookId(data[0].id);
        }
      } catch (err: any) {
        setError(err?.message || 'Failed to load books.');
      } finally {
        setLoadingBooks(false);
      }
    };
    loadBooks();
  }, []);

  useEffect(() => {
    if (selectedBookId !== null) {
      fetchWaitingList(selectedBookId);
    } else {
      setWaitingEntries([]);
    }
  }, [selectedBookId]);

  const fetchWaitingList = async (bookId: number) => {
    try {
      setLoadingEntries(true);
      setError(null);
      const entries = await apiService.getWaitingListForBook(bookId);
      setWaitingEntries(entries);
    } catch (err: any) {
      setError(err?.message || 'Failed to load waiting list.');
      setWaitingEntries([]);
    } finally {
      setLoadingEntries(false);
    }
  };

  const handleDelete = async (entry: WaitingList) => {
    if (!entry.id) {
      return;
    }
    const confirmed = window.confirm(`Remove ${entry.username ?? 'this user'} from the waiting list?`);
    if (!confirmed) {
      return;
    }
    try {
      setSuccess(null);
      await apiService.deleteWaitingEntry(entry.id);
      setSuccess('Waiting list entry removed.');
      if (selectedBookId !== null) {
        fetchWaitingList(selectedBookId);
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || 'Failed to remove entry.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-6">
        <button
          onClick={() => navigate('/admin/dashboard')}
          className="inline-flex items-center gap-2 text-blue-600 hover:text-blue-800 text-sm font-medium"
        >
          <ArrowLeft size={18} />
          Back to Admin Dashboard
        </button>

        <div className="mt-6 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Manage Waiting Lists</h1>
            <p className="text-gray-600 mt-2">
              Inspect waiting list entries for each book and remove users when necessary.
            </p>
          </div>
          <div className="hidden md:flex items-center gap-2 text-gray-500">
            <Users size={18} />
            <span className="text-sm">
              {waitingEntries.length} entr{waitingEntries.length === 1 ? 'y' : 'ies'} selected
            </span>
          </div>
        </div>

        <div className="mt-6 grid grid-cols-1 lg:grid-cols-[320px_1fr] gap-6">
          <section className="bg-white rounded-lg shadow border border-gray-200 p-4 h-fit">
            <h2 className="text-sm font-semibold text-gray-700 mb-3">Books</h2>
            {loadingBooks ? (
              <div className="flex items-center justify-center py-10 text-gray-500">
                <Loader2 className="animate-spin" size={22} />
                <span className="ml-2 text-sm">Loading books...</span>
              </div>
            ) : (
              <div className="space-y-2 max-h-[500px] overflow-y-auto pr-1">
                {books.map((book) => (
                  <button
                    key={book.id}
                    onClick={() => setSelectedBookId(book.id)}
                    className={`w-full text-left rounded-lg border px-3 py-2 transition ${
                      selectedBookId === book.id
                        ? 'border-blue-500 bg-blue-50 text-blue-700'
                        : 'border-gray-200 hover:bg-gray-50 text-gray-700'
                    }`}
                  >
                    <p className="text-sm font-medium">{book.title}</p>
                    <p className="text-xs text-gray-500 truncate">{book.author}</p>
                  </button>
                ))}
                {books.length === 0 && (
                  <p className="text-sm text-gray-500 text-center py-8">No books available.</p>
                )}
              </div>
            )}
          </section>

          <section className="bg-white rounded-lg shadow border border-gray-200 p-6">
            <div className="flex justify-between items-center mb-4">
              <div>
                <h2 className="text-xl font-semibold text-gray-900">Waiting List</h2>
                <p className="text-sm text-gray-500">
                  {books.find((b) => b.id === selectedBookId)?.title ?? 'Select a book to view entries.'}
                </p>
              </div>
            </div>

            {error && (
              <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}
            {success && (
              <div className="mb-4 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                {success}
              </div>
            )}

            {loadingEntries ? (
              <div className="flex items-center justify-center py-16 text-gray-500">
                <Loader2 className="animate-spin" size={24} />
                <span className="ml-2 text-sm">Loading waiting list...</span>
              </div>
            ) : waitingEntries.length === 0 ? (
              <div className="py-16 text-center text-sm text-gray-500">
                No waiting list entries for this book.
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        User
                      </th>
                      <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Joined
                      </th>
                      <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {waitingEntries.map((entry) => (
                      <tr key={entry.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3 text-sm text-gray-800">
                          <div className="font-medium">{entry.username ?? 'Unknown user'}</div>
                          {entry.userId && (
                            <div className="text-xs text-gray-500">ID: {entry.userId}</div>
                          )}
                        </td>
                        <td className="px-4 py-3 text-sm text-gray-600">
                          {entry.joinedDate
                            ? new Date(entry.joinedDate).toLocaleString()
                            : 'Unknown'}
                        </td>
                        <td className="px-4 py-3 text-right text-sm">
                          <button
                            onClick={() => handleDelete(entry)}
                            className="inline-flex items-center gap-1 rounded-lg border border-red-200 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50"
                          >
                            <Trash2 size={16} />
                            Remove
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      </div>
    </div>
  );
};

export default ManageWaitingList;
