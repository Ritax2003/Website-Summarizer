import React from 'react';
import { BrowserRouter as Router, Route, Routes , Link} from 'react-router-dom';
import Mainpage from './pages/MainPage';
import HistoryPage from './pages/HistoryPage';
import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <Router>
      <div style={{padding:20}}>
        <nav>
          <Link to="/">Summarize</Link> | <Link to="/history">History</Link>
        </nav>
        <Routes>
          <Route path="/" element={<Mainpage />} />
          <Route path="/history" element={<HistoryPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
