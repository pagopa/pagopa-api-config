Feature: Testing the IBAN CRUD operations

  Background:
    Given ApiConfig running

  Scenario: Ibans creation by csv file
    When the client loads a csv file with ibans to create
    Then the client receives the status code 200 for the requested operation
  
  Scenario: Ibans update by csv file
    When the client loads a csv file with ibans to update
    Then the client receives the status code 200 for the requested operation 
    
  Scenario: Ibans delete by csv file
    When the client loads a csv file with ibans to delete
    Then the client receives the status code 200 for the requested operation
  
  Scenario: A csv file containing mixed behavior for ibans (Insertion, Update and Deletion) is loaded
    When the client loads a csv file with mixed behavior ibans
    Then the client receives the status code 200 for the requested operation
    
  Scenario: A BAD csv file is loaded containing two entries on the same PA for the same IBAN
    When the client loads a bad csv file with two entries on the same PA for the same IBAN
    Then the client receives the status code 400 for the requested operation
    
  Scenario: A BAD csv file is loaded containing two insert operations for the same POSTAL IBAN in two different PAs
    When the client loads a bad csv file with two insert operations for the same POSTAL IBAN in two different PAs
    Then the client receives the status code 400 for the requested operation
    
  Scenario: A BAD csv file containing an invalid IBAN entry is loaded
    When the client loads a bad csv file with an invalid IBAN entry
    Then the client receives the status code 400 for the requested operation
    
  Scenario: The same csv file is incorrectly loaded twice in a row
    When the client loads a csv file with ibans to create
    Then the client receives the status code 200 for the requested operation
    When the client loads the same CSV file used previously
    Then the client receives the status code 400 for the requested operation
    
  Scenario: A BAD csv file is loaded containing a deletion for a PA - Iban relationship that does not exist
    When the client loads a bad csv file with PA - Iban relationship that does not exist for deletion
    Then the client receives the status code 400 for the requested operation
  
  Scenario: A BAD csv file is loaded containing an update for a PA - Iban relationship that does not exist
    When the client loads a bad csv file with PA - Iban relationship that does not exist for the update
    Then the client receives the status code 400 for the requested operation
    
  Scenario: A not well formed CSV file is loaded
    When the client loads not well formed csv file
    Then the client receives the status code 400 for the requested operation
    
  
   
