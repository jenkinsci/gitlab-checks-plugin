package io.jenkins.plugins.gitlabchecks;

import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.gitlabbranchsource.BranchSCMRevision;
import io.jenkins.plugins.gitlabbranchsource.GitLabSCMSource;
import io.jenkins.plugins.gitlabbranchsource.MergeRequestSCMRevision;
import io.jenkins.plugins.gitlabbranchsource.helpers.GitLabHelper;
import jenkins.plugins.git.GitTagSCMRevision;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMRevisionAction;
import jenkins.scm.api.SCMSource;
import org.gitlab4j.api.GitLabApi;

public class GitLabChecksContext {
    private final Job<?, ?> job;
    private final Run<?, ?> run;

    public GitLabChecksContext(Job<?, ?> job) {
        this.job = job;
        this.run = null;
    }

    public GitLabChecksContext(Run<?, ?> run) {
        this.job = run.getParent();
        this.run = run;
    }

    public boolean isValid() {
        GitLabSCMSource gitLabSource = this.getSource();

        if (gitLabSource == null) {
            return false;
        }

        String credentialsId = gitLabSource.getCredentialsId();

        if (credentialsId == null) {
            return false;
        }

        if (this.run == null) {
            return false;
        }

        return true;
    }

    public GitLabApi makeGitLabApi() {
        GitLabSCMSource source = this.getSource();
        return GitLabHelper.apiBuilder(this.job, source.getServerName(), source.getCredentialsId());
    }

    public long projectId() {
        return this.getSource().getProjectId();
    }

    public String getHeadSha() {
        if (this.run == null) {
            return null;
        }

        GitLabSCMSource source = getSource();
        if (source == null) {
            return null;
        }

        SCMRevision revision = SCMRevisionAction.getRevision(source, this.run);

        if (revision instanceof BranchSCMRevision) {
            return ((BranchSCMRevision) revision).getHash();
        } else if (revision instanceof MergeRequestSCMRevision) {
            return ((MergeRequestSCMRevision) revision).getHeadHash();
        } else if (revision instanceof GitTagSCMRevision) {
            return ((GitTagSCMRevision) revision).getHash();
        }

        return null;
    }

    private GitLabSCMSource getSource() {
        SCMSource scmSource = SCMSource.SourceByItem.findSource(this.job);
        if (!(scmSource instanceof GitLabSCMSource)) {
            return null;
        }

        return (GitLabSCMSource) scmSource;
    }
}
