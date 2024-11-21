import React from "react";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { http, HttpResponse } from "msw";

import RequestsCreatePage from "main/pages/RecommendationRequest/RequestCreatePage";

export default {
  title: "pages/Requests/RequestsCreatePage",
  component: RequestsCreatePage,
};

const Template = () => <RequestsCreatePage storybook={true} />;

export const Default = Template.bind({});
Default.parameters = {
  msw: [
    http.get("/api/currentUser", () => {
      return HttpResponse.json(apiCurrentUserFixtures.userOnly, {
        status: 200,
      });
    }),
    http.get("/api/systemInfo", () => {
      return HttpResponse.json(systemInfoFixtures.showingNeither, {
        status: 200,
      });
    }),
    http.post("/api/recommendationrequest/post", () => {
      return HttpResponse.json({}, { status: 200 });
    }),
  ],
};
