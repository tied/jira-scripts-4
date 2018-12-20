import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import java.sql.Timestamp;

def cfManager = ComponentAccessor.getCustomFieldManager();
def cfInstallation = cfManager.getCustomFieldObject(11400L);
issue.setCustomFieldValue(cfInstallation, new Timestamp((new Date()).time));
