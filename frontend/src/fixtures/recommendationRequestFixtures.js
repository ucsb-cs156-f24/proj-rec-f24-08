const recommendationRequestFixtures = {
  oneRecommendation: [
    {
      id: 1,
      professor_id: 1,
      submissionDate: "2022-02-02T12:00",
      completionDate: "",
      status: "PENDING",
      details: "test details",
      recommendationType: "CS Department BS/MS program",
      user_id: 1,
    },
  ],

  threeRecommendations: [
    {
      id: 2,
      professor_id: 2,
      submissionDate: "2022-02-02T12:00",
      completionDate: "",
      status: "PENDING",
      details: "test details",
      recommendationType: "CS Department BS/MS program",
      user_id: 2,
    },

    {
      id: 3,
      professor_id: 3,
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "COMPLETED",
      details: "test details",
      recommendationType: "MS program (other than CS Dept BS/MS)",
      user_id: 3,
    },

    {
      id: 4,
      professor_id: 4,
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "DENIED",
      details: "test details",
      recommendationType: "Scholarship or Fellowship",
      user_id: 4,
    },
  ],
};

export { recommendationRequestFixtures };
