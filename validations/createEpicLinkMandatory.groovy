import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;

def customFieldManager = ComponentAccessor.getCustomFieldManager();
def optionsManager = ComponentAccessor.getOptionsManager();
CustomField epicLinkCf = customFieldManager.getCustomFieldObjectByName("Epic Link");
def epicIssue = issue.getCustomFieldValue(epicLinkCf) as String;

return (null != epicIssue && !"".equals(epicIssue));
