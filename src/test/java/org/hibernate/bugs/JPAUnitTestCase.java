package org.hibernate.bugs;

import org.hibernate.testing.TestForIssue;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JPAUnitTestCase {

    @ClassRule
    public static final MSSQLServerContainer sqlServer;
    private static final TimeZone defaultTimeZone = TimeZone.getDefault();

    static {
        sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-latest-ubuntu")
                .withPassword("secr3t?!");
        sqlServer.setPortBindings(singletonList("1433:1433"));
    }

    private EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
    }

    @After
    public void destroy() {
        TimeZone.setDefault(defaultTimeZone);
        entityManagerFactory.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13369")
    public void hhh13369test() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC"))); // force time zone to UTC for predictable results
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        OffsetDateTime now = OffsetDateTime.now(Clock.fixed(Instant.EPOCH, ZoneId.of("CET")));
        EntityWithOffsetTimestamp entity = new EntityWithOffsetTimestamp(0, now);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        assertThat("Persisted time should be the same as the original", entity.getCreatedAt(), equalTo(now));

        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
