#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Testing CDI operations
  
  Background:
    Given ApiConfig running

  @tag1
  Scenario: CDI creation and deletion
    When The client creates a CDI with an IdentificativoFlusso valued as 01234567890_123 and an IdentificativoPSP valued as 01234567890
    Then The client receives status code 201 
    When The client deletes the created CDI with an IdentificativoFlusso valued as 01234567890_123 and an IdentificativoPSP valued as 01234567890
    Then The client receives status code 200 

  @tag2
  Scenario Outline: The client attempts to create a CDI with CHARITY prefix 
    When The client creates a CDI with an IdentificativoFlusso valued as 01234567890_456 and an IdentificativoPSP valued as "CHARITY_01234567890" 
    Then The client receives status code 422
