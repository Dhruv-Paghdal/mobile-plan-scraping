import React, { useState } from 'react';
import './Navbar.css';
import logoGif from '/logo.gif';

const Navbar = ({ onSelectProvider }) => {
  const [isActive, setIsActive] = useState(false);
  const [selectedProvider, setSelectedProvider] = useState(null);

  const providers = ['bell', 'rogers', 'telus', 'freedom', 'virgin'];

  const handleClick = () => {
    setIsActive(!isActive);
  };

  const handleProviderSelect = (provider) => {
    const newSelection = selectedProvider === provider ? null : provider;
    setSelectedProvider(newSelection);
    onSelectProvider(newSelection); // Pass the selected provider to the App component
  };

  return (
    <header className="header">
      <a href="/" className="logo">
        <img src={logoGif} alt="Logo" className="logo-img" />
        <span className="logo-name">DataDial</span>
      </a>

      <nav className={`navbar ${isActive ? 'active' : ''}`}>
        {providers.map((provider) => (
          <button
            key={provider}
            className={`nav-btn ${selectedProvider === provider ? 'active' : ''}`}
            onClick={() => handleProviderSelect(provider)}
          >
            {provider.toUpperCase()}
          </button>
        ))}
      </nav>

      <div className={`hamburger-wrapper ${isActive ? 'active' : ''}`} onClick={handleClick}>
        <div className="hamburger-icon">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
