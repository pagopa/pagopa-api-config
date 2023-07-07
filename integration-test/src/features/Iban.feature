Feature: feat

  Background:
    Given ApiConfig running

  Scenario: Iban creation, update and deletion
    When the client creates the Iban
    Then the client receives status code 201 
    And the response "description" is equal to "Testing"
    Then the "update" of the iban
    And the client receives status code 200
    And the response "description" is equal to "Updated description testing"
    Then the "delete" of the iban
    And the client receives status code 200
