package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.postgresql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public record PostgreSqlConfig(
        String host,
        int port,
        String database,
        String tablePrefix,
        String user,
        String passwd,
        boolean useSsl,
        int maxConnection
) {
    public HikariDataSource createDataSource() {
        var config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(jdbcUrl());

        if (!user.isEmpty()) {
            config.setUsername(user);
        }

        if (!passwd.isEmpty()) {
            config.setPassword(passwd);
        }

        config.setMaximumPoolSize(Math.max(Runtime.getRuntime().availableProcessors(), maxConnection));
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1));
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(10));
        config.setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(1));

        var props = new Properties();
        props.setProperty("dataSource.cachePrepStmts", "true");
        props.setProperty("dataSource.prepStmtCacheSize", "250");
        props.setProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        props.setProperty("dataSource.useServerPrepStmts", "true");
        props.setProperty("dataSource.useLocalSessionState", "true");
        props.setProperty("dataSource.rewriteBatchedStatements", "true");
        props.setProperty("dataSource.cacheResultSetMetadata", "true");
        props.setProperty("dataSource.cacheServerConfiguration", "true");
        props.setProperty("dataSource.elideSetAutoCommits", "true");
        props.setProperty("dataSource.maintainTimeStats", "false");

        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }

    public String jdbcUrl() {
        return "jdbc:postgresql://"
                + host + ":" + port + "/" + database
                + "?ssl=" + useSsl;
    }
}
