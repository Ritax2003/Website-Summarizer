// src/pages/HistoryPage.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';

function HistoryPage() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);  // State for current page
  const [totalPages, setTotalPages] = useState(0); // Keep track of total pages

   useEffect(() => {
        setLoading(true);
        // Fetch history data from backend
        axios.get('http://localhost:8080/api/summarization/all')
            .then(response => {
                // Check if the response contains a `content` array (for pagination)
                const data = response.data.content ? response.data.content : response.data;
                setHistory(data);
                
                setTotalPages(response.data.totalPages); // Set total pages from the response
                setLoading(false);
              })
            .catch(error => {
                console.error("There was an error fetching the history data!", error);
                setLoading(false);
            });
    }, [page]);
    const formatTimestamp = (timestamp) => {
        const date = new Date(timestamp);
        return date.toLocaleString();  // Formats date and time to a user-friendly format
    };
    
    const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {  // Ensure the page number is valid
      setPage(newPage);
    }
    };
    
    if (loading) {
        return <p>Loading...</p>;
    }
  return (
    <div>
      <h2>History of Summarized Websites</h2>
      
      {history.length === 0 ? (
        <p>No summaries available.</p>
      ) : (
        <table border="1" style={{ width: '100%', textAlign: 'left' }}>
          <thead>
            <tr>
              <th>URL</th>
              <th>Summary</th>
              <th>Created At</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item, index) => (
              <tr key={index}>
                <td>{item.url}</td>
                <td>{item.summary}</td>
                <td>{formatTimestamp(item.timestamp)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Pagination Controls */}
      <div style={{ marginTop: '20px' }}>
        <button 
          onClick={() => handlePageChange(page - 1)} 
          disabled={page === 0}>
          Previous
        </button>
        
        <span> Page {page + 1} of {totalPages} </span>
        
        <button 
          onClick={() => handlePageChange(page + 1)} 
          disabled={page === totalPages - 1}>
          Next
        </button>
      </div>
    </div>
  );
}

export default HistoryPage;
