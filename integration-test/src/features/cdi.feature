Feature: Testing CDI operations
  
  Background:
    Given ApiConfig running

  Scenario: CDI creation and deletion
    When the client creates a CDI with a runtime random IdentificativoFlusso and an IdentificativoPSP valued as "BPPIITRRZZZ"
    Then the client receives status code 201 
    When the client deletes the created CDI with an IdentificativoPSP valued as "BPPIITRRZZZ"
    Then the client receives status code 200 

  Scenario Outline: The client attempts to create a CDI with CHARITY prefix 
    When the client creates a CDI with a runtime random IdentificativoFlusso and an IdentificativoPSP valued as "CHARITYNEXI" 
    Then the client receives status code 422
