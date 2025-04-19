import React, { useState } from 'react';
import axios from 'axios';

function MainPage() {
  const [URL, setURL] = useState('');
  const [summary, setSummary] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');  // Reset the error state before making the request

    const payload = { url: URL };

    try {
      // Send POST request to Spring Boot API
      const response = await axios.post('http://localhost:8080/api/summarization/add', payload);

      // If successful, set the summary state
      if (response.data) {
        setSummary(response.data.summary);  // Make sure to access the summary from the response object
      }
    } catch (err) {
      console.error('Error while summarizing:', err);
      setError('Failed to summarize the URL.');  // Set the error message if the request fails
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Summarize a Website</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={URL}
          onChange={(e) => setURL(e.target.value)}
          placeholder="Enter website URL"
          style={{ width: '300px' }}
          required
        />
        <button type="submit" disabled={loading}>Submit</button>
      </form>

      {loading && <p>Summarizing...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}  {/* Display error if any */}
      {summary && (
        <div>
          <h3>Summary:</h3>
          <p>{summary}</p> {/* Directly render the summary string */}
        </div>
      )}
    </div>
  );
}

export default MainPage;
