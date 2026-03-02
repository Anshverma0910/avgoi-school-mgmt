package com.avgoi.schoolmgmt.tenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Multi-Tenant Guard: extracts X-TenantID and X-AcademicYear, sets TenantContext.
 * For /api/** (except POST /api/schools), returns 400 if X-TenantID is missing.
 * Clears context in afterCompletion to prevent data leaking between requests.
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    public static final String TENANT_HEADER = "X-TenantID";
    public static final String ACADEMIC_YEAR_HEADER = "X-AcademicYear";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String tenantId = request.getHeader(TENANT_HEADER);
        String academicYear = request.getHeader(ACADEMIC_YEAR_HEADER);

        if ((tenantId == null || tenantId.isBlank()) && !isSchoolRegistrationPost(request)) {
            log.warn("Tenant guard: missing X-TenantID for {} {} -> 400 Bad Request",
                    request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    Map.of("error", "Bad Request", "message", "X-TenantID header is required")));
            return false;
        }

        TenantContext.setCurrentTenantId(tenantId);
        TenantContext.setCurrentAcademicYear(academicYear);
        log.debug("Tenant guard: {} {} -> tenantId={}, academicYear={}",
                request.getMethod(), request.getRequestURI(), tenantId, academicYear);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        TenantContext.clear();
    }

    private static boolean isSchoolRegistrationPost(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/schools".equals(request.getRequestURI());
    }
}
