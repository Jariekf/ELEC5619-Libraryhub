import axios from 'axios';
import type { AxiosInstance } from 'axios';
import type { 
  Book, 
  User, 
  Review, 
  Reservation, 
  WaitingList, 
  LoginRequest, 
  RegisterRequest
} from '../types';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: 'http://localhost:8080',
      withCredentials: true, // Important for Spring Security session management
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to handle authentication
    this.api.interceptors.request.use(
      (config) => {
        // Spring Security handles authentication via cookies/session
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Add response interceptor to handle errors
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Redirect to login page
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Authentication endpoints
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

  // Book endpoints
  async getBooks(): Promise<Book[]> {
    try {
      const response = await this.api.get('/api/books');
      console.log('Books API response:', response.data);
      // Handle both direct array response and wrapped response
      return Array.isArray(response.data) ? response.data : response.data.books || [];
    } catch (error) {
      console.error('Error fetching books:', error);
      throw error;
    }
  }

  async getBookById(id: number): Promise<Book> {
    const response = await this.api.get(`/api/books/${id}`);
    return response.data;
  }

  async addBook(book: { title: string; author: string; genre: string; available: boolean }): Promise<void> {
    await this.api.post('/api/books/add', null, {
      params: book
    });
  }

  async updateBook(id: number, book: { title: string; author: string; genre: string; available: boolean }): Promise<void> {
    await this.api.post(`/api/books/${id}/edit`, null, {
      params: book
    });
  }

  async deleteBook(id: number): Promise<void> {
    await this.api.post(`/api/books/${id}/delete`);
  }

  async searchBooks(query: string): Promise<Book[]> {
    const response = await this.api.post('/api/books/search', null, {
      params: { title: query }
    });
    // Handle both direct array response and wrapped response
    return Array.isArray(response.data) ? response.data : response.data.books || [];
  }

  async reserveBook(bookId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/reserve`);
  }

  async returnBook(bookId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/return`);
  }

  async joinWaitingList(bookId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/join-waiting-list`);
  }

  async cancelWaiting(bookId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/cancel-waiting`);
  }

  async submitReview(bookId: number, review: { rating: number; comment: string }): Promise<void> {
    await this.api.post(`/books/${bookId}/reviews`, null, {
      params: review
    });
  }

  async deleteReview(bookId: number, reviewId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/reviews/${reviewId}/delete`);
  }

  // Review endpoints
  async getReviewsForBook(bookId: number): Promise<Review[]> {
    const response = await this.api.get(`/books/${bookId}/reviews`);
    return response.data;
  }

  async submitReview(bookId: number, review: { rating: number; comment: string }): Promise<void> {
    const formData = new FormData();
    formData.append('rating', review.rating.toString());
    formData.append('comment', review.comment);
    
    await this.api.post(`/books/${bookId}/reviews`, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
  }

  async deleteReview(bookId: number, reviewId: number): Promise<void> {
    await this.api.post(`/books/${bookId}/reviews/${reviewId}/delete`);
  }

  // Waiting list endpoints
  async joinWaitingList(bookId: number): Promise<WaitingList> {
    const response = await this.api.post(`/api/waitinglist/book/${bookId}`);
    return response.data;
  }

  async getWaitingList(bookId: number): Promise<WaitingList[]> {
    const response = await this.api.get(`/api/waitinglist/book/${bookId}`);
    return response.data;
  }

  async cancelWaiting(bookId: number): Promise<void> {
    await this.api.delete(`/api/waitinglist/book/${bookId}`);
  }

  // Reservation endpoints
  async getUserReservations(): Promise<Reservation[]> {
    const response = await this.api.get('/customer/dashboard');
    return response.data.reservations || [];
  }

  // Chatbot endpoints
  async sendChatMessage(message: string): Promise<{ response: string; user: string }> {
    const response = await this.api.post('/api/chatbot/send', { message });
    return response.data;
  }

  async getChatbotHealth(): Promise<{ status: string; service: string; capabilities: string }> {
    const response = await this.api.get('/api/chatbot/health');
    return response.data;
  }

  // Admin endpoints
  async getAllReservations(): Promise<Reservation[]> {
    const response = await this.api.get('/admin/reservations');
    return response.data.reservations || [];
  }

  async getAllCustomers(): Promise<User[]> {
    const response = await this.api.get('/admin/customers');
    return response.data.customers || [];
  }

  async addBook(book: Omit<Book, 'id'>): Promise<void> {
    const formData = new FormData();
    formData.append('title', book.title);
    formData.append('author', book.author);
    formData.append('genre', book.genre);
    formData.append('available', book.available.toString());
    
    await this.api.post('/books/add', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
  }

  async updateBook(bookId: number, book: Partial<Book>): Promise<void> {
    const formData = new FormData();
    if (book.title) formData.append('title', book.title);
    if (book.author) formData.append('author', book.author);
    if (book.genre) formData.append('genre', book.genre);
    if (book.available !== undefined) formData.append('available', book.available.toString());
    
    await this.api.post(`/books/admin/edit/${bookId}`, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    });
  }

  async deleteBook(bookId: number): Promise<void> {
    await this.api.post(`/books/admin/delete/${bookId}`);
  }
}

export const apiService = new ApiService();
export default apiService;

