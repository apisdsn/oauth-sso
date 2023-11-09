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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

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
@MockitoSettings(strictness = Strictness.LENIENT)
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
        reimbursementRequest = new ReimbursementRequest();
        reimbursementRequest.setAmount(BigDecimal.valueOf(1000));
        reimbursementRequest.setActivity("Travel");
        reimbursementRequest.setTypeReimbursement("Transport");
        reimbursementRequest.setDescription("Travel to client location");
        reimbursementRequest.setStatus(false);

        principal = mock(OAuth2AuthenticatedPrincipal.class);
        when(principal.getAttributes()).thenReturn(Map.of("sub", "123"));

        employee = new Employee();
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
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.create(reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
    }

    @Test
    public void testUpdateReimbursementUserWhenAllParametersAreValidThenReturnReimbursementResponse() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.updateReimbursementUser(1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
    }

    @Test
    public void testUpdateReimbursementByAdminWhenAllParametersAreValidThenReturnReimbursementResponse() {
        when(reimbursementRepository.findById(anyLong())).thenReturn(Optional.of(reimbursement));
        when(reimbursementRepository.save(any(Reimbursement.class))).thenReturn(reimbursement);

        ReimbursementResponse response = reimbursementService.updateReimbursementByAdmin(1L, reimbursementRequest, principal);

        verify(validationHelper, times(1)).validate(reimbursementRequest);
        verify(reimbursementRepository, times(1)).save(any(Reimbursement.class));
        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
    }

    @Test
    public void testRemoveReimbursementByAdminWhenAllParametersAreValidThenNoReturn() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByAdmin(1L, "123");

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    public void testRemoveReimbursementByUserWhenAllParametersAreValidThenNoReturn() {
        when(employeeRepository.findByClientId(anyString())).thenReturn(Optional.of(employee));
        when(reimbursementRepository.findFirstByEmployeeAndReimbursementId(any(Employee.class), anyLong())).thenReturn(Optional.of(reimbursement));

        reimbursementService.removeReimbursementByUser(1L, principal);

        verify(reimbursementRepository, times(1)).delete(any(Reimbursement.class));
    }

    @Test
    public void testGetReimbursementsWithStatusFalseWhenStatusIsFalseThenReturnReimbursementResponseList() {
        when(reimbursementRepository.findByStatusFalse()).thenReturn(Collections.singletonList(reimbursement));

        List<ReimbursementResponse> responses = reimbursementService.getReimbursementsWithStatusFalse();

        verify(reimbursementRepository, times(1)).findByStatusFalse();
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(reimbursement.getReimbursementId(), responses.get(0).getReimbursementId());
    }

    @Test
    public void testToReimbursementResponseWhenReimbursementIsValidThenReturnReimbursementResponse() {
        ReimbursementResponse response = reimbursementService.toReimbursementResponse(reimbursement);

        assertNotNull(response);
        assertEquals(reimbursement.getReimbursementId(), response.getReimbursementId());
    }

    @Test
    public void testConvertRupiahWhenBigDecimalValueIsValidThenReturnString() {
        String result = reimbursementService.convertRupiah(BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertTrue(result.contains("Rp"));
    }
}
