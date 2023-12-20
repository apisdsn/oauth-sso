//package demo.app.it.steps;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import demo.app.entity.Employee;
//import demo.app.it.utils.FeatureUtils;
//import demo.app.model.WebResponse;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Component
//public class RegisterEmployeeStepDefinitions {
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    private TestRestTemplate restTemplate;
//    private ResponseEntity<WebResponse<Long>> response;
//    private Employee mockedAccount;
//
//    @Given("the user provides valid account details")
//    public void theUserProvidesValidAccountDetails() {
//        mockedAccount = FeatureUtils.getMockAccount();
//    }
//
//    @When("the user sends a POST request to {string}")
//    public void theUserSendsAPOSTRequestTo(String path) throws JsonProcessingException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth("FpcQlA16cY1WFCHlVxcRBYscPwVzdQD9Rl_eoheYtl5TgUcsVS0R_f5LsEyYxxB-593dW90");
//
//        HttpEntity<Employee> entity = new HttpEntity<>(mockedAccount, headers);
//        response = this.restTemplate.exchange(FeatureUtils.URL + path, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
//        });
//    }
//
//    @Then("the response status code of account registration should be {int}")
//    public void theResponseStatusCodeOfAccountRegistrationShouldBe(int statusCode) {
//        assertEquals(HttpStatusCode.valueOf(statusCode), response.getStatusCode());
//    }
//
//    @And("the response body should contain the created account ID {long}")
//    public void theResponseBodyShouldContainTheCreatedAccountID(long id) {
//        assertEquals(id, Objects.requireNonNull(response.getBody()).getData());
//    }
//}