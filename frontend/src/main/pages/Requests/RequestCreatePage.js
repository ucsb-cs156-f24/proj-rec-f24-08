import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import RecommendationRequestForm from "main/components/Requests/RecommendationRequestForm";
import { Navigate } from "react-router-dom";
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function RestaurantCreatePage({ storybook = false }) {
  const objectToAxiosParams = (request) => ({
    url: "/api/recommendationrequests/post",
    method: "POST",
    params: {
        professorName: request.professorName,
        professorEmail: request.professorEmail,
        requesterName: request.requesterName,
        submissionDate: Date.now().toISOString(), // gets current date
        // completionDate: request.completionDate, // nothing yet because haven't completed? - are we allowed to not submit a value?
        status: "PENDING",
        details: request.recommendationTypes !== "Other" ? request.details : "", // in the case that Other is not selected
        recommendationTypes: request.recommendationTypes === "Other" ? request.recommendationTypes : request.details, // in the case that Other is selected and they are inputting a new rec type
    },
  });

  const onSuccess = (request) => {
    toast(
      `New Request Created - id: ${request.id} requester name: ${request.requesterName}`,
    );
  };

  const mutation = useBackendMutation(
    objectToAxiosParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    ["/api/recommendationrequests/all"], // mutation makes this key stale so that pages relying on it reload
  );

  const { isSuccess } = mutation;

  const onSubmit = async (data) => {
    mutation.mutate(data);
  };

  if (isSuccess && !storybook) {
    return <Navigate to="/recommendationrequests/studentprofile" />;
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Create New Recommendation Request</h1>
        <RecommendationRequestForm submitAction={onSubmit} />
      </div>
    </BasicLayout>
  );
}
