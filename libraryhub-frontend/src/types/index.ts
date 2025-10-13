export interface User {
  id: number;
  username: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface Book {
  id: number;
  title: string;
  author: string;
  genre: string;
  available: boolean;
  isbn?: string;
  publishedYear?: number;
  description?: string;
}

export interface Review {
  id: number;
  bookId: number;
  username: string;
  rating: number;
  comment: string;
  createdAt: string;
}

export interface Reservation {
  id: number;
  book: Book;
  user: User;
  reservedAt: string;
  dueDate?: string;
}

export interface WaitingList {
  id: number;
  bookId: number;
  username: string;
  joinedAt: string;
  position: number;
}

export interface ChatMessage {
  id: string;
  message: string;
  response: string;
  user: string;
  timestamp: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}

