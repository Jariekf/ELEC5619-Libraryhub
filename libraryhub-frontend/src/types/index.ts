export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  reservations?: Reservation[];
}

export interface UserProfile extends User {
  active: boolean;
  createdAt: string;
}

export interface Book {
  id: number;
  title: string;
  author: string;
  genre: string;
  isbn?: string;
  description?: string;
  available: boolean;
  averageRating?: number;
  reviewCount?: number;
}

export interface Review {
  id: number;
  rating: number;
  comment: string;
  username: string;  // Changed: directly stores username
  bookId?: number;   // Changed: directly stores bookId
}

export interface Reservation {
  id: number;
  reservationDate: string;
  book?: Book;
  user?: User;
  bookId?: number;
  userId?: number;
}

export interface WaitingList {
  id: number;
  joinedDate: string;
  position?: number;
  book?: Book;
  user?: User;
  bookId?: number;
  userId?: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  username?: string;
  email?: string;
  password?: string;
}

export interface DashboardStats {
  totalBooks: number;
  totalUsers: number;
  totalReservations: number;
  availableBooks: number;
}

export interface AdminManagedUser {
  id: number;
  username: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  active: boolean;
  createdAt: string;
}

export interface AdminCreateUserPayload {
  username: string;
  email: string;
  password: string;
  role: 'CUSTOMER' | 'ADMIN';
  active: boolean;
}

export interface AdminUpdateUserPayload {
  username?: string;
  email?: string;
  password?: string;
  role?: 'CUSTOMER' | 'ADMIN';
  active?: boolean;
}
