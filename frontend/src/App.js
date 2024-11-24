import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";

import RecommendationRequestIndexPage from "main/pages/RecommendationRequest/RecommendationRequestIndexPage";
import RecommendationRequestEditPage from "main/pages/RecommendationRequest/RecommendationRequestEditPage";
import RecommendationRequestCreatePage from "main/pages/RecommendationRequest/RecommendationRequestCreatePage";

import PendingRequestsPage from "main/pages/Requests/PendingRequestsPage";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import StatisticsPage from "main/pages/Requests/StatisticsPage";

import { hasRole, useCurrentUser } from "main/utils/currentUser";

import "bootstrap/dist/css/bootstrap.css";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const { data: currentUser } = useCurrentUser();

  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<HomePage />} />
        <Route exact path="/profile" element={<ProfilePage />} />
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <Route exact path="/admin/users" element={<AdminUsersPage />} />
        )}
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <>
            <Route
              exact
              path="/requests/pending"
              element={<PendingRequestsPage />}
            />
            <Route
              exact
              path="/requests/completed"
              element={<CompletedRequestsPage />}
            />
            <Route
              exact
              path="/requests/statistics"
              element={<StatisticsPage />}
            />
          </>
        )}
        //add path to recommendation request
        {hasRole(currentUser, "ROLE_USER") && (
          <>
            <Route
              exact
              path="/requests"
              element={<RecommendationRequestIndexPage />}
            />
          </>
        )}
        {hasRole(currentUser, "ROLE_ADMIN") && (
          <>
            <Route
              exact
              path="/requests/edit/:id"
              element={<RecommendationRequestEditPage />}
            />
            <Route
              exact
              path="/requests/create"
              element={<RecommendationRequestCreatePage />}
            />
          </>
        )}
      </Routes>
    </BrowserRouter>
  );
}

export default App;