import React, { useEffect, useState } from 'react';
import apiService from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import type { UserProfile, UpdateProfileRequest } from '../../types';

const ProfilePage: React.FC = () => {
  const { updateProfile } = useAuth();

  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await apiService.getProfile();
        setProfile(data);
        setForm((prev) => ({
          ...prev,
          username: data.username,
          email: data.email,
          password: '',
          confirmPassword: '',
        }));
      } catch (err) {
        setError('Failed to load profile. Please try again later.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    if (!profile) {
      setError('Unable to load the current profile.');
      return;
    }

    if (form.password && form.password !== form.confirmPassword) {
      setError('The passwords do not match.');
      return;
    }

    const payload: UpdateProfileRequest = {};
    if (form.username && form.username.trim() !== profile.username) {
      payload.username = form.username.trim();
    }
    if (form.email && form.email.trim() !== profile.email) {
      payload.email = form.email.trim();
    }
    if (form.password) {
      payload.password = form.password;
    }

    if (Object.keys(payload).length === 0) {
      setError('No changes detected.');
      return;
    }

    try {
      setIsSaving(true);
      const updatedProfile = await updateProfile(payload);
      setProfile(updatedProfile);
      setForm((prev) => ({
        ...prev,
        username: updatedProfile.username,
        email: updatedProfile.email,
        password: '',
        confirmPassword: '',
      }));
      setSuccess('Profile updated successfully.');
    } catch (err: any) {
      const message = err?.response?.data?.error ?? err?.message ?? 'Update failed.';
      setError(message);
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-library-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white shadow-md rounded-lg p-6">
          <h1 className="text-2xl font-semibold text-gray-900 mb-6">Profile Settings</h1>

          {error && (
            <div className="mb-4 rounded-md bg-red-50 p-4 text-red-700 text-sm">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 rounded-md bg-green-50 p-4 text-green-700 text-sm">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700">
                Username
              </label>
              <input
                id="username"
                name="username"
                type="text"
                value={form.username}
                onChange={handleChange}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-library-500 focus:ring-library-500"
              />
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                Email
              </label>
              <input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-library-500 focus:ring-library-500"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                  New Password (optional)
                </label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="Leave blank to keep current password"
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-library-500 focus:ring-library-500"
                />
              </div>
              <div>
                <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                  Confirm Password
                </label>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  placeholder="Re-enter the new password"
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-library-500 focus:ring-library-500"
                />
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3">
              <button
                type="submit"
                disabled={isSaving}
                className="inline-flex justify-center rounded-md border border-transparent bg-library-600 py-2 px-4 text-sm font-medium text-white shadow-sm hover:bg-library-700 focus:outline-none focus:ring-2 focus:ring-library-500 focus:ring-offset-2 disabled:opacity-60"
              >
                {isSaving ? 'Saving...' : 'Save changes'}
              </button>
            </div>
          </form>

          {profile && (
            <div className="mt-8 text-sm text-gray-500">
              <p>Account created at: {new Date(profile.createdAt).toLocaleString()}</p>
              <p>Account status: {profile.active ? 'Active' : 'Inactive'}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
