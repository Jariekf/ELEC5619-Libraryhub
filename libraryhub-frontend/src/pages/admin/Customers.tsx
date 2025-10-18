import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../../services/api';
import type {
  AdminManagedUser,
  AdminCreateUserPayload,
  AdminUpdateUserPayload
} from '../../types';
import { Plus, Edit, Trash2, X, ArrowLeft } from 'lucide-react';

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const ManageUsers: React.FC = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState<AdminManagedUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<AdminManagedUser | null>(null);

  const [createForm, setCreateForm] = useState<AdminCreateUserPayload>({
    username: '',
    email: '',
    password: '',
    role: 'CUSTOMER',
    active: true,
  });

  const [editForm, setEditForm] = useState<AdminUpdateUserPayload>({
    username: '',
    email: '',
    password: '',
    role: 'CUSTOMER',
    active: true,
  });

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await apiService.getAdminUsers();
      setUsers(data);
    } catch (err: any) {
      setError(err?.message || 'Failed to load users.');
    } finally {
      setLoading(false);
    }
  };

  const resetFeedback = () => {
    setError(null);
    setSuccess(null);
  };

  const openCreateModal = () => {
    resetFeedback();
    setCreateForm({
      username: '',
      email: '',
      password: '',
      role: 'CUSTOMER',
      active: true,
    });
    setShowCreateModal(true);
  };

  const openEditModal = (user: AdminManagedUser) => {
    resetFeedback();
    setSelectedUser(user);
    setEditForm({
      username: user.username,
      email: user.email,
      password: '',
      role: user.role,
      active: user.active,
    });
    setShowEditModal(true);
  };

  const handleCreateChange = (event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type, checked } = event.target;
    setCreateForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleEditChange = (event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type, checked } = event.target;
    setEditForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleCreateSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    resetFeedback();

    const payload: AdminCreateUserPayload = {
      username: createForm.username.trim(),
      email: createForm.email.trim(),
      password: createForm.password,
      role: createForm.role,
      active: createForm.active,
    };

    if (!payload.username) {
      setError('Username is required.');
      return;
    }
    if (!emailRegex.test(payload.email)) {
      setError('Invalid email address.');
      return;
    }
    if (!payload.password || payload.password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    try {
      await apiService.createAdminUser(payload);
      setSuccess('User created successfully.');
      setShowCreateModal(false);
      await loadUsers();
    } catch (err: any) {
      setError(err?.response?.data?.error || err?.message || 'Failed to create user.');
    }
  };

  const handleEditSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    resetFeedback();

    if (!selectedUser) {
      setError('No user selected.');
      return;
    }

    const username = editForm.username?.trim();
    const email = editForm.email?.trim();

    if (!username) {
      setError('Username cannot be empty.');
      return;
    }
    if (!emailRegex.test(email || '')) {
      setError('Invalid email address.');
      return;
    }

    const payload: AdminUpdateUserPayload = {
      username,
      email,
      role: editForm.role,
      active: editForm.active,
    };

    if (editForm.password && editForm.password.trim().length > 0) {
      if (editForm.password.trim().length < 6) {
        setError('Password must be at least 6 characters long.');
        return;
      }
      payload.password = editForm.password.trim();
    }

    try {
      await apiService.updateAdminUser(selectedUser.id, payload);
      setSuccess('User updated successfully.');
      setShowEditModal(false);
      await loadUsers();
    } catch (err: any) {
      setError(err?.response?.data?.error || err?.message || 'Failed to update user.');
    }
  };

  const handleDelete = async (user: AdminManagedUser) => {
    resetFeedback();
    const confirmed = window.confirm(`Delete user "${user.username}"? This action cannot be undone.`);
    if (!confirmed) {
      return;
    }
    try {
      await apiService.deleteAdminUser(user.id);
      setSuccess('User deleted successfully.');
      await loadUsers();
    } catch (err: any) {
      setError(err?.response?.data?.error || err?.message || 'Failed to delete user.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="flex items-center gap-2 text-blue-600 hover:text-blue-800 cursor-pointer" onClick={() => navigate('/admin/dashboard')}>
          <ArrowLeft size={18} />
          <span className="text-sm font-medium">Back to Admin Dashboard</span>
        </div>

        <div className="mt-6 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Manage Users</h1>
            <p className="text-gray-600 mt-2">Create, update, or remove user accounts and assign roles.</p>
          </div>
          <button
            onClick={openCreateModal}
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow hover:bg-blue-700"
          >
            <Plus size={18} /> Add User
          </button>
        </div>

        {error && (
          <div className="mt-6 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
            {error}
          </div>
        )}
        {success && (
          <div className="mt-6 rounded-lg border border-green-200 bg-green-50 p-4 text-sm text-green-800">
            {success}
          </div>
        )}

        {loading ? (
          <div className="mt-12 flex justify-center">
            <div className="h-12 w-12 animate-spin rounded-full border-4 border-blue-600 border-t-transparent"></div>
          </div>
        ) : (
          <div className="mt-6 overflow-x-auto rounded-lg border border-gray-200 bg-white shadow-sm">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Username</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Email</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Role</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Status</th>
                  <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">Created</th>
                  <th className="px-4 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-500">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {users.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-6 text-center text-sm text-gray-600">
                      No users found.
                    </td>
                  </tr>
                ) : (
                  users.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{user.username}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">{user.email}</td>
                      <td className="px-4 py-3 text-sm">
                        <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'}`}>
                          {user.role}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm">
                        <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${user.active ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-700'}`}>
                          {user.active ? 'Active' : 'Disabled'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600">
                        {new Date(user.createdAt).toLocaleString()}
                      </td>
                      <td className="px-4 py-3 text-right text-sm">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => openEditModal(user)}
                            className="inline-flex items-center gap-1 rounded-lg border border-gray-200 px-3 py-1.5 text-xs font-medium text-gray-700 hover:bg-gray-100"
                          >
                            <Edit size={16} /> Edit
                          </button>
                          <button
                            onClick={() => handleDelete(user)}
                            className="inline-flex items-center gap-1 rounded-lg border border-red-200 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50"
                          >
                            <Trash2 size={16} /> Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showCreateModal && (
        <Modal title="Add User" onClose={() => setShowCreateModal(false)}>
          <form className="space-y-4" onSubmit={handleCreateSubmit}>
            <InputField
              label="Username"
              name="username"
              value={createForm.username}
              onChange={handleCreateChange}
              required
            />
            <InputField
              label="Email"
              name="email"
              type="email"
              value={createForm.email}
              onChange={handleCreateChange}
              required
            />
            <InputField
              label="Password"
              name="password"
              type="password"
              value={createForm.password}
              onChange={handleCreateChange}
              required
            />
            <SelectField
              label="Role"
              name="role"
              value={createForm.role}
              onChange={handleCreateChange}
              options={[
                { value: 'CUSTOMER', label: 'Customer' },
                { value: 'ADMIN', label: 'Administrator' },
              ]}
            />
            <CheckboxField
              label="Account is active"
              name="active"
              checked={createForm.active}
              onChange={handleCreateChange}
            />
            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100"
                onClick={() => setShowCreateModal(false)}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
              >
                Save
              </button>
            </div>
          </form>
        </Modal>
      )}

      {showEditModal && selectedUser && (
        <Modal title={`Edit ${selectedUser.username}`} onClose={() => setShowEditModal(false)}>
          <form className="space-y-4" onSubmit={handleEditSubmit}>
            <InputField
              label="Username"
              name="username"
              value={editForm.username || ''}
              onChange={handleEditChange}
              required
            />
            <InputField
              label="Email"
              name="email"
              type="email"
              value={editForm.email || ''}
              onChange={handleEditChange}
              required
            />
            <InputField
              label="Password (leave blank to keep current)"
              name="password"
              type="password"
              value={editForm.password || ''}
              onChange={handleEditChange}
              placeholder="Enter a new password"
            />
            <SelectField
              label="Role"
              name="role"
              value={editForm.role || 'CUSTOMER'}
              onChange={handleEditChange}
              options={[
                { value: 'CUSTOMER', label: 'Customer' },
                { value: 'ADMIN', label: 'Administrator' },
              ]}
            />
            <CheckboxField
              label="Account is active"
              name="active"
              checked={Boolean(editForm.active)}
              onChange={handleEditChange}
            />
            <div className="flex justify-end gap-3 pt-2">
              <button
                type="button"
                className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100"
                onClick={() => setShowEditModal(false)}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
              >
                Update
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
};

interface ModalProps {
  title: string;
  onClose: () => void;
  children: React.ReactNode;
}

const Modal: React.FC<ModalProps> = ({ title, onClose, children }) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
    <div className="w-full max-w-lg rounded-xl bg-white shadow-xl">
      <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
        <button
          onClick={onClose}
          className="rounded-full p-1 text-gray-500 hover:bg-gray-100"
          aria-label="Close modal"
        >
          <X size={18} />
        </button>
      </div>
      <div className="px-6 py-5">{children}</div>
    </div>
  </div>
);

interface InputFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  name: string;
}

const InputField: React.FC<InputFieldProps> = ({ label, name, ...props }) => (
  <div>
    <label htmlFor={name} className="block text-sm font-medium text-gray-700">
      {label}
    </label>
    <input
      id={name}
      name={name}
      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
      {...props}
    />
  </div>
);

interface SelectFieldProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  name: string;
  options: { value: 'CUSTOMER' | 'ADMIN'; label: string }[];
}

const SelectField: React.FC<SelectFieldProps> = ({ label, name, options, ...props }) => (
  <div>
    <label htmlFor={name} className="block text-sm font-medium text-gray-700">
      {label}
    </label>
    <select
      id={name}
      name={name}
      className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
      {...props}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  </div>
);

interface CheckboxFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  name: string;
}

const CheckboxField: React.FC<CheckboxFieldProps> = ({ label, name, ...props }) => (
  <label className="inline-flex items-center gap-2 text-sm text-gray-700">
    <input
      type="checkbox"
      name={name}
      className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
      {...props}
    />
    {label}
  </label>
);

export default ManageUsers;
