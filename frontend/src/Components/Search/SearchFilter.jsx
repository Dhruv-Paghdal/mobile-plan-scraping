import React, { useState, useEffect, useRef } from 'react';
import './SearchFilter.css';
import Button from '../Button/Button'; // Adjust path based on your folder structure

const SearchFilter = ({ onFilter, onFieldFilter, selectedProvider, hasPlans, fetchPlansFromAPI }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [lastSearchTerm, setLastSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [spellSuggestions, setSpellSuggestions] = useState([]);
  const [isDropdownVisible, setIsDropdownVisible] = useState(false);
  const [mostSearchedWords, setMostSearchedWords] = useState([]);
  const [spellCheckError, setSpellCheckError] = useState(null);
  const [filterField, setFilterField] = useState('');
  const [filterValue, setFilterValue] = useState('');
  const wrapperRef = useRef(null);

  const filterFields = [
    { label: 'Data', value: 'Data' },
    { label: 'Calls', value: 'Calls' },
    { label: 'Texts', value: 'Texts' },
    { label: 'Countries', value: 'Countries' },
    { label: 'Price', value: 'Price' },
    { label: 'Type (4G/5G)', value: 'Type(4g/5g)' },
    { label: 'Company', value: 'Company' },
  ];

  const fetchSuggestions = async (text) => {
    if (!text.trim()) {
      setSuggestions([]);
      setIsDropdownVisible(false);
      console.log('No text, cleared suggestions');
      return;
    }
    try {
      const response = await fetch(`/suggestion/getsuggestion?text=${encodeURIComponent(text)}`, {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) throw new Error('Failed to fetch suggestions');
      const data = await response.json();
      console.log('Suggestions:', data);
      const uniqueSuggestions = [...new Set(data.map(item => item.toLowerCase()))];
      setSuggestions(uniqueSuggestions);
      setIsDropdownVisible(true);
    } catch (error) {
      console.error('Suggestion fetch error:', error);
      setSuggestions([]);
      setIsDropdownVisible(false);
    }
  };

  const fetchSpellCheckSuggestions = async (text) => {
    if (!text.trim()) {
      console.log('No text for spell-check, clearing spell suggestions');
      setSpellSuggestions([]);
      setSpellCheckError(null);
      return;
    }
    try {
      console.log(`Fetching spell-check suggestions for: "${text}"`);
      const response = await fetch(`spellCheck/spell-check?input=${encodeURIComponent(text)}`, {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Spell-check API failed: ${response.status} - ${errorText}`);
      }
      const data = await response.json();
      console.log(`Spell-check suggestions for "${text}":`, data);
      setSpellSuggestions(Array.isArray(data) ? data : []);
      setSpellCheckError(null);
    } catch (error) {
      console.error('Spell-check fetch error:', error);
      setSpellSuggestions([]);
      setSpellCheckError(error.message);
    }
  };

  const fetchMostSearchedWords = async () => {
    try {
      console.log('Fetching most searched words from /search-freq');
      const response = await fetch(`searchFreq/search-freq`, {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) throw new Error('Failed to fetch most searched words');
      const data = await response.json();
      console.log('Most searched words:', data);
      setMostSearchedWords(data || []);
    } catch (error) {
      console.error('Search frequency fetch error:', error);
      setMostSearchedWords([]);
    }
  };

  const handleSearchChange = (e) => {
    const term = e.target.value;
    setSearchTerm(term);
    fetchSuggestions(term);
    setSpellSuggestions([]);
    setSpellCheckError(null);
  };

  const handleKeyPress = async (e) => {
    if (e.key === 'Enter' && searchTerm.trim()) {
      console.log(`Enter pressed with search term: ${searchTerm}`);
      setIsDropdownVisible(false);
      setLastSearchTerm(searchTerm);
      await fetchSpellCheckSuggestions(searchTerm);
      onFilter(searchTerm);
      setSearchTerm('');
      setSuggestions([]);
      setIsDropdownVisible(false);
    }
  };

  const handleSuggestionClick = async (suggestion) => {
    console.log(`Suggestion clicked: ${suggestion}`);
    setSearchTerm(suggestion);
    setIsDropdownVisible(false);
    setLastSearchTerm(suggestion);
    await fetchSpellCheckSuggestions(suggestion);
    onFilter(suggestion);
    setSearchTerm('');
    setSuggestions([]);
    setIsDropdownVisible(false);
  };

  const handleSpellSuggestionClick = (suggestion) => {
    console.log(`Spell-check suggestion clicked: ${suggestion}`);
    setSearchTerm(suggestion);
    setSpellSuggestions([]);
    setSpellCheckError(null);
    fetchSuggestions(suggestion);
    setLastSearchTerm(suggestion);
    onFilter(suggestion);
    setSearchTerm('');
    setSuggestions([]);
    setIsDropdownVisible(false);
  };

  const handleFieldFilter = () => {
    if (filterField && filterValue.trim()) {
      console.log(`Applying filter: ${filterField} = ${filterValue}`);
      onFieldFilter({ field: filterField, value: filterValue });
      setFilterValue('');
    }
  };

  const handleFilterKeyPress = (e) => {
    if (e.key === 'Enter') handleFieldFilter();
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        console.log('Clicked outside, hiding dropdown');
        setIsDropdownVisible(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const shouldShowSpellSuggestion = !hasPlans && spellSuggestions.length > 0 && lastSearchTerm;
  const validSpellSuggestions = shouldShowSpellSuggestion
    ? spellSuggestions.filter(suggestion => suggestion && suggestion.trim() && suggestion !== '-')
    : [];

  return (
    <div className="sf-container">
      <div className="sf-main-wrapper">
        {/* Search Bar */}
        <div className="sf-search-wrapper" ref={wrapperRef}>
          <input
            type="text"
            className="sf-search-bar"
            placeholder="Search for companies' plans (press Enter)..."
            value={searchTerm}
            onChange={handleSearchChange}
            onKeyPress={handleKeyPress}
            onClick={() => fetchMostSearchedWords()}
            onFocus={() => suggestions.length > 0 && setIsDropdownVisible(true)}
          />
          {isDropdownVisible && suggestions.length > 0 && (
            <ul className="sf-suggestions-list">
              {suggestions.map((suggestion, index) => (
                <li
                  key={index}
                  className="sf-suggestion-item"
                  onMouseDown={() => handleSuggestionClick(suggestion)}
                >
                  {suggestion}
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Filter and Fetch Button */}
        <div className="sf-filter-wrapper">
          <select
            className="sf-filter-select"
            value={filterField}
            onChange={(e) => setFilterField(e.target.value)}
          >
            <option value="">Filter by</option>
            {filterFields.map((field) => (
              <option key={field.value} value={field.value}>
                {field.label}
              </option>
            ))}
          </select>
          <input
            type="text"
            className="sf-filter-input"
            placeholder="Filter value"
            value={filterValue}
            onChange={(e) => setFilterValue(e.target.value)}
            onKeyPress={handleFilterKeyPress}
          />
          <Button onClick={handleFieldFilter}>Apply</Button>
          <Button onClick={fetchPlansFromAPI} className="fetch-plans-btn">
            Fetch All Plans
          </Button>
        </div>
      </div>

      {/* Spell Check and Most Searched Words */}
      {spellCheckError && <div className="sf-spell-check-error">Spell-check error: {spellCheckError}</div>}
      {shouldShowSpellSuggestion && validSpellSuggestions.length > 0 && (
        <div className="sf-spell-check">
          Did you mean:{' '}
          {validSpellSuggestions.map((suggestion, index) => (
            <span key={index}>
              <span className="sf-spell-suggestion" onClick={() => handleSpellSuggestionClick(suggestion)}>
                {suggestion}
              </span>
              {index < validSpellSuggestions.length - 1 ? ', ' : '?'}
            </span>
          ))}
        </div>
      )}
      {mostSearchedWords.length > 0 && (
        <div className="sf-most-searched">
          <p>
            Most searched words:{' '}
            {mostSearchedWords.map((word, index) => (
              <span key={index} className="sf-most-searched-word">
                {word}{index < mostSearchedWords.length - 1 ? ', ' : ''}
              </span>
            ))}
          </p>
        </div>
      )}
    </div>
  );
};

export default SearchFilter;