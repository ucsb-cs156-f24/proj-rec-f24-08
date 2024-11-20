import { render, screen, waitFor, mockConsole } from "@testing-library/react";
import CompletedRequestsPage from "main/pages/Requests/CompletedRequestsPage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { recommendationRequestsFixtures } from "fixtures/recommendationRequestFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

describe("CompletedRequestsPage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);

  const setupUserOnly = () => {
    axiosMock.reset();
    axiosMock.resetHistory();
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
  };

  const testId = "RecommendationRequestTable"

  const queryClient = new QueryClient();

  test("renders only recommendations with completed or denied statuses for professor user", async () => {
    setupUserOnly();
    axiosMock
      .onGet("/api/recommendationrequests/all")
      .reply(200, recommendationRequestsFixtures.threeRecommendations);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CompletedRequestsPage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(
        screen.getByTestId(`${testId}-cell-row-0-col-id`),
      ).toHaveTextContent("3");
    });
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent(
      "4",
    );

    const createRecommendationButton = screen.queryByText("Create Recommendation");
    expect(createRecommendationButton).not.toBeInTheDocument();

    const name = screen.getByText("testname2");
    expect(name).toBeInTheDocument();

    const professorEmail = screen.getByText(
      "testemail2@ucsb.edu",
    );
    expect(professorEmail).toBeInTheDocument();

    // no delete or edit buttons should be visible
    expect(
      screen.queryByTestId("RecommendationRequestTable-cell-row-0-col-Delete-button"),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByTestId("RecommendationRequestTable-cell-row-0-col-Edit-button"),
    ).not.toBeInTheDocument();
  });

  test("renders empty table when backend unavailable, user only", async () => {
    setupUserOnly();

    axiosMock.onGet("/api/recommendationrequests/all").timeout();

    const restoreConsole = mockConsole();

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <CompletedRequestsPage />
        </MemoryRouter>
      </QueryClientProvider>,
    );

    await waitFor(() => {
      expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1);
    });

    const errorMessage = console.error.mock.calls[0][0];
    expect(errorMessage).toMatch(
      "Error communicating with backend via GET on /api/recommendationrequests/all",
    );
    restoreConsole();
  });

  // test("what happens when you click delete, admin", async () => {
  //   setupAdminUser();

  //   axiosMock
  //     .onGet("/api/restaurants/all")
  //     .reply(200, restaurantFixtures.threeRestaurants);
  //   axiosMock
  //     .onDelete("/api/restaurants")
  //     .reply(200, "Restaurant with id 1 was deleted");

  //   render(
  //     <QueryClientProvider client={queryClient}>
  //       <MemoryRouter>
  //         <RestaurantIndexPage />
  //       </MemoryRouter>
  //     </QueryClientProvider>,
  //   );

  //   await waitFor(() => {
  //     expect(
  //       screen.getByTestId(`${testId}-cell-row-0-col-id`),
  //     ).toBeInTheDocument();
  //   });

  //   expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent(
  //     "2",
  //   );

  //   const deleteButton = screen.getByTestId(
  //     `${testId}-cell-row-0-col-Delete-button`,
  //   );
  //   expect(deleteButton).toBeInTheDocument();

  //   fireEvent.click(deleteButton);

  //   await waitFor(() => {
  //     expect(mockToast).toBeCalledWith("Restaurant with id 1 was deleted");
  //   });

  //   await waitFor(() => {
  //     expect(axiosMock.history.delete.length).toBe(1);
  //   });
  //   expect(axiosMock.history.delete[0].url).toBe("/api/restaurants");
  //   expect(axiosMock.history.delete[0].url).toBe("/api/restaurants");
  //   expect(axiosMock.history.delete[0].params).toEqual({ id: 2 });
  // });
});
