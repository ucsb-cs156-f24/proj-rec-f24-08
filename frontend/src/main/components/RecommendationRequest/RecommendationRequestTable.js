import React from "react";
import OurTable, { ButtonColumn } from "main/components/OurTable";

import { useBackendMutation } from "main/utils/useBackend";
import {
  cellToAxiosParamsDelete,
  onDeleteSuccess,
} from "main/utils/RecommendationRequestUtils";
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";

export default function RecommendationRequestTable({ requests, currentUser }) {
  const navigate = useNavigate();

  const editCallback = (cell) => {
    navigate(`/requests/edit/${cell.row.values.id}`);
  };

  // Stryker disable all : hard to test for query caching

  const deleteMutation = useBackendMutation(
    cellToAxiosParamsDelete,
    { onSuccess: onDeleteSuccess },
    ["/api/recommendationrequest/all"],
  );
  // Stryker restore all

  // Stryker disable next-line all : TODO try to make a good test for this
  const deleteCallback = async (cell) => {
    deleteMutation.mutate(cell);
  };

  const columns = [
    {
      Header: "id",
      accessor: "id", // accessor is the "key" in the data
    },
    {
      Header: "Professor Name",
      accessor: "professor.fullName",
    },
    {
      Header: "Professor Email",
      accessor: "professor.email",
    },
    {
      Header: "Requester Name",
      accessor: "requester.fullName",
    },
    {
      Header: "Recommendation Type",
      accessor: "recommendationType",
    },
    {
      Header: "Details",
      accessor: "details",
    },
    {
      Header: "Status",
      accessor: "status",
    },
    {
      Header: "Submission Date",
      accessor: "submissionDate",
    },
    {
      Header: "Last Modified Date",
      accessor: "lastModifiedDate",
    },
    {
      Header: "Completion Date",
      accessor: "completionDate",
    },
  ];

  //since all admins have the role of a user, we can just check if the current user has the role ROLE_USER
  if (hasRole(currentUser, "ROLE_USER")) {
    columns.push(
      ButtonColumn(
        "Delete",
        "danger",
        deleteCallback,
        "RecommendationRequestTable",
      ),
    );
  }

  if (
    hasRole(currentUser, "ROLE_USER") &&
    !hasRole(currentUser, "ROLE_ADMIN")
  ) {
    columns.push(
      ButtonColumn(
        "Edit",
        "primary",
        editCallback,
        "RecommendationRequestTable",
      ),
    );
  }

  return (
    <OurTable
      data={requests}
      columns={columns}
      testid={"RecommendationRequestTable"}
    />
  );
}
