import React from 'react';
import './ErrorAlert.css'; // Import the CSS file

const ErrorAlert = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="error-alert">
      <strong>Oops! </strong>
      <span>{message}</span>
      <button className="close-btn" onClick={onClose} aria-label="Close">
        &times;
      </button>
    </div>
  );
};

export default ErrorAlert;
