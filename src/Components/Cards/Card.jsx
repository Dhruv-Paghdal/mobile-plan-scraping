import React from 'react';
import './Card.css';

const plansData = [
  {
    id: 1,
    company: 'Bell',
    data: '20GB',
    price: '$40/month',
    speed: '5G',
    countries: 'Canada, USA',
  },
  {
    id: 2,
    company: 'Rogers',
    data: '50GB',
    price: '$60/month',
    speed: '5G+',
    countries: 'Canada',
  },
  {
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },
  {
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },
  {
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },
  {
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },{
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },{
    id: 3,
    company: 'Telus',
    data: '10GB',
    price: '$25/month',
    speed: '4G',
    countries: 'Canada, Mexico',
  },
  // Add more plans as needed
];

const Card = () => {
  return (
    <div className="plans-container">
      <h1 className="plans-title">Best Plans</h1>
      <div className="plans-grid">
        {plansData.map((plan) => (
          <div key={plan.id} className="plan-card">
            <h2>{plan.company}</h2>
            <p><strong>Data:</strong> {plan.data}</p>
            <p><strong>Price:</strong> {plan.price}</p>
            <p><strong>Speed:</strong> {plan.speed}</p>
            <p><strong>Countries:</strong> {plan.countries}</p>
            <button className="select-btn">Select Plan</button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Card;
