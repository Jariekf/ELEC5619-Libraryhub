import axios from 'axios';
import type { AxiosInstance } from 'axios';
import type {
  Book,
  User,
  UserProfile,
  Review,
  Reservation,
  WaitingList,
  LoginRequest,
  RegisterRequest,
  UpdateProfileRequest,
  DashboardStats,
  AdminManagedUser,
  AdminCreateUserPayload,
  AdminUpdateUserPayload
} from '../types';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080',
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.api.interceptors.request.use(
        (config) => {
          return config;
        },
        (error) => {
          return Promise.reject(error);
        }
    );

    this.api.interceptors.response.use(
        (response) => response,
        (error) => {
          return Promise.reject(error);
        }
    );
  }

  // Auth APIs
  async login(credentials: LoginRequest): Promise<void> {
    const formData = new FormData();
    formData.append('username', credentials.username);
    formData.append('password', credentials.password);

    await this.api.post('/auth/login', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
  }

  async register(userData: RegisterRequest): Promise<void> {
    const formData = new FormData();
    formData.append('username', userData.username);
    formData.append('email', userData.email);
    formData.append('password', userData.password);

    await this.api.post('/auth/register', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
  }

  async logout(): Promise<void> {
    await this.api.post('/auth/logout');
  }

  async getCurrentUser(): Promise<User> {
    const response = await this.api.get('/api/user/current');
    return response.data;
  }

  async getProfile(): Promise<UserProfile> {
    const response = await this.api.get('/api/user/profile');
    return response.data;
  }

  async updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
    const payload: UpdateProfileRequest = {};
    if (data.username && data.username.trim()) {
      payload.username = data.username.trim();
    }
    if (data.email && data.email.trim()) {
      payload.email = data.email.trim();
    }
    if (data.password && data.password.trim()) {
      payload.password = data.password;
    }

    const response = await this.api.put('/api/user/profile', payload);
    return response.data;
  }

  async updateCurrentUser(update: { email?: string; password?: string }) {
    return this.api.put('/api/user/current', update);
  }

  // Book APIs
  async getBooks(): Promise<Book[]> {
    const response = await this.api.get('/api/books');
    return Array.isArray(response.data) ? response.data : response.data.books || [];
  }

  async getBookById(id: number): Promise<Book> {
    const response = await this.api.get(`/api/books/${id}`);
    return response.data;
  }

  async addBook(book: Omit<Book, 'id'>): Promise<Book> {
    const params = new URLSearchParams();
    params.append('title', book.title);
    params.append('author', book.author);
    params.append('genre', book.genre);
    if (book.isbn) params.append('isbn', book.isbn);
    if (book.description) params.append('description', book.description);
    params.append('available', book.available.toString());

    const response = await this.api.post('/api/books/add', params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    return response.data;
  }

  async updateBook(id: number, book: Partial<Book>): Promise<Book> {
    const params = new URLSearchParams();
    if (book.title) params.append('title', book.title);
    if (book.author) params.append('author', book.author);
    if (book.genre) params.append('genre', book.genre);
    if (book.isbn) params.append('isbn', book.isbn);
    if (book.description) params.append('description', book.description);
    if (book.available !== undefined) params.append('available', book.available.toString());

    const response = await this.api.post(`/api/books/${id}/edit`, params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    return response.data;
  }

  async deleteBook(id: number): Promise<void> {
    await this.api.post(`/api/books/${id}/delete`);
  }

  async searchBooks(query: string): Promise<Book[]> {
    const response = await this.api.post('/api/books/search', null, {
      params: { title: query },
    });
    return Array.isArray(response.data) ? response.data : response.data.books || [];
  }

  // Review APIs
  async getReviewsForBook(bookId: number): Promise<Review[]> {
    const response = await this.api.get(`/api/books/${bookId}/reviews`);
    return Array.isArray(response.data) ? response.data : response.data.reviews || [];
  }

  async submitReview(bookId: number, rating: number, comment: string): Promise<Review> {
    const params = new URLSearchParams();
    params.append('rating', String(rating));
    params.append('comment', comment);

    const response = await this.api.post(`/api/books/${bookId}/reviews`, params, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
    return response.data;
  }

  async deleteReview(reviewId: number): Promise<void> {
    await this.api.delete(`/api/reviews/${reviewId}`);
  }

  // Reservation APIs
  async reserveBook(bookId: number): Promise<Reservation> {
    const response = await this.api.post(`/api/books/${bookId}/reserve`);
    return response.data;
  }

  async cancelReservation(bookId: number): Promise<void> {
    await this.api.delete(`/api/books/${bookId}/reserve`);
  }

  async getUserReservations(): Promise<Reservation[]> {
    const response = await this.api.get('/api/user/reservations');
    return Array.isArray(response.data) ? response.data : response.data.reservations || [];
  }

  // Waiting List APIs
  async joinWaitingList(bookId: number): Promise<WaitingList> {
    const response = await this.api.post(`/api/books/${bookId}/waitinglist`);
    return response.data;
  }

  async cancelWaiting(bookId: number): Promise<void> {
    await this.api.delete(`/api/books/${bookId}/waitinglist`);
  }

  async getWaitingListForBook(bookId: number): Promise<WaitingList[]> {
    const response = await this.api.get(`/api/books/${bookId}/waitinglist`);
    return Array.isArray(response.data) ? response.data : response.data.waitingList || [];
  }

  // Admin APIs
  async getDashboardStats(): Promise<DashboardStats> {
    const response = await this.api.get('/api/admin/stats');
    return response.data;
  }

  async getAllReservations(): Promise<Reservation[]> {
    const response = await this.api.get('/api/admin/reservations');
    return Array.isArray(response.data) ? response.data : response.data.reservations || [];
  }

  async getAdminUsers(): Promise<AdminManagedUser[]> {
    const response = await this.api.get('/api/admin/users');
    return Array.isArray(response.data) ? response.data : response.data.users || [];
  }

  async createAdminUser(payload: AdminCreateUserPayload): Promise<AdminManagedUser> {
    const response = await this.api.post('/api/admin/users', payload);
    return response.data;
  }

  async updateAdminUser(userId: number, payload: AdminUpdateUserPayload): Promise<AdminManagedUser> {
    const response = await this.api.put(`/api/admin/users/${userId}`, payload);
    return response.data;
  }

  async deleteAdminUser(userId: number): Promise<void> {
    await this.api.delete(`/api/admin/users/${userId}`);
  }

  async deleteReservation(reservationId: number): Promise<{ message: string; status: string }> {
    const response = await this.api.delete(`/api/admin/reservations/${reservationId}`);
    return response.data;
  }

  async getAllWaitingList(): Promise<WaitingList[]> {
    const response = await this.api.get('/api/admin/waitinglist');
    return response.data;
  }

  async getBookWaitingList(bookId: number): Promise<WaitingList[]> {
    const response = await this.api.get(`/api/admin/books/${bookId}/waitinglist`);
    return response.data;
  }

  async deleteWaitingEntry(entryId: number): Promise<{ message: string; status: string }> {
    const response = await this.api.delete(`/api/admin/waitinglist/${entryId}`);
    return response.data;
  }

  async getReservationForBook(bookId: number): Promise<Reservation | null> {
    try {
      const response = await this.api.get(`/api/books/${bookId}/reservation`);
      return response.data;
    } catch (error) {
      return null;
    }
  }

  //Customer APIs
  async getMyReservations(): Promise<Reservation[]> {
    const response = await this.api.get('/api/reservations/my');
    return response.data;
  }

  async returnBook(bookId: number): Promise<void> {
    await this.api.post(`/api/books/${bookId}/return`);
  }

  //
  // Recommend API
  async getRecommendations(): Promise<Book[]> {
    const response = await this.api.get('/api/recommendations');
    return Array.isArray(response.data) ? response.data : [];
  }

  // Chatbot APIs
  async getChatbotHealth(): Promise<{ status: string; message?: string; service?: string; capabilities?: string }> {
    try {
      const response = await this.api.get('/api/chatbot/health');
      return response.data;
    } catch (error) {
      return { status: 'error', message: 'Chatbot service unavailable' };
    }
  }

  async sendChatMessage(message: string): Promise<{ response: string; user?: string; error?: string }> {
    const response = await this.api.post('/api/chatbot/send', { message });
    return response.data;
  }
}

export const apiService = new ApiService();
export default apiService;
