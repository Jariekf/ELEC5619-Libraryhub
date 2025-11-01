import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { User, UserProfile, LoginRequest, RegisterRequest, UpdateProfileRequest } from '../types';
import apiService from '../services/api';

interface AuthContextType {
    user: User | null;
    login: (credentials: LoginRequest) => Promise<void>;
    register: (userData: RegisterRequest) => Promise<void>;
    logout: () => Promise<void>;
    updateProfile: (data: UpdateProfileRequest) => Promise<UserProfile>;
    isLoading: boolean;
    isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const isAuthenticated = !!user;

    useEffect(() => {
        checkAuthStatus();
    }, []);

    const checkAuthStatus = async () => {
        console.log('=== Checking auth status ===');
        try {
            const currentUser = await apiService.getCurrentUser();
            console.log('API response:', currentUser);

            // Validate that we got actual user data (not HTML or invalid data)
            if (currentUser &&
                typeof currentUser === 'object' &&
                currentUser.username &&
                currentUser.id &&
                currentUser.email) {
                console.log('✅ Valid user data, authenticated');
                setUser(currentUser);
            } else {
                console.log('❌ Invalid user data format');
                setUser(null);
            }
        } catch (error) {
            console.log('❌ Auth check failed:', error);
            setUser(null);
        } finally {
            setIsLoading(false);
            console.log('=== Auth check complete ===');
        }
    };

    const login = async (credentials: LoginRequest) => {
        try {
            await apiService.login(credentials);
            const currentUser = await apiService.getCurrentUser();

            // Validate user data after login
            if (currentUser && currentUser.username && currentUser.id) {
                setUser(currentUser);
            } else {
                throw new Error('Invalid user data received');
            }
        } catch (error) {
            setUser(null);
            throw new Error('Login failed! Please check username and password.');
        }
    };

    const register = async (userData: RegisterRequest) => {
        try {
            await apiService.register(userData);
            await checkAuthStatus();
        } catch (error) {
            throw new Error('Registration failed. Please try again.');
        }
    };

    const logout = async () => {
        try {
            await apiService.logout();
            setUser(null);
        } catch (error) {
            console.error('Logout error:', error);
            setUser(null);
        }
    };

    const updateProfile = async (data: UpdateProfileRequest): Promise<UserProfile> => {
        const updated = await apiService.updateProfile(data);
        const sanitizedUser: User = {
            id: updated.id,
            username: updated.username,
            email: updated.email,
            role: updated.role,
        };
        setUser(sanitizedUser);
        return updated;
    };

    const value: AuthContextType = {
        user,
        login,
        register,
        logout,
        updateProfile,
        isLoading,
        isAuthenticated,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};