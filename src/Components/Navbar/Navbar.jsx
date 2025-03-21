import React,{ useState } from 'react'
import './Navbar.css'
import logoGif from 'C:/Users/PATEL VIRAJ/Downloads/New folder/my-app/src/assets/logo.gif';

const Navbar=()=>{
  const [isActive, setIsActive] = useState(false); // state to track active class

  const handleClick = () => {
    setIsActive(!isActive); // toggle active class
  };

  
  return (
    <header className="header">
        <a href="/" className="logo">
        <img src={logoGif} alt="Logo" className="logo-img" /> 
        <span className="logo-name">DataDial</span> </a>

        <nav className="navbar">
            <a href="/">Bell</a>
            <a href="/">Rogers</a>
            <a href="/">Telus</a>
            <a href="/">Freedom</a>
            <a href="/">Fido</a>
        </nav>

      <div className={`hamburger-wrapper ${isActive ? 'active' : ''}`} onClick={handleClick}>
        <div className="hamburger-icon">
          <span></span>
          <span></span>
          <span></span>
        </div>
        </div>    
    </header>
  )
   
}
export default Navbar