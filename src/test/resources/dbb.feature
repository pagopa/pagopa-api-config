Feature: brokers api
  Scenario: client makes call to GET /brokers
    Given brokers in database:
      | username | password | email               |
      | everzet  | 123456   | everzet@knplabs.com |
      | fabpot   | 22@222   | fabpot@symfony.com  |
    When the client calls /brokers?page=1
    Then the client receives status code of 200
    And the client receives server version 1.0
