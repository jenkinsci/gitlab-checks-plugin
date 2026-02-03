package io.jenkins.plugins.gitlabchecks;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.checks.api.ChecksPublisher;
import io.jenkins.plugins.checks.api.ChecksPublisherFactory;
import java.util.Optional;

@Extension
public class GitLabChecksPublisherFactory extends ChecksPublisherFactory {

    @Override
    protected Optional<ChecksPublisher> createPublisher(Job<?, ?> job, TaskListener listener) {
        GitLabChecksContext context = new GitLabChecksContext(job);
        return createPublisher(context, listener);
    }

    @Override
    protected Optional<ChecksPublisher> createPublisher(Run<?, ?> run, TaskListener listener) {
        GitLabChecksContext context = new GitLabChecksContext(run);
        return createPublisher(context, listener);
    }

    private Optional<ChecksPublisher> createPublisher(GitLabChecksContext context, TaskListener listener) {
        if (!context.isValid()) {
            return Optional.empty();
        }

        return Optional.of(new GitLabChecksPublisher(context, listener));
    }
}
