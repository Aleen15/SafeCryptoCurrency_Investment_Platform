// OTPVerification.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './OTPVerification.css';
import land from '../Assets/land.jpg';
import axios from 'axios';


const OTPVerification = () => {
  const resetState = localStorage.getItem('resetState');  // Or use context/global state
  if (resetState !== 'EMAIL_VERIFIED') {
    navigate('/forgot-password');  // Redirect to email verification page
  }
  const [otp, setOtp] = useState('');
  const [otpError, setOtpError] = useState('');
  const [isVerifying, setIsVerifying] = useState(false); // State for loading
  const [isVerified, setIsVerified] = useState(false); // State for verified status
  const navigate = useNavigate();

  const validateOTP = (otp) => {
    const otpPattern = /^[0-9]{6}$/; // OTP must be exactly 6 digits
    if (!otpPattern.test(otp)) {
      setOtpError('OTP must be a 6-digit number.');
      return false;
    } else {
      setOtpError('');
      return true;
    }
  };

  const handleVerifyOTP = async (e) => {
    e.preventDefault();
    // Validate OTP before sending request
    if (validateOTP(otp)) {
      try {
        const email = localStorage.getItem('email');
        if (!email) {
          setOtpError('Email is missing. Please try again.');
          return;
        }

        // Send OTP verification request to the backend
        const response = await axios.post('http://localhost:8005/auth/otp-verification', { email, otp });
        alert(response.data); // "OTP verified successfully" message from backend
        navigate('/reset-password'); // Redirect to password reset page after success
      } catch (error) {
        if (error.response && error.response.status === 404) {
          // OTP or email verification failed
          setOtpError('Invalid OTP. Please try again.');
        } else {
          // General error
          setOtpError('An error occurred. Please try again later.');
        }
      }
    }
  };


  return (
    <div className="otp-verification-page">
      <div className="otp-verification-container">
        <h2>OTP Verification</h2>
        <p>Enter the OTP sent to your email to verify your account.</p>
        <form onSubmit={handleVerifyOTP}>
          <div className="input-group">
            <input
              type="text"
              placeholder="Enter OTP"
              value={otp}
              onChange={(e) => {
                setOtp(e.target.value);
                validateOTP(e.target.value); // Real-time validation
              }}
              required
              disabled={isVerifying || isVerified} // Disable input if verifying or verified
            />
            <span className="icon">🔑</span>
          </div>
          {otpError && <p className="error-message">{otpError}</p>}
          <button
            type="submit"
            className="verify-button"
            disabled={isVerifying || isVerified} // Disable button if verifying or verified
          >
            {isVerifying ? 'Verifying...' : 'Verify OTP'}
          </button>
          {isVerified && <p className="verified-message">OTP Verified Successfully!</p>}
        </form>
      </div>
      <div>
        <img
          src={land}
          alt="Device mockups"
          className="mockups-img"
        />
      </div>
    </div>
  );
};

export default OTPVerification;
