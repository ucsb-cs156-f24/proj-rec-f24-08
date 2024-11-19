import React from "react";
import { useBackend } from "main/utils/useBackend";

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import RecommendationRequestTable from "main/components/RecommendationRequests/RecommendationRequestTable";
import { useCurrentUser } from "main/utils/currentUser";

export default function CompletedRequestsPage() {
  const currentUser = useCurrentUser();

  const {
    data: recommendations,
    error: _error,
    status: _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    ["/api/recommendationrequests/all"],
    { method: "GET", url: "/api/recommendationrequests/all" },
    // Stryker disable next-line all : don't test default value of empty list
    [],
  );
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Completed Recommendations</h1>
        <RecommendationRequestTable recommendations={recommendations} currentUser={currentUser} />
      </div>
    </BasicLayout>
  );
}