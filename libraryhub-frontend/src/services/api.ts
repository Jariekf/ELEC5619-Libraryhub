import axios from 'axios';
import type { AxiosInstance } from 'axios';
import type {
  Book,
  User,
  Review,
  Reservation,
  WaitingList,
  LoginRequest,
  RegisterRequest,
  DashboardStats
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
          if (error.response?.status === 401) {
            window.location.href = '/login';
          }
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

  // 修复：匹配后端的 POST /{id}/delete 端点
  async deleteBook(id: number): Promise<void> {
    await this.api.post(`/api/books/${id}/delete`);
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

  async getAllCustomers(): Promise<User[]> {
    const response = await this.api.get('/api/admin/customers');
    return Array.isArray(response.data) ? response.data : response.data.customers || [];
  }

  async getCustomerById(userId: number): Promise<User> {
    const response = await this.api.get(`/api/admin/customers/${userId}`);
    return response.data;
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