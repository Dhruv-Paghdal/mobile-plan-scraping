// App.jsx
import React, { useState, useEffect } from 'react';
import Navbar from './Components/Navbar/Navbar';
import SearchFilter from './Components/Search/SearchFilter';
import Card from './Components/Cards/Card';
import Spinner from './Components/Spinner/Spinner';
import ErrorAlert from './Components/Error/ErrorAlert';

function App() {
  const [allPlans, setAllPlans] = useState([]);
  const [filteredPlans, setFilteredPlans] = useState([]);
  const [error, setError] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [selectedProvider, setSelectedProvider] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isFetchingPlans, setIsFetchingPlans] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 0);
    return () => clearTimeout(timer);
  }, []);

  const handleApiError = async (response) => {
    const errorText = await response.text();
    const errorMessage = `Error ${response.status}: ${errorText}`;
    setError(errorMessage);
    console.error(errorMessage);
  };

  const fetchPlans = async (companyName) => {
    if (!companyName.trim()) {
      setAllPlans([]);
      setFilteredPlans([]);
      setHasSearched(false);
      setSelectedProvider('');
      return;
    }
    try {
      const response = await fetch(`/api/get-all-plans?companyName=${companyName}`, {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) {
        await handleApiError(response);
        return;
      }
      const rawText = await response.text();
      let cleanedText = rawText
        .replace(/"Countries":\s*""([^"}]+)"/g, '"Countries": "$1"')
        .replace(/"Calls":\s*"([^"]+)""/g, '"Calls": "$1"');
      const data = JSON.parse(cleanedText);
      const normalizedPlans = (Array.isArray(data) ? data : []).map(plan => ({
        ...plan,
        company: plan.company || companyName || "Unknown",
        Data: plan.Data || plan.data || "N/A",
        Price: plan.Price || plan.price || "0",
        "Type(4g/5g)": plan["Type(4g/5g)"] || plan.type || "N/A",
        Countries: typeof plan.Countries === 'string' ? plan.Countries.split('-').filter(Boolean) : plan.Countries || [],
        Calls: plan.Calls || plan.calls || "N/A",
        Text: plan.Text || plan.text || "N/A",
      }));
      setAllPlans(normalizedPlans);
      setFilteredPlans(normalizedPlans);
      setHasSearched(true);
      setSelectedProvider(companyName);
    } catch (error) {
      const userFriendlyMessage = 'An unexpected error occurred while retrieving the plans. Please try again.';
      setError(userFriendlyMessage);
      console.error('Fetch error:', error);
      setAllPlans([]);
      setFilteredPlans([]);
      setHasSearched(true);
      setSelectedProvider('');
    }
  };

  const fetchPlansFromPageRanking = async (searchTerm) => {
    if (!searchTerm.trim()) {
      setFilteredPlans(allPlans);
      setHasSearched(true);
      return;
    }
    try {
      const response = await fetch(`pageRanking/page-ranking?input=${encodeURIComponent(searchTerm)}`, {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) {
        await handleApiError(response);
        return;
      }
      const rawText = await response.text();
      let cleanedText = rawText
        .replace(/"Countries":\s*""([^"}]+)"/g, '"Countries": "$1"')
        .replace(/"Calls":\s*"([^"]+)""/g, '"Calls": "$1"')
        .replace(/,\s*}/g, '}')
        .replace(/,\s*]/g, ']')
        .replace(/[\n\r\t]/g, '');
      const data = JSON.parse(cleanedText);
      const cleanedPlans = (Array.isArray(data) ? data : []).map(plan => {
        const cleanedPlan = { ...plan };
        Object.keys(cleanedPlan).forEach(key => {
          if (!cleanedPlan[key] || cleanedPlan[key] === '-' || cleanedPlan[key] === '') {
            cleanedPlan[key] = "N/A";
          }
        });
        let countries = cleanedPlan.Countries || "N/A";
        if (typeof countries === 'string') {
          countries = countries
            .replace('U.S.', 'US')
            .replace('USA', 'US')
            .replace(' + Roam Beyond', '')
            .replace('Canda', 'Canada')
            .split('-')
            .filter(Boolean)
            .join(', ');
        }
        cleanedPlan.Countries = countries;
        cleanedPlan.Company = plan.Company || "Unknown";
        cleanedPlan.Text = plan.Texts || plan.Text || "N/A";
        if (plan.Texts) delete cleanedPlan.Texts;
        return cleanedPlan;
      });
      const searchTermLower = searchTerm.toLowerCase();
      const filteredPlansBySearch = cleanedPlans.filter(plan => {
        const fields = [plan.Data, plan.Calls, plan.Text, plan.Countries, plan.Price, plan['Type(4g/5g)'], plan.Company];
        return fields.some(field => (typeof field === 'string' && field.toLowerCase().includes(searchTermLower)));
      });
      setFilteredPlans(filteredPlansBySearch);
      setHasSearched(true);
    } catch (error) {
      const userFriendlyMessage = 'Failed to fetch suggestions. Please check your internet or try later.';
      setError(userFriendlyMessage);
      console.error('Page-ranking API error:', error);
      setFilteredPlans([]);
      setHasSearched(true);
    }
  };

  const fetchPlansFromAPI = async () => {
    setIsFetchingPlans(true);
    try {
      const response = await fetch('api/get-plans', {
        headers: { 'Accept': 'application/json', 'ngrok-skip-browser-warning': 'true' },
      });
      if (!response.ok) {
        await handleApiError(response);
        return;
      }
      const data = await response.json();
      const normalizedPlans = Object.entries(data).flatMap(([company, plans]) =>
        plans.map(plan => ({
          Company: company,
          Data: plan.dataamount || plan.amount || plan.features.find(f => f.includes('GB')) || "N/A",
          Price: plan.price || "N/A",
          "Type(4g/5g)": plan.type || plan.features.find(f => f.includes('5G') || f.includes('4G')) || "N/A",
          Countries: plan.countries
            ? (typeof plan.countries === 'string' ? plan.countries.split(/[,\n]+/).filter(Boolean).join(', ') : "N/A")
            : (plan.features.find(f => f.includes('Canada') || f.includes('US') || f.includes('Mexico')) || "N/A"),
          Calls: plan.features.find(f => f.includes('talk') || f.includes('calling')) || "N/A",
          Text: plan.features.find(f => f.includes('text') || f.includes('messaging')) || "N/A",
          Features: plan.features.filter(Boolean).join(', ') || "N/A",
          Details: plan.details?.length ? plan.details.join(', ') : "N/A",
        }))
      );
      setAllPlans(normalizedPlans);
      setFilteredPlans(normalizedPlans);
      setHasSearched(true);
    } catch (error) {
      const userFriendlyMessage = 'Something went wrong while loading all plans. Please try again.';
      setError(userFriendlyMessage);
      console.error('Fetch error:', error);
      setAllPlans([]);
      setFilteredPlans([]);
      setHasSearched(true);
    } finally {
      setIsFetchingPlans(false);
    }
  };

  const handleProviderSelect = (provider) => {
    setError(null);
    setTimeout(() => {
      fetchPlans(provider || '');
    }, 1000);
  };

  const handleFilter = (searchTerm) => {
    setError(null);
    fetchPlansFromPageRanking(searchTerm);
  };

  const handleFieldFilter = ({ field, value }) => {
    const valueLower = value.toLowerCase();
    const filtered = allPlans.filter(plan => {
      let fieldValue = plan[field];
      if (field === 'Countries' && Array.isArray(fieldValue)) {
        fieldValue = fieldValue.join(', ');
      }
      fieldValue = typeof fieldValue === 'string' ? fieldValue.toLowerCase() : fieldValue?.toString().toLowerCase() || '';
      return fieldValue.includes(valueLower);
    });
    setFilteredPlans(filtered);
    setHasSearched(true);
  };

  if (isLoading) {
    return <Spinner message="Loading your experience..." />;
  }

  return (
    <div>
      <Navbar onSelectProvider={handleProviderSelect} />
      <ErrorAlert message={error} onClose={() => setError(null)} />
      <SearchFilter
        onFilter={handleFilter}
        onFieldFilter={handleFieldFilter}
        selectedProvider={selectedProvider}
        hasPlans={filteredPlans.length > 0}
        fetchPlansFromAPI={fetchPlansFromAPI}
      />
      {isFetchingPlans ? (
        <Spinner message="Fetching plans..." />
      ) : (
        <Card plans={filteredPlans} hasSearched={hasSearched} />
      )}
    </div>
  );
}

export default App;
