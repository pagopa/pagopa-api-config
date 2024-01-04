Feature: Testing the IBAN CRUD operations

  Background:
    Given ApiConfig running

  Scenario: Ibans creation by csv file
    When the client loads a csv file with ibans to create
    Then the client receives the status code 200 for the requested operation
  
  Scenario: Ibans update by csv file
    When the client loads a csv file with ibans to update
    Then the client receives the status code 200 for the requested operation 
    
  #Scenario: Ibans delete by csv file
  #  When the client loads a csv file with ibans to delete
  #  Then the client receives the status code 200 for the requested operation
   
