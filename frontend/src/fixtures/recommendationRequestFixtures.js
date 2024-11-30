const recommendationRequestFixtures = {
  oneRecommendation: [
    {
      id: 1,
      professor_id: 1,
      professor: {
        fullName: "Phil Conrad",
        email: "phtcon@ucsb.edu",
      },
      submissionDate: "2022-02-02T12:00",
      dueDate: "2022-04-02T12:00",
      lastModifiedDate: "2022-02-02T12:00",
      completionDate: "",
      status: "PENDING",
      details: "details",
      recommendationType: "CS Department BS/MS program",
      requester_id: 1,
      requester: {
        fullName: "Divyani Punj",
        email: "divyanipunj@ucsb.edu",
      },
    },
  ],

  threeRecommendations: [
    {
      id: 2,
      professor_id: 2,
      professor: {
        fullName: "Chaewon Bang",
        email: "chaewonbang@ucsb.edu",
      },
      submissionDate: "2022-01-02T12:00",
      dueDate: "2022-09-02T12:00",
      lastModifiedDate: "2022-06-02T12:00",
      completionDate: "2022-06-02T12:00",
      status: "COMPLETED",
      details: "lots of details",
      recommendationType: "CS Department BS/MS program",
      requester_id: 2,
      requester: {
        fullName: "Mia Scott",
        email: "mscott@ucsb.edu",
      },
    },

    {
      id: 3,
      professor_id: 3,
      professor: {
        fullName: "Lithika Anabarasan",
        email: "lithika@ucsb.edu",
      },
      submissionDate: "2022-02-02T12:00",
      dueDate: "2022-10-02T12:00",
      lastModifiedDate: "2022-02-02T12:00",
      completionDate: "",
      status: "PENDING",
      details: "test details",
      recommendationType: "CS Department BS/MS program",
      requester_id: 3,
      requester: {
        fullName: "Shrena Punj",
        email: "shrenapunj@ucsb.edu",
      },
    },
    {
      id: 4,
      professor_id: 4,
      professor: {
        fullName: "Riya Seghal",
        email: "riya@ucsb.edu",
      },
      submissionDate: "2022-02-02T12:00",
      dueDate: "2022-04-02T12:00",
      lastModifiedDate: "2022-02-02T12:00",
      completionDate: "2022-02-03T12:00",
      status: "COMPLETED",
      details: "test details",
      recommendationType: "CS Department BS/MS program",
      requester_id: 4,
      requester: {
        fullName: "Krish Seghal",
        email: "krishseghal@ucsb.edu",
      },
    },
  ],
};

export { recommendationRequestFixtures };
