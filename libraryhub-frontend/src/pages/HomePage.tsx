import { useAuth } from '../contexts/AuthContext'
import '../HomePage.css'

const HomePage = () => {
    const { isAuthenticated } = useAuth()

    return (
        <div className="homepage-background">
            <div className="homepage-overlay">
                <div className="homepage-content">
                    <div className="homepage-hero">
                        <h1 className="homepage-title">LibraryHub</h1>
                        <p className="homepage-subtitle">
                            Discover, reserve, and review books in our digital library.
                        </p>

                        <div className="homepage-buttons">
                            {isAuthenticated ? (
                                <>
                                    <a href="/books" className="homepage-btn homepage-btn-primary">
                                        Browse Books
                                    </a>
                                    <a href="/chatbot" className="homepage-btn homepage-btn-secondary">
                                        AI Assistant
                                    </a>
                                </>
                            ) : (
                                <>
                                    <a href="/register" className="homepage-btn homepage-btn-primary">
                                        Get Started
                                    </a>
                                    <a href="/login" className="homepage-btn homepage-btn-secondary">
                                        Sign In
                                    </a>
                                </>
                            )}
                        </div>
                    </div>

                    <div className="homepage-stats">
                        <div className="homepage-stat">
                            <div className="homepage-stat-number">10K+</div>
                            <div className="homepage-stat-label">Books</div>
                        </div>
                        <div className="homepage-stat">
                            <div className="homepage-stat-number">5K+</div>
                            <div className="homepage-stat-label">Users</div>
                        </div>
                        <div className="homepage-stat">
                            <div className="homepage-stat-number">50+</div>
                            <div className="homepage-stat-label">Categories</div>
                        </div>
                        <div className="homepage-stat">
                            <div className="homepage-stat-number">24/7</div>
                            <div className="homepage-stat-label">Access</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default HomePage