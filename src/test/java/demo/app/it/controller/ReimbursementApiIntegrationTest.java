package demo.app.it.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.model.WebResponse;
import demo.app.repository.ReimbursementRepository;
import demo.app.service.ReimbursementService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReimbursementApiIntegrationTest {
    private static HttpHeaders headers;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ReimbursementRepository reimbursementRepository;
    @Autowired
    private ReimbursementService reimbursementService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("WCIUyf1_uaqPiVgz4OyqJRks0YT-ybNb7DisPIKYxE1kBuBt687IY_5yrcU3z9I05wJDXTY");
    }

    @Test
    @Sql(statements = "INSERT INTO employees(employee_id, client_id, email, full_name, phone_number, gender, company, position) VALUES (1, '239414077758111751', 'user@i2dev.com', 'John Doe', '089512611411', 'Laki-Laki', 'PT Cinta Sejati', 'Software Engineer')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(statements = "INSERT INTO reimbursement(reimbursement_id, employee_id, approved_id, approved_name, amount, activity, type, description, status, date_created, date_updated) VALUES ('[value-1]','[value-2]','[value-3]','[value-4]','[value-5]','[value-6]','[value-7]','[value-8]','[value-9]','[value-10]','[value-11]')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM employees WHERE client_id = '239414077758111751'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateCurrentReimbursementAndReturn200HttpStatus() throws JsonProcessingException {
        ReimbursementRequest reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(reimbursementRequest), headers);

        ResponseEntity<WebResponse<ReimbursementResponse>> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
        });

        WebResponse<ReimbursementResponse> result = response.getBody();
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Rp1.000,00", result.getData().getAmount());
        assertEquals("Travel", result.getData().getActivity());
        assertEquals("Transport", result.getData().getTypeReimbursement());
        assertEquals("Travel to client location", result.getData().getDescription());
        assertEquals(false, result.getData().getStatus());
        List<Reimbursement> optionalReimbursement = reimbursementRepository.findByStatusFalse();
        boolean isDescriptionFound = optionalReimbursement.stream()
                .map(Reimbursement::getDescription)
                .anyMatch(description -> description.equals(result.getData().getDescription()));
        assertTrue(isDescriptionFound);
        assertNull(result.getErrors());
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/api/reimbursements";
    }
}
