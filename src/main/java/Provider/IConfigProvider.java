package Provider;

import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;

public interface IConfigProvider {
    String getFullPath();

    IssueInputBuilder getIssueInputBuilder(String projectKey, String issueTypeId);
}
