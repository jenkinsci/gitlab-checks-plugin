package io.jenkins.plugins.gitlabchecks;

import static io.jenkins.plugins.gitlabchecks.CheckStatusToGitLabCommitStatus.makeCommitStatus;
import static org.junit.jupiter.api.Assertions.*;

import io.jenkins.plugins.checks.api.ChecksDetails;
import io.jenkins.plugins.checks.api.ChecksOutput;
import java.time.*;
import java.util.Optional;
import org.gitlab4j.api.models.CommitStatus;
import org.junit.jupiter.api.Test;

public class CommitStatusConvertTest {
    @Test
    public void testNoCheckName() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder().build();
        Optional<CommitStatus> status = makeCommitStatus(check);
        assertTrue(status.isEmpty());
    }

    @Test
    void testConvertEverything() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withDetailsURL("http://check/url")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withTitle("title")
                        .withSummary("description")
                        .build())
                .withStartedAt(LocalDateTime.of(2026, 1, 12, 10, 30, 40))
                .withCompletedAt(LocalDateTime.of(2026, 1, 12, 11, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertEquals("http://check/url", status.getTargetUrl());
        assertEquals("description", status.getDescription());
        assertEquals("title", status.getStatus());

        ZonedDateTime expectedStartedAt = ZonedDateTime.of(2026, 1, 12, 10, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualStartedAt = status.getStartedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedStartedAt, actualStartedAt);

        ZonedDateTime expectedFinishedAt = ZonedDateTime.of(2026, 1, 12, 11, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualFinishedAt = status.getFinishedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedFinishedAt, actualFinishedAt);
    }

    @Test
    void testMissingDetailsUrl() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withTitle("title")
                        .withSummary("description")
                        .build())
                .withStartedAt(LocalDateTime.of(2026, 1, 12, 10, 30, 40))
                .withCompletedAt(LocalDateTime.of(2026, 1, 12, 11, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertNull(status.getTargetUrl());
        assertEquals("description", status.getDescription());
        assertEquals("title", status.getStatus());

        ZonedDateTime expectedStartedAt = ZonedDateTime.of(2026, 1, 12, 10, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualStartedAt = status.getStartedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedStartedAt, actualStartedAt);

        ZonedDateTime expectedFinishedAt = ZonedDateTime.of(2026, 1, 12, 11, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualFinishedAt = status.getFinishedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedFinishedAt, actualFinishedAt);
    }

    @Test
    void testMissingOutputDescription() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withDetailsURL("http://check/url")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withTitle("title")
                        .build())
                .withStartedAt(LocalDateTime.of(2026, 1, 12, 10, 30, 40))
                .withCompletedAt(LocalDateTime.of(2026, 1, 12, 11, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertEquals("http://check/url", status.getTargetUrl());
        assertNull(status.getDescription());
        assertEquals("title", status.getStatus());

        ZonedDateTime expectedStartedAt = ZonedDateTime.of(2026, 1, 12, 10, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualStartedAt = status.getStartedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedStartedAt, actualStartedAt);

        ZonedDateTime expectedFinishedAt = ZonedDateTime.of(2026, 1, 12, 11, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualFinishedAt = status.getFinishedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedFinishedAt, actualFinishedAt);
    }

    @Test
    void testMissingOutputStatus() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withDetailsURL("http://check/url")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withSummary("description")
                        .build())
                .withStartedAt(LocalDateTime.of(2026, 1, 12, 10, 30, 40))
                .withCompletedAt(LocalDateTime.of(2026, 1, 12, 11, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertEquals("http://check/url", status.getTargetUrl());
        assertEquals("description", status.getDescription());
        assertNull(status.getStatus());

        ZonedDateTime expectedStartedAt = ZonedDateTime.of(2026, 1, 12, 10, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualStartedAt = status.getStartedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedStartedAt, actualStartedAt);

        ZonedDateTime expectedFinishedAt = ZonedDateTime.of(2026, 1, 12, 11, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualFinishedAt = status.getFinishedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedFinishedAt, actualFinishedAt);
    }

    @Test
    void testMissingStartedAt() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withDetailsURL("http://check/url")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withTitle("title")
                        .withSummary("description")
                        .build())
                .withCompletedAt(LocalDateTime.of(2026, 1, 12, 11, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertEquals("http://check/url", status.getTargetUrl());
        assertEquals("description", status.getDescription());
        assertEquals("title", status.getStatus());

        assertNull(status.getStartedAt());

        ZonedDateTime expectedFinishedAt = ZonedDateTime.of(2026, 1, 12, 11, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualFinishedAt = status.getFinishedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedFinishedAt, actualFinishedAt);
    }

    @Test
    void testMissingCompletedAt() {
        ChecksDetails check = new ChecksDetails.ChecksDetailsBuilder()
                .withName("my-check-name")
                .withDetailsURL("http://check/url")
                .withOutput(new ChecksOutput.ChecksOutputBuilder()
                        .withTitle("title")
                        .withSummary("description")
                        .build())
                .withStartedAt(LocalDateTime.of(2026, 1, 12, 10, 30, 40))
                .build();
        Optional<CommitStatus> statusOpt = makeCommitStatus(check);

        assertTrue(statusOpt.isPresent());
        CommitStatus status = statusOpt.get();
        assertEquals("my-check-name", status.getName());
        assertEquals("http://check/url", status.getTargetUrl());
        assertEquals("description", status.getDescription());
        assertEquals("title", status.getStatus());

        ZonedDateTime expectedStartedAt = ZonedDateTime.of(2026, 1, 12, 10, 30, 40, 0, ZoneOffset.UTC);
        ZonedDateTime actualStartedAt = status.getStartedAt().toInstant().atZone(ZoneOffset.UTC);
        assertEquals(expectedStartedAt, actualStartedAt);

        assertNull(status.getFinishedAt());
    }
}
