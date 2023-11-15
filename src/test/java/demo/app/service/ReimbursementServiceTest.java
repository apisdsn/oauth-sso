package demo.app.service;

import demo.app.entity.Employee;
import demo.app.entity.Reimbursement;
import demo.app.model.ReimbursementRequest;
import demo.app.model.ReimbursementResponse;
import demo.app.repository.EmployeeRepository;
import demo.app.repository.ReimbursementRepository;
import demo.app.validator.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ReimbursementServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ReimbursementRepository reimbursementRepository;
    @Mock
    private ValidationHelper validationHelper;
    @InjectMocks
    private ReimbursementService reimbursementService;
    private ReimbursementRequest reimbursementRequest;
    private OAuth2AuthenticatedPrincipal principal;
    private Employee employee;
    private Reimbursement reimbursement;

    @BeforeEach
    public void setUp() {
        reimbursementRepository.deleteAll();
        employeeRepository.deleteAll();

        reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000.00));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        reimbursementRequest.setStatus(false);

        principal = mock(OAuth2AuthenticatedPrincipal.class);

        employee = new Employee();
        employee.setFullName("John Doe");
        employee.setClientId("123");

        reimbursement = new Reimbursement();
        reimbursement.setEmployee(employee);
        reimbursement.setAmount(reimbursementRequest.getAmount());
        reimbursement.setActivity(reimbursementRequest.getActivity());
        reimbursement.setTypeReimbursement(reimbursementRequest.getTypeReimbursement());
        reimbursement.setDescription(reimbursementRequest.getDescription());
        reimbursement.setStatus(reimbursementRequest.getStatus());
        reimbursement.setDateCreated(LocalDateTime.now());
    }

    @Test
    public void testCreateWhenAllParametersAreValidThenReturnReimbursementResponse() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.create(reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);

        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
        assertEquals(reimbursement.getStatus(), response.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), response.getAmount());
        assertEquals(reimbursement.getActivity(), response.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), response.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), response.getDescription());
    }

    @Test
    public void testCreateWhenReimbursementRequestIsInvalidThenThrowResponseStatusException() {
        reimbursementRequest.setAmount(null);

        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));

        assertThrows(IllegalArgumentException.class, () -> reimbursementService.create(reimbursementRequest, principal));

        verify(validationHelper, times(1)).validate(reimbursementRequest);
    }

    @Test
    public void testCreateWhenEmployeeIsNotFoundThenThrowResponseStatusException() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> reimbursementService.create(reimbursementRequest, principal));

        verify(validationHelper, times(1)).validate(reimbursementRequest);
    }

    @Test
    public void testUpdateReimbursementUserWhenAllParametersAreValidThenReturnReimbursementResponse() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.updateReimbursementUser(1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
        assertEquals(reimbursement.getStatus(), response.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), response.getAmount());
        assertEquals(reimbursement.getActivity(), response.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), response.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), response.getDescription());

    }

    @Test
    public void testUpdateReimbursementByAdminWhenAllParametersAreValidThenReturnReimbursementResponse() {
        reimbursementRequest.setStatus(true);

        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), (anyLong()))).thenReturn(Optional.of(reimbursement));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.updateReimbursementByAdmin("123", 1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);

        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
        assertEquals(reimbursement.getStatus(), response.getStatus());
    }

    @Test
    public void testRemoveReimbursementByAdminWhenAllParametersAreValidThenNoReturn() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByAdmin("123", 1L);

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    public void testRemoveReimbursementByUserWhenAllParametersAreValidThenNoReturn() {
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByUser(1L, principal);

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    public void testGetReimbursementsWithStatusFalseWhenStatusIsFalseThenReturnReimbursementResponseList() {
        when(reimbursementRepository.findByStatusFalse()).thenReturn(Collections.singletonList(reimbursement));

        List<ReimbursementResponse> responses = reimbursementService.getReimbursementsWithStatusFalse();
        System.out.println(responses);

        verify(reimbursementRepository, times(1)).findByStatusFalse();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());

        assertEquals(reimbursement.getReimbursementId(), responses.get(0).getReimbursementId());
        assertEquals(reimbursement.getStatus(), responses.get(0).getStatus());
        assertEquals(reimbursement.getActivity(), responses.get(0).getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), responses.get(0).getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), responses.get(0).getDescription());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), responses.get(0).getAmount());
        assertEquals(reimbursement.getApprovedId(), responses.get(0).getApprovedId());
        assertEquals(reimbursement.getApprovedName(), responses.get(0).getApprovedName());
        assertEquals(reimbursement.getEmployee().getEmployeeId(), responses.get(0).getEmployeeId());
    }

    @Test
    public void testToReimbursementResponseWhenReimbursementIsValidThenReturnReimbursementResponse() {
        ReimbursementResponse response = reimbursementService.toReimbursementResponse(reimbursement);

        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
        assertEquals(reimbursement.getStatus(), response.getStatus());
        assertEquals(reimbursementService.convertRupiah(reimbursement.getAmount()), response.getAmount());
        assertEquals(reimbursement.getActivity(), response.getActivity());
        assertEquals(reimbursement.getTypeReimbursement(), response.getTypeReimbursement());
        assertEquals(reimbursement.getDescription(), response.getDescription());
    }

    @Test
    public void testConvertRupiahWhenBigDecimalValueIsValidThenReturnString() {
        String result = reimbursementService.convertRupiah(BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertTrue(result.contains("Rp"));
    }

    // ... rest of the test cases ...
}