package Util.Util;

import Provider.ConfigFileProvider;
import Provider.ServerLog;
import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JiraUtil {
    private static ConfigFileProvider configFileProvider = ConfigFileProvider.instance();

    public static void createBug(IssueInputBuilder issueInputBuilder, List<ServerLog> serverLogList) throws URISyntaxException {
        URI jiraServerUri = getUri(configFileProvider.getString("baseUri"));
        JiraRestClient restClient = getClient(jiraServerUri, configFileProvider.getString("username"), configFileProvider.getString("password"));
        serverLogList.forEach(serverLog -> {
            File file = new File(serverLog.getFilePath());
            if (file.exists() && file.length() > 0) {
                issueInputBuilder.setSummary(configFileProvider.getString("summary") + "(" + serverLog.getExceptionName() + ")" + serverLog.getDateStr());
                IssueInput issueInput = issueInputBuilder.build();
                BasicIssue basicIssue = createIssue(restClient, issueInput);
                System.out.println(basicIssue.getKey() + "创建成功！");
                URI fileUri = null;
                try {
                    fileUri = new URI(jiraServerUri+configFileProvider.getString("issueUri") + basicIssue.getKey() + "/attachments");
                    upLoadFile(restClient, fileUri, file);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        System.exit(0);
    }

    public static URI getUri(String uri) throws URISyntaxException {
        return new URI(uri);
    }

    private static JiraRestClient getClient(URI uri, String user, String password) {
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(uri, user, password);
    }

    // 得到所有项目信息
    private static void getAllProjects(JiraRestClient restClient)
            throws InterruptedException, ExecutionException {

        Promise<Iterable<BasicProject>> list = restClient
                .getProjectClient().getAllProjects();
        Iterable<BasicProject> a = list.get();
        Iterator<BasicProject> it = a.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

    }

    // 得到单一项目信息
    private static void getProject(JiraRestClient restClient,
                                   String porjectKEY) throws InterruptedException, ExecutionException {
        Project project = restClient.getProjectClient()
                .getProject(porjectKEY).get();
        System.out.println(project);

    }

    // 得到单一问题信息
    private static void getIssue(JiraRestClient restClient,
                                 String issueKEY) throws InterruptedException, ExecutionException {

        Promise<Issue> list = restClient.getIssueClient()
                .getIssue(issueKEY);
        Issue issue = list.get();
        System.out.println(issue);

    }

    // 创建问题
    public static BasicIssue createIssue(JiraRestClient jiraRestClient,
                                         IssueInput newIssue) {
        BasicIssue basicIssue = jiraRestClient.getIssueClient().createIssue(newIssue).claim();
        return basicIssue;
    }

    //添加备注到问题
    public static void addCommentToIssue(JiraRestClient jiraRestClient, Issue issue, String comment) {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();
        issueClient.addComment(issue.getCommentsUri(), Comment.valueOf(comment)).claim();
    }

    //上传文件
    public static void upLoadFile(JiraRestClient jiraRestClient, URI uri, File file) throws InterruptedException {
        IssueRestClient issueClient = jiraRestClient.getIssueClient();
        issueClient.addAttachments(uri, file);
        Thread.sleep(2000);
    }


    //通过标题获取问题
    public static Iterable findIssuesByLabel(JiraRestClient jiraRestClient, String label) {
        SearchRestClient searchClient = jiraRestClient.getSearchClient();
        String jql = "labels%3D" + label;
        SearchResult results = ((SearchRestClient) jiraRestClient).searchJql(jql).claim();
        return results.getIssues();
    }

    //通过KEY获取问题
    public static Issue findIssueByIssueKey(JiraRestClient jiraRestClient, String issueKey) {
        SearchRestClient searchClient = jiraRestClient.getSearchClient();
        String jql = "issuekey = \"" + issueKey + "\"";
        SearchResult results = searchClient.searchJql(jql).claim();
        return (Issue) results.getIssues().iterator().next();
    }

    //获取问题的所有字段
    private static void getIssueFields(JiraRestClient restClient,
                                       String issueKEY) throws InterruptedException, ExecutionException {
        try {

            Promise<Issue> list = restClient.getIssueClient()
                    .getIssue(issueKEY);
            Issue issue = list.get();
            Iterable<Field> fields = issue.getFields();
            Iterator<Field> it = fields.iterator();
            while (it.hasNext()) {
                System.out.println(it.next());
            }

        } finally {
        }
    }

    //获取user
    private static BasicUser getUser(URI uri, String user, String displayName) {
        return new BasicUser(uri, user, displayName);
    }


}

