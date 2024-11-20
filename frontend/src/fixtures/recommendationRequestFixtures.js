const recommendationRequestFixtures = {
  oneRecommendation: [
    {
      id: 1,
      professorName: "testname",
      professorEmail: "testemail@ucsb.edu",
      requesterName: "recname",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "PENDING",
      details: "test details",
      recommendationTypes: "CS Department BS/MS program",
    },
  ],

  threeRecommendations: [
    {
      id: 2,
      professorName: "testname1",
      professorEmail: "testemail1@ucsb.edu",
      requesterName: "recname1",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "PENDING",
      details: "test details",
      recommendationTypes: "CS Department BS/MS program",
    },

    {
      id: 3,
      professorName: "testname2",
      professorEmail: "testemail2@ucsb.edu",
      requesterName: "recname2",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "COMPLETED",
      details: "test details",
      recommendationTypes: "MS program (other than CS Dept BS/MS)",
    },

    {
      id: 4,
      professorName: "testname3",
      professorEmail: "testemail3@ucsb.edu",
      requesterName: "recname3",
      submissionDate: "2022-02-02T12:00",
      completionDate: "2022-02-02T12:00",
      status: "DENIED",
      details: "test details",
      recommendationTypes: "Scholarship or Fellowship",
    },
  ],
};

export { recommendationRequestFixtures };
