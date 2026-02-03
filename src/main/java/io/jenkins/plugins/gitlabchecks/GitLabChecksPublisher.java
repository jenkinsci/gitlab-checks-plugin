package io.jenkins.plugins.gitlabchecks;

import static io.jenkins.plugins.gitlabchecks.CheckStatusToGitLabCommitStatus.makeBuildStatus;
import static io.jenkins.plugins.gitlabchecks.CheckStatusToGitLabCommitStatus.makeCommitStatus;

import hudson.model.TaskListener;
import io.jenkins.plugins.checks.api.ChecksDetails;
import io.jenkins.plugins.checks.api.ChecksPublisher;
import java.io.PrintWriter;
import java.util.Optional;
import org.gitlab4j.api.CommitsApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitStatus;
import org.gitlab4j.models.Constants;
import org.jspecify.annotations.NonNull;

public class GitLabChecksPublisher extends ChecksPublisher {
    private final GitLabChecksContext context;
    private final TaskListener listener;

    public GitLabChecksPublisher(GitLabChecksContext context, TaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void publish(@NonNull ChecksDetails details) {
        long projectId = this.context.projectId();
        String commitSha = this.context.getHeadSha();

        if (commitSha == null) {
            listener.getLogger().println("[GitLab Checks] Unable to find commit SHA");
            return;
        }

        Optional<Constants.CommitBuildState> buildState = makeBuildStatus(details);

        if (buildState.isEmpty()) {
            this.listener
                    .getLogger()
                    .println(
                            "[GitLab Checks] Unable to convert check status and conclusion to GitLab commit build status");
            return;
        }

        Optional<CommitStatus> commitStatus = makeCommitStatus(details);
        if (commitStatus.isEmpty()) {
            return;
        }

        try (GitLabApi api = this.context.makeGitLabApi()) {
            CommitsApi commitsApi = api.getCommitsApi();

            commitsApi.addCommitStatus(projectId, commitSha, buildState.orElse(null), commitStatus.get());
        } catch (GitLabApiException e) {
            try (PrintWriter ignored = listener.error("GitLab check publish failed with", e)) {}
        }
    }
}
