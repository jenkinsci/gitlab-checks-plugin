package io.jenkins.plugins.gitlabchecks;

import io.jenkins.plugins.checks.api.ChecksDetails;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import org.gitlab4j.api.models.CommitStatus;
import org.gitlab4j.models.Constants;
import org.jspecify.annotations.NonNull;

public class CheckStatusToGitLabCommitStatus {
    public static Optional<CommitStatus> makeCommitStatus(@NonNull ChecksDetails check) {
        Optional<String> name = check.getName();
        if (name.isEmpty()) {
            return Optional.empty();
        }

        CommitStatus status = new CommitStatus();
        status.setName(name.get());

        check.getDetailsURL().ifPresent(status::setTargetUrl);

        check.getOutput().ifPresent(output -> {
            output.getSummary().ifPresent(status::setDescription);
            output.getTitle().ifPresent(status::setStatus);
        });

        check.getStartedAt().map(d -> Date.from(d.toInstant(ZoneOffset.UTC))).ifPresent(status::setStartedAt);
        check.getCompletedAt().map(d -> Date.from(d.toInstant(ZoneOffset.UTC))).ifPresent(status::setFinishedAt);

        return Optional.of(status);
    }

    public static Optional<Constants.CommitBuildState> makeBuildStatus(@NonNull ChecksDetails details) {
        switch (details.getStatus()) {
            case NONE, QUEUED -> {
                return Optional.of(Constants.CommitBuildState.PENDING);
            }
            case IN_PROGRESS -> {
                return Optional.of(Constants.CommitBuildState.RUNNING);
            }
            case COMPLETED -> {
                switch (details.getConclusion()) {
                    case NONE, ACTION_REQUIRED -> {
                        return Optional.of(Constants.CommitBuildState.PENDING);
                    }
                    case SKIPPED -> {
                        return Optional.of(Constants.CommitBuildState.SKIPPED);
                    }
                    case CANCELED -> {
                        return Optional.of(Constants.CommitBuildState.CANCELED);
                    }
                    case TIME_OUT, FAILURE -> {
                        return Optional.of(Constants.CommitBuildState.FAILED);
                    }
                    case NEUTRAL, SUCCESS -> {
                        return Optional.of(Constants.CommitBuildState.SUCCESS);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
