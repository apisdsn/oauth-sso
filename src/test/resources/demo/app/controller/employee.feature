#Feature: Employee Management
#
#  Scenario: Register Employee an account
#    Given the user provides valid account details
#    When the user sends a POST request to "/register"
#    Then the response status code of account registration should be 201
#    And the response body should contain the created account ID 1