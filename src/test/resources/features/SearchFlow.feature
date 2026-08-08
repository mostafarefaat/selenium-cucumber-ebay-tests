Feature: Search and Filter Products on eBay

  Scenario Outline: Search for a product and filter by transmission
    Given I navigate to the eBay home page
    Then I should land on the eBay main page
    When I search for "<searchTerm>"
    Then search results should be displayed
    And I log the number of search results
    When I filter results by Transmission "<transmission>"
    Then the filtered results should reflect the "<transmission>" transmission

    Examples:
      | searchTerm    | transmission |
      | mazda mx-5    | Manual       |