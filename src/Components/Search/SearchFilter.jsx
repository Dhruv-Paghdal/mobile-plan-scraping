import React from 'react';
import './SearchFilter.css';

const SearchFilter = () => {
  return (
    <div className="search-filter-container">
      <div className="search-box-container">
        <div className="search-content">
          <input
            className="search-box"
            placeholder="Get plans on the basis of.."
          />
          <div className="results">
            <div className="result-item">data-plan</div>
            <div className="result-item">price</div>
            <div className="result-item">countries</div>
            <div className="result-item">Speed</div>
          </div>
        </div>
        <input
            type="text"
            className="simple-search-bar"
            placeholder="Search for specific companies plans...."
          />
      </div>
    </div>
  );
};

export default SearchFilter;
