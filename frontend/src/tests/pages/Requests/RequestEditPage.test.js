import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import RequestEditPage from "main/pages/Requests/RequestEditPage";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import mockConsole from "jest-mock-console";

const mockToast = jest.fn();
jest.mock("react-toastify", () => {
  const originalModule = jest.requireActual("react-toastify");
  return {
    __esModule: true,
    ...originalModule,
    toast: (x) => mockToast(x),
  };
});

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => {
  const originalModule = jest.requireActual("react-router-dom");
  return {
    __esModule: true,
    ...originalModule,
    useParams: () => ({
      id: 17,
    }),
    Navigate: (x) => {
      mockNavigate(x);
      return null;
    },
  };
});

describe("RequestEditPage tests", () => {
  describe("when the backend doesn't return data", () => {
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
      axiosMock.reset();
      axiosMock.resetHistory();
      axiosMock
        .onGet("/api/currentUser")
        .reply(200, apiCurrentUserFixtures.userOnly);
      axiosMock
        .onGet("/api/systemInfo")
        .reply(200, systemInfoFixtures.showingNeither);
      axiosMock
        .onGet("/api/recommendationrequest", { params: { id: 17 } })
        .timeout();
    });

    const queryClient = new QueryClient();
    test("renders header but table is not present", async () => {
      const restoreConsole = mockConsole();

      render(
        <QueryClientProvider client={queryClient}>
          <MemoryRouter>
            <RequestEditPage />
          </MemoryRouter>
        </QueryClientProvider>,
      );
      await screen.findByText("Edit Request");
      expect(
        screen.queryByTestId("RecommendationRequestForm-professorName"),
      ).not.toBeInTheDocument();
      restoreConsole();
    });
  });

  describe("tests where backend is working normally", () => {
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
      axiosMock.reset();
      axiosMock.resetHistory();
      axiosMock
        .onGet("/api/currentUser")
        .reply(200, apiCurrentUserFixtures.userOnly);
      axiosMock
        .onGet("/api/systemInfo")
        .reply(200, systemInfoFixtures.showingNeither);
      axiosMock
        .onGet("/api/recommendationrequest", { params: { id: 17 } })
        .reply(200, {
          id: 17,
          professorName: "test",
          professorEmail: "testemail@ucsb.edu",
          requesterName: "testname1",
          submissionDate: "2022-02-02T12:00",
          completionDate: "2022-02-02T12:00",
          status: "PENDING",
          details: "test details",
          recommendationTypes: "PhD program",
        });
      axiosMock.onPut("/api/recommendationrequest").reply(200, {
        id: "17",
        professorName: "testnameother",
        professorEmail: "testnameotheremail@ucsb.edu",
        requesterName: "testname2",
        submissionDate: "2022-02-02T12:00",
        completionDate: "2022-02-02T12:00",
        status: "PENDING",
        details: "test details 2",
        recommendationTypes: "PhD program",
      });
    });

    const queryClient = new QueryClient();

    test("Is populated with the data provided", async () => {
      render(
        <QueryClientProvider client={queryClient}>
          <MemoryRouter>
            <RequestEditPage />
          </MemoryRouter>
        </QueryClientProvider>,
      );

      await screen.findByTestId("RecommendationRequestForm-id");

      const idField = screen.getByTestId("RecommendationRequestForm-id");
      const professorNameField = screen.getByTestId(
        "RecommendationRequestForm-professorName",
      );
      const professorEmailField = screen.getByTestId(
        "RecommendationRequestForm-professorEmail",
      );
      const requesterNameField = screen.getByTestId(
        "RecommendationRequestForm-requesterName",
      );
      const recommendationTypesField = screen.getByTestId(
        "RecommendationRequestForm-recommendationTypes",
      );
      const detailsField = screen.getByTestId(
        "RecommendationRequestForm-details",
      );

      const submitButton = screen.getByTestId(
        "RecommendationRequestForm-submit",
      );

      expect(idField).toBeInTheDocument();
      expect(idField).toHaveValue("17");
      expect(professorNameField).toBeInTheDocument();
      expect(professorNameField).toHaveValue("test");
      expect(professorEmailField).toBeInTheDocument();
      expect(professorEmailField).toHaveValue("testemail@ucsb.edu");
      expect(requesterNameField).toBeInTheDocument();
      expect(requesterNameField).toHaveValue("testname1");
      expect(recommendationTypesField).toBeInTheDocument();
      expect(recommendationTypesField).toHaveValue("PhD program");
      expect(detailsField).toBeInTheDocument();
      expect(detailsField).toHaveValue("test details");

      expect(submitButton).toHaveTextContent("Update");
    });

    test("Changes when you click Update", async () => {
      render(
        <QueryClientProvider client={queryClient}>
          <MemoryRouter>
            <RequestEditPage />
          </MemoryRouter>
        </QueryClientProvider>,
      );

      await screen.findByTestId("RecommendationRequestForm-id");

      const idField = screen.getByTestId("RecommendationRequestForm-id");
      const professorNameField = screen.getByTestId(
        "RecommendationRequestForm-professorName",
      );
      const professorEmailField = screen.getByTestId(
        "RecommendationRequestForm-professorEmail",
      );
      const requesterNameField = screen.getByTestId(
        "RecommendationRequestForm-requesterName",
      );
      const recommendationTypesField = screen.getByTestId(
        "RecommendationRequestForm-recommendationTypes",
      );
      const detailsField = screen.getByTestId(
        "RecommendationRequestForm-details",
      );

      const submitButton = screen.getByTestId(
        "RecommendationRequestForm-submit",
      );

      expect(idField).toHaveValue("17");
      expect(professorNameField).toHaveValue("test");
      expect(professorEmailField).toHaveValue("testemail@ucsb.edu");
      expect(requesterNameField).toHaveValue("testname1");
      expect(recommendationTypesField).toHaveValue("PhD program");
      expect(detailsField).toHaveValue("test details");

      fireEvent.change(professorNameField, {
        target: { value: "testnameother" },
      });
      fireEvent.change(professorEmailField, {
        target: { value: "testemail2@ucsb.edu" },
      });
      fireEvent.change(requesterNameField, {
        target: { value: "testname2" },
      });
      fireEvent.change(recommendationTypesField, {
        target: { value: "PhD program" },
      });
      fireEvent.change(detailsField, {
        target: { value: "test details 2" },
      });

      fireEvent.click(submitButton);

      await waitFor(() => expect(mockToast).toBeCalled());
      expect(mockToast).toBeCalledWith(
        "Request Updated - id: 17 requester name: testname2",
      );
      expect(mockNavigate).toBeCalledWith({ to: "/requests/statistics" });
    });
  });
});
