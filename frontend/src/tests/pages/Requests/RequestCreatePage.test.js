import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import RequestCreatePage from "main/pages/Requests/RequestCreatePage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

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
    Navigate: (x) => {
      mockNavigate(x);
      return null;
    },
  };
});

describe("RequestCreatePage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);

  beforeEach(() => {
    jest.clearAllMocks();
    axiosMock.reset();
    axiosMock.resetHistory();
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
  });

  const queryClient = new QueryClient();
  test("renders without crashing", async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestCreatePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Professor Name")).toBeInTheDocument();
    });
  });

  test("on submit, makes request to backend, and redirects to /recommendationrequest/studentprofile with type=Other", async () => {
    const queryClient = new QueryClient();
    const request = {
      id: 1,
      professorName: "test",
      professorEmail: "testemail@ucsb.edu",
      requesterName: "testname1",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "PENDING",
      details: "other_type",
      recommendationTypes: "Other",
    };

    axiosMock.onPost("/api/recommendationrequest/post").reply(202, request);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestCreatePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Professor Name")).toBeInTheDocument();
    });

    const profNameInput = screen.getByLabelText("Professor Name");
    expect(profNameInput).toBeInTheDocument();

    const profEmailInput = screen.getByLabelText("Professor Email");
    expect(profEmailInput).toBeInTheDocument();

    const requesterNameInput = screen.getByLabelText("Requester Name");
    expect(requesterNameInput).toBeInTheDocument();

    const recommendationTypesInput = screen.getByLabelText(
      "Recommendation Types",
    );
    expect(recommendationTypesInput).toBeInTheDocument();

    const detailsInput = screen.getByLabelText("Details");
    expect(detailsInput).toBeInTheDocument();

    const createButton = screen.getByText("Create");
    expect(createButton).toBeInTheDocument();

    fireEvent.change(profNameInput, { target: { value: "test" } });
    fireEvent.change(profEmailInput, {
      target: { value: "testemail@ucsb.edu" },
    });
    fireEvent.change(requesterNameInput, { target: { value: "testname1" } });
    fireEvent.change(recommendationTypesInput, { target: { value: "Other" } });
    fireEvent.change(detailsInput, { target: { value: "other_type" } });
    fireEvent.click(createButton);

    await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

    expect(axiosMock.history.post[0].params.professorName).toBe("test");
    expect(axiosMock.history.post[0].params.professorEmail).toBe(
      "testemail@ucsb.edu",
    );
    expect(axiosMock.history.post[0].params.requesterName).toBe("testname1");
    expect(axiosMock.history.post[0].params.status).toBe("PENDING");
    expect(axiosMock.history.post[0].params.details).toBe("");
    expect(axiosMock.history.post[0].params.recommendationTypes).toBe(
      "other_type",
    );

    // assert - check that the toast was called with the expected message
    expect(mockToast).toBeCalledWith(
      "New Request Created - id: 1 requester name: testname1",
    );
    expect(mockNavigate).toBeCalledWith({ to: "/requests/studentprofile" });
  });

  test("on submit, makes request to backend, and redirects to /recommendationrequest/studentprofile without type=Other", async () => {
    const queryClient = new QueryClient();
    const request = {
      id: 1,
      professorName: "test",
      professorEmail: "testemail@ucsb.edu",
      requesterName: "testname1",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "PENDING",
      details: "other_type",
      recommendationTypes: "CS Department BS/MS program",
    };

    axiosMock.onPost("/api/recommendationrequest/post").reply(202, request);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <RequestCreatePage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(screen.getByLabelText("Professor Name")).toBeInTheDocument();
    });

    const profNameInput = screen.getByLabelText("Professor Name");
    expect(profNameInput).toBeInTheDocument();

    const profEmailInput = screen.getByLabelText("Professor Email");
    expect(profEmailInput).toBeInTheDocument();

    const requesterNameInput = screen.getByLabelText("Requester Name");
    expect(requesterNameInput).toBeInTheDocument();

    const recommendationTypesInput = screen.getByLabelText(
      "Recommendation Types",
    );
    expect(recommendationTypesInput).toBeInTheDocument();

    const detailsInput = screen.getByLabelText("Details");
    expect(detailsInput).toBeInTheDocument();

    const createButton = screen.getByText("Create");
    expect(createButton).toBeInTheDocument();

    fireEvent.change(profNameInput, { target: { value: "test" } });
    fireEvent.change(profEmailInput, {
      target: { value: "testemail@ucsb.edu" },
    });
    fireEvent.change(requesterNameInput, { target: { value: "testname1" } });
    fireEvent.change(recommendationTypesInput, { target: { value: "CS Department BS/MS program" } });
    fireEvent.change(detailsInput, { target: { value: "other_type" } });
    fireEvent.click(createButton);

    await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

    expect(axiosMock.history.post[0].params.professorName).toBe("test");
    expect(axiosMock.history.post[0].params.professorEmail).toBe(
      "testemail@ucsb.edu",
    );
    expect(axiosMock.history.post[0].params.requesterName).toBe("testname1");
    expect(axiosMock.history.post[0].params.status).toBe("PENDING");
    expect(axiosMock.history.post[0].params.details).toBe("other_type");
    expect(axiosMock.history.post[0].params.recommendationTypes).toBe(
      "CS Department BS/MS program",
    );

    // assert - check that the toast was called with the expected message
    expect(mockToast).toBeCalledWith(
      "New Request Created - id: 1 requester name: testname1",
    );
    expect(mockNavigate).toBeCalledWith({ to: "/requests/studentprofile" });
  });
});
