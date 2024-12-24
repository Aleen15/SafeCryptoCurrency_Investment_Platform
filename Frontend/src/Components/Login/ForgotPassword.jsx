// ForgotPassword.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ForgotPassword.css';
import land from '../Assets/land.jpg';
import axios from 'axios';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [isEmailValid, setIsEmailValid] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [isVerified, setIsVerified] = useState(false);
  const [error, setError] = useState('');  // State for error message
  const navigate = useNavigate();

  const handleEmailChange = (e) => {
    const emailValue = e.target.value;
    setEmail(emailValue);

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setIsEmailValid(emailRegex.test(emailValue));
    setIsVerified(false); // Reset verification status if email changes
  };

  const handleVerifyEmail = async () => {
    if (isEmailValid) {
      setIsVerifying(true); // Start loading
      try {
        // Send the email to the backend for verification
        const response = await axios.post('http://localhost:8005/auth/forgot-password', { email });

        if (response.status === 200) {
          setIsVerified(true);  // Mark as verified
          setTimeout(() => {
            navigate('/otp-verification'); // Redirect to OTP verification page
          }, 1500); // Wait before redirecting
        }
      } catch (error) {
        setError('An error occurred or user not found');  // Set error message if any error occurs
      } finally {
        setIsVerifying(false);  // Stop loading
      }
    }
  };

  return (
    <div className="forgot-password-page">
      <div className="forgot-password-container">
        <h2 className='f'>Forgot Password</h2>
        <p className='p'>Enter your email to verify it and proceed to reset your password.</p>
        <form>
          <div className="input-group">
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={handleEmailChange}
              required
            />
            <span className="icon">📧</span>
          </div>
          <div className="btn">
            <button
              type="button"
              className="verify-button"
              onClick={handleVerifyEmail}
              disabled={!isEmailValid || isVerifying}
            >
              {isVerifying ? 'Verifying...' : 'Verify Email'}
            </button>
          </div>
          {isVerified && (
            <p className="verified-message">Email has been verified successfully!</p>
          )}
        </form>
      </div>
      <div className="mockups">
        <img
          src={land}
          alt="Device mockups"
          className="mockups-img"
        />
      </div>
    </div>
  );
};

export default ForgotPassword;
