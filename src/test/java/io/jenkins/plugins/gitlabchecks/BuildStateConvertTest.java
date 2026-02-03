package io.jenkins.plugins.gitlabchecks;

import static io.jenkins.plugins.gitlabchecks.CheckStatusToGitLabCommitStatus.makeBuildStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jenkins.plugins.checks.api.ChecksConclusion;
import io.jenkins.plugins.checks.api.ChecksDetails;
import io.jenkins.plugins.checks.api.ChecksStatus;
import java.util.Optional;
import java.util.stream.Stream;
import org.gitlab4j.models.Constants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BuildStateConvertTest {
    private static class TestCase {
        public final ChecksDetails Details;
        public final Optional<Constants.CommitBuildState> ExpectedState;

        public TestCase(ChecksStatus status, Constants.CommitBuildState expectedState) {
            this.Details =
                    new ChecksDetails.ChecksDetailsBuilder().withStatus(status).build();
            this.ExpectedState = Optional.ofNullable(expectedState);
        }

        public TestCase(ChecksStatus status, ChecksConclusion conclusion, Constants.CommitBuildState expectedState) {
            this.Details = new ChecksDetails.ChecksDetailsBuilder()
                    .withStatus(status)
                    .withConclusion(conclusion)
                    .build();
            this.ExpectedState = Optional.ofNullable(expectedState);
        }

        public TestCase(ChecksStatus status) {
            this.Details =
                    new ChecksDetails.ChecksDetailsBuilder().withStatus(status).build();
            this.ExpectedState = Optional.empty();
        }

        public TestCase(ChecksStatus status, ChecksConclusion conclusion) {
            this.Details = new ChecksDetails.ChecksDetailsBuilder()
                    .withStatus(status)
                    .withConclusion(conclusion)
                    .build();
            this.ExpectedState = Optional.empty();
        }

        @Override
        public String toString() {
            return String.format(
                    "Status: %s, Conclusion: %s => ExpectedState: %s",
                    Details.getStatus(), Details.getConclusion(), ExpectedState);
        }
    }

    @ParameterizedTest
    @MethodSource("generateOutputs")
    void testGenerateStatus(TestCase details) {
        Optional<Constants.CommitBuildState> actual = makeBuildStatus(details.Details);

        assertEquals(details.ExpectedState, actual, "For " + details);
    }

    private static Stream<TestCase> generateOutputs() {
        return Stream.of(
                new TestCase(ChecksStatus.NONE, Constants.CommitBuildState.PENDING),
                new TestCase(ChecksStatus.QUEUED, Constants.CommitBuildState.PENDING),
                new TestCase(ChecksStatus.IN_PROGRESS, Constants.CommitBuildState.RUNNING),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.NONE, Constants.CommitBuildState.PENDING),
                new TestCase(
                        ChecksStatus.COMPLETED, ChecksConclusion.ACTION_REQUIRED, Constants.CommitBuildState.PENDING),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.FAILURE, Constants.CommitBuildState.FAILED),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.NEUTRAL, Constants.CommitBuildState.SUCCESS),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.CANCELED, Constants.CommitBuildState.CANCELED),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.SKIPPED, Constants.CommitBuildState.SKIPPED),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.SUCCESS, Constants.CommitBuildState.SUCCESS),
                new TestCase(ChecksStatus.COMPLETED, ChecksConclusion.TIME_OUT, Constants.CommitBuildState.FAILED),
                new TestCase(ChecksStatus.NONE, Constants.CommitBuildState.PENDING));
    }
}
