package Provider;

import Util.JiraUtil;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.IssueFieldId;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConfigFileProvider implements IConfigProvider {
    private static ConfigFileProvider s_instance = null;
    Map<String, Map<String, String>> caseMap = null;
    private static String CASE = "AUTO-001";
    public static String BaseURL = "http://qc.feifanuniv.com";

    public ConfigFileProvider() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(this.getClass().getClassLoader().getResourceAsStream("QcAutoSuite.xml"));
            doc.getDocumentElement().normalize();
            Element docElement = doc.getDocumentElement();
            NodeList caseNodeList = docElement.getElementsByTagName("Case");
            caseMap = new LinkedHashMap<>();
            for (int i = 0; i < caseNodeList.getLength(); i++) {
                Element caseElement = (Element) caseNodeList.item(i);
                Map<String, String> entryMap = new LinkedHashMap<String, String>();
                NodeList entryNodeList = caseElement.getElementsByTagName("Entry");
                for (int j = 0; j < entryNodeList.getLength(); j++) {
                    Element entryElement = (Element) entryNodeList.item(j);
                    entryMap.put(entryElement.getAttribute("Name"), entryElement.getTextContent());
                }
                caseMap.put(caseElement.getAttribute("Name"), entryMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigFileProvider instance() {
        if (s_instance == null) {
            try {
                s_instance = new ConfigFileProvider();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s_instance;
    }

    @Override
    public String getFullPath() {
        try {
            URI uri = this.getClass().getClassLoader().getResource(".").toURI().resolve("../../logs/");
            return uri.getPath().substring(1).replace("/", File.separator);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public IssueInputBuilder getIssueInputBuilder(String projectKey, String issueTypeId) {
        IssueInputBuilder issueBuilder = new IssueInputBuilder(projectKey, Long.valueOf(issueTypeId));

        String assignee = getString("assignee");
        String displayName = getString("displayName");
        if (assignee != null & displayName != null) {
            BasicUser assigneer = null;
            try {
                assigneer = new BasicUser(JiraUtil.getUri(BaseURL), assignee, displayName);
                issueBuilder.setAssignee(assigneer);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        issueBuilder.setSummary(getString("summary") + getDate());
        issueBuilder.setDescription(getString("description"));

        if (!getString("labels").isEmpty()) {
            List labelList = getLabels(getString("labels"));
            issueBuilder.setFieldValue(IssueFieldId.LABELS_FIELD.id, labelList);

        }

        return issueBuilder;
    }


    public String getString(String dataId) {
        return caseMap.get(CASE).get(dataId);
    }

    private List getLabels(String str) {
        String s[] = str.split("&");
        List list = new ArrayList();
        Collections.addAll(list, s);
        return list;
    }

    private static String getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(date);
    }
}
