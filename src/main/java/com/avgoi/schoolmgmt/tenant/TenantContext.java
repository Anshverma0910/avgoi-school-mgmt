package com.avgoi.schoolmgmt.tenant;

/**
 * ThreadLocal holder for the current tenant and academic year (Multi-Tenant Guard for AVGOI).
 * Clear in afterCompletion to prevent data leaking between requests.
 */
public final class TenantContext {

    private static final ThreadLocal<String> currentTenantId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentAcademicYear = new ThreadLocal<>();

    private TenantContext() {
    }

    public static String getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void setCurrentTenantId(String tenantId) {
        currentTenantId.set(tenantId);
    }

    public static String getCurrentAcademicYear() {
        return currentAcademicYear.get();
    }

    public static void setCurrentAcademicYear(String academicYear) {
        currentAcademicYear.set(academicYear);
    }

    /** Clears both thread locals to prevent data leaking between requests. */
    public static void clear() {
        currentTenantId.remove();
        currentAcademicYear.remove();
    }

    // Aliases for existing code
    public static String getCurrentTenant() { return getCurrentTenantId(); }
    public static void setCurrentTenant(String tenantId) { setCurrentTenantId(tenantId); }
    public static String getCurrentYear() { return getCurrentAcademicYear(); }
    public static void setCurrentYear(String year) { setCurrentAcademicYear(year); }
    public static String getTenantId() { return getCurrentTenantId(); }
    public static void setTenantId(String tenantId) { setCurrentTenantId(tenantId); }
}
