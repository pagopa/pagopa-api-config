Feature: brokers api

  Background:
    Given INTERMEDIARI_PA in database:
      | OBJ_ID | ID_INTERMEDIARIO_PA | ENABLED | CODICE_INTERMEDIARIO | FAULT_BEAN_ESTESO |
      | 2      | '80007580279'       | 'Y'     | 'Regione Veneto'     | 'N'               |
    Given headers:
    """
     {"apiKey": "ciao"}
    """

  Scenario: makes call to GET /brokers
    Given headers:
    """
     {"apiKey": "override"}
    """
    When calls GET /brokers?page=0
    Then receives status code of 200

  Scenario: makes call to POST /brokers
    Given body:
    """
     {"broker_code": "1", "enabled": true, "description": "ciao"}
    """
    When calls POST /brokers
    Then receives status code of 200
