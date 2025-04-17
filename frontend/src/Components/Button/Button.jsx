import React from 'react';
import './Button.css';

const Button = ({ onClick, children, className = '' }) => {
  return (
    <button className={`custom-btn ${className}`} onClick={onClick}>
      {children}
    </button>
  );
};

export default Button;