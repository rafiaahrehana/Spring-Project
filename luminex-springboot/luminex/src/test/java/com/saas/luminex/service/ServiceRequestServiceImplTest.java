package com.saas.luminex.service;

import com.saas.luminex.dto.request.ServiceRequestCreateRequest;
import com.saas.luminex.dto.request.ServiceRequestUpdateRequest;
import com.saas.luminex.dto.response.ServiceRequestResponse;
import com.saas.luminex.entity.*;
import com.saas.luminex.enums.*;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.ServiceRepository;
import com.saas.luminex.repository.ServiceRequestRepository;
import com.saas.luminex.repository.UserRepository;
import com.saas.luminex.service.impl.ServiceRequestServiceImpl;
import com.saas.luminex.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceRequestService Unit Tests")
class ServiceRequestServiceImplTest {

    @Mock private ServiceRequestRepository requestRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private SecurityUtil securityUtil;

    @InjectMocks
    private ServiceRequestServiceImpl requestService;

    private User mockClient;
    private User mockEmployee;
    private com.saas.luminex.entity.Service mockService;
    private ServiceRequest mockRequest;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockClient = User.builder().id(1L).name("Badrul").email("badrul@test.com")
                .role(Role.CLIENT).isActive(true).build();

        mockEmployee = User.builder().id(2L).name("Emon").email("emon@test.com")
                .role(Role.EMPLOYEE).isActive(true).build();

        mockCategory = Category.builder().id(1L).name("Web Development").build();

        mockService = com.saas.luminex.entity.Service.builder()
                .id(1L).name("Website Redesign").price(BigDecimal.valueOf(5000))
                .priceType(PriceType.FIXED).category(mockCategory).build();

        mockRequest = ServiceRequest.builder()
                .id(1L).client(mockClient).service(mockService)
                .status(RequestStatus.PENDING).priority(Priority.NORMAL)
                .progress(0).workedHours(0).build();
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("createRequest() - creates and sends notification to client")
    void createRequest_success_sendsNotification() {
        ServiceRequestCreateRequest dto = new ServiceRequestCreateRequest();
        dto.setServiceId(1L);
        dto.setPriority(Priority.HIGH);

        when(securityUtil.getCurrentUser()).thenReturn(mockClient);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));
        when(requestRepository.save(any())).thenReturn(mockRequest);

        ServiceRequestResponse response = requestService.createRequest(dto);

        assertThat(response).isNotNull();
        verify(notificationService).send(
                eq(mockClient), anyString(), anyString(),
                eq(NotificationType.REQUEST_SUBMITTED));
    }

    @Test
    @DisplayName("createRequest() - invalid serviceId throws ResourceNotFoundException")
    void createRequest_invalidService_throws() {
        ServiceRequestCreateRequest dto = new ServiceRequestCreateRequest();
        dto.setServiceId(999L);

        when(securityUtil.getCurrentUser()).thenReturn(mockClient);
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.createRequest(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateRequest() - admin assigns employee and sends notification")
    void updateRequest_assignEmployee_notifiesEmployee() {
        ServiceRequestUpdateRequest dto = new ServiceRequestUpdateRequest();
        dto.setAssignedEmployeeId(2L);
        dto.setStatus(RequestStatus.IN_PROGRESS);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockEmployee));
        when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        requestService.updateRequest(1L, dto);

        verify(notificationService).send(
                eq(mockEmployee), anyString(), anyString(),
                eq(NotificationType.REQUEST_ASSIGNED));
    }

    @Test
    @DisplayName("updateRequest() - progress > 100 throws BadRequestException")
    void updateRequest_invalidProgress_throws() {
        ServiceRequestUpdateRequest dto = new ServiceRequestUpdateRequest();
        dto.setProgress(110);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));

        assertThatThrownBy(() -> requestService.updateRequest(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Progress must be between 0 and 100");
    }

    @Test
    @DisplayName("updateRequest() - assigning non-EMPLOYEE user throws BadRequestException")
    void updateRequest_assignNonEmployee_throws() {
        ServiceRequestUpdateRequest dto = new ServiceRequestUpdateRequest();
        dto.setAssignedEmployeeId(1L); // client ID

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockClient)); // CLIENT role

        assertThatThrownBy(() -> requestService.updateRequest(1L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("EMPLOYEE role");
    }

    @Test
    @DisplayName("updateRequest() - completing request notifies client about payment")
    void updateRequest_completed_notifiesClientPaymentDue() {
        ServiceRequestUpdateRequest dto = new ServiceRequestUpdateRequest();
        dto.setStatus(RequestStatus.COMPLETED);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(mockRequest));
        when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        requestService.updateRequest(1L, dto);

        verify(notificationService).send(
                eq(mockClient), anyString(), anyString(),
                eq(NotificationType.PAYMENT_DUE));
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteRequest() - non-existent ID throws ResourceNotFoundException")
    void deleteRequest_notFound_throws() {
        when(requestRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> requestService.deleteRequest(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
