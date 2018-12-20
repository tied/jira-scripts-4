import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.security.roles.ProjectRoleManager;


def team = ComponentAccessor.groupManager.getGroup("teamName");
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
def isInGroup = ComponentAccessor.groupManager.getUserNamesInGroup(team).contains(currentUser);
def projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager);
def role = projectRoleManager.getProjectRole("Administrators");
def isProjectAdmin = projectRoleManager.isUserInProjectRole(currentUser, role, underlyingIssue.getProjectObject());


CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
def dd = getFieldById("duedate");
def dueDate = underlyingIssue.dueDate;
def status = underlyingIssue.getStatus().name;

if (!isInGroup && !isProjectAdmin && status.equalsIgnoreCase("somestatus")){
    dd.setReadOnly(true);
    dd.setError("You can not edit due date field after issue is transitioned to somestatus. Only Team/project administrators can do this.")
} else {
    dd.setReadOnly(false);
    dd.clearError();
}
