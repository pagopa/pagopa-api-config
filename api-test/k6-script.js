// Auto-generated by the postman-to-k6 converter

import "./libs/shim/core.js";
import "./libs/shim/urijs.js";
import { group } from "k6";

export let options = { maxRedirects: 4 };

const Request = Symbol.for("request");
postman[Symbol.for("initial")]({
  options,
  environment: {
    "api-config-host": "https://api.dev.platform.pagopa.it",
    "api-config-base-path": "apiconfig"
  }
});

export default function() {
  group("creditor institutions", function() {
    postman[Request]({
      name: "health check",
      id: "29feb50a-655d-4cf7-b235-75c29f8027ea",
      method: "GET",
      address: "{{api-config-host}}/{{api-config-base-path}}/info"
    });

    postman[Request]({
      name: "getCreditorInstitutions",
      id: "b1b3c3ed-a8fc-41c2-91bd-03036dd468e0",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/creditorinstitutions?limit=1&page=0",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getCreditorInstitution",
      id: "0cc0d3b2-512b-4aa5-8a8f-59eb443327e3",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/creditorinstitutions/00168480242",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getCreditorInstitutionStations",
      id: "99b6e07d-43e2-4824-af3f-54a569b3bfcd",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/creditorinstitutions/00168480242/stations",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getCreditorInstitutionEncodings",
      id: "8abb3443-8ece-4609-986a-0191ff2da242",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/creditorinstitutions/00168480242/encodings",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getCreditorInstitutionsIbans",
      id: "5ed0df93-1eb7-45f3-995a-87d0abf0cdc1",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/creditorinstitutions/00168480242/ibans",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getStations",
      id: "65b6f1d4-f470-4d4f-9fe1-ad1eccbfd3ff",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/stations?limit=1&page=0&creditorinstitutioncode=00168480242&intermediarycode=80007580279",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getStation",
      id: "3c3d9358-60ea-4930-9921-e08144a67787",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/stations/80007580279_01",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getBrokers",
      id: "c909b70e-7267-4fe3-8b8a-f8f71dffbfc5",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/brokers?limit=1&page=0",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });

    postman[Request]({
      name: "getBroker",
      id: "a8dd629a-9ee5-4a42-930a-9baaa482741d",
      method: "GET",
      address:
        "{{api-config-host}}/{{api-config-base-path}}/brokers/80007580279",
      post(response) {
        pm.test("Status code is 200", function() {
          pm.response.to.have.status(200);
        });
      }
    });
  });
}
