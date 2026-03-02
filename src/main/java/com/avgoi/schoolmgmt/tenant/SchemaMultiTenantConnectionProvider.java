package com.avgoi.schoolmgmt.tenant;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.Wrapped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Runs SET search_path TO &lt;X-TenantID&gt;, public at the start of every session so queries hit the tenant schema first.
 */
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private static final Logger log = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider.class);

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        String schema = (tenantIdentifier != null && !tenantIdentifier.isBlank())
                ? quoteSchemaName(tenantIdentifier)
                : "public";
        try (Statement stmt = connection.createStatement()) {
            String sql = "SET search_path TO " + schema + ", public";
            stmt.execute(sql);
            log.info("Schema routing: {}", sql);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)
                || DataSource.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType)) {
            return unwrapType.cast(this);
        }
        if (DataSource.class.isAssignableFrom(unwrapType)) {
            return unwrapType.cast(dataSource);
        }
        throw new IllegalArgumentException("Cannot unwrap to " + unwrapType.getName());
    }

    private static String quoteSchemaName(String name) {
        if (name == null || name.isBlank()) {
            return "public";
        }
        return "\"" + name.replace("\"", "\"\"") + "\"";
    }
}
