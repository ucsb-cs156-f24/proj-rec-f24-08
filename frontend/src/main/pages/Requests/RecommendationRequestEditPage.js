import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

import { useParams } from "react-router-dom";
import RecommendationRequestForm from "main/components/RecommendationRequest/RecommendationRequestForm";
import { Navigate } from "react-router-dom";
import { useBackend, useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function RecommendationRequestEditPage({ storybook = false }) {
  let { id } = useParams();

  const {
    data: request,
    _error,
    _status,
  } = useBackend(
    // Stryker disable next-line all : don't test internal caching of React Query
    [`/api/recommendationrequest?id=${id}`],
    {
      // Stryker disable next-line all : GET is the default, so mutating this to "" doesn't introduce a bug
      method: "GET",
      url: `/api/recommendationrequest`,
      params: {
        id,
      },
    },
  );

  const objectToAxiosPutParams = (request) => ({
    url: "/api/recommendationrequest",
    method: "PUT",
    params: {
      id: request.id,
    },
    data: {
      professorName: request.professorName,
      professorEmail: request.professorEmail,
      submissionDate: new Date().toISOString(), // gets current date
      status: "PENDING",
      details: request.recommendationTypes !== "Other" ? request.details : "", // in the case that Other is not selected
      recommendationTypes:
        request.recommendationTypes === "Other"
          ? request.details
          : request.recommendationTypes,
    },
  });

  const onSuccess = (request) => {
    toast(
      `Request Updated - id: ${request.id} professor name: ${request.professorName} professorEmail: ${request.professorEmail}`,
    );
  };

  const mutation = useBackendMutation(
    objectToAxiosPutParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    [`/api/recommendationrequest?id=${id}`],
  );

  const { isSuccess } = mutation;

  const onSubmit = async (data) => {
    mutation.mutate(data);
  };

  if (isSuccess && !storybook) {
    return <Navigate to="/requests/statistics" />;
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Edit Request</h1>
        {request && (
          <RecommendationRequestForm
            submitAction={onSubmit}
            buttonLabel={"Update"}
            initialContents={request}
          />
        )}
      </div>
    </BasicLayout>
  );
}
