Feature: feat

  Background:
    Given ApiConfig running

  Scenario: Iban creation, update and deletion
    When the client creates the Iban "IT99C0222211111000000003333"
    Then the client receives status code 201 
    And the response "description" is equal to "Testing"
    Then the client "update" the iban "IT99C0222211111000000003333"
    And the client receives status code 200
    And the response "description" is equal to "Updated description testing"
    Then the client "delete" the iban "IT99C0222211111000000003333"
    And the client receives status code 200
  
  Scenario: getting Ibans of creditor creditorInstitution
    When the client gets the Ibans for an EC 
    Then the client receives status code 200 

  Scenario: Iban update not found
    When the client "update" Iban "IT99C0222211111000000003333"
    Then the client receives status code 404

  Scenario: Iban delete not found
    When the client "delete" Iban "IT99C0222211111000000003333"
    Then the client receives status code 404

  Scenario: Update iban, body and queryParam not matching
    When the client "update" Iban "fakeIban"
    Then the client receives status code 400