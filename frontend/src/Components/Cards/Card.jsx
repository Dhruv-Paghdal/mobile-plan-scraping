import React from 'react';
import './Card.css';

const Card = ({ plans, hasSearched }) => {
  console.log('Plans in Card:', plans);
  console.log('Plans order in Card:', plans.map(plan => plan.Company || 'Unknown'));

  // Helper function to format field values for display
  const formatFieldValue = (value) => {
    if (Array.isArray(value)) {
      return value.join(', ');
    }
    if (value === null || value === undefined) {
      return 'N/A';
    }
    if (typeof value === 'object') {
      return JSON.stringify(value);
    }
    return value.toString();
  };

  return (
    <div className="card-container">
      {plans.length === 0 ? (
        <p className="no-plans">
          {hasSearched ? "No plans available." : "Search for plans."}
        </p>
      ) : (
        plans.map((plan, index) => (
          <div key={index} className="plan-card">
            {/* Card title is the company name from the API */}
            <h2>{plan.Company.toUpperCase() || `Plan ${index + 1}`}</h2>
            {/* Dynamically display all fields except the Company field */}
            {Object.entries(plan)
              .filter(([key]) => key !== 'Company') // Exclude the Company field
              .map(([key, value]) => (
                <p key={key}>
                  <strong>{key}:</strong> {formatFieldValue(value)}
                </p>
              ))}
          </div>
        ))
      )}
    </div>
  );
};

export default Card;