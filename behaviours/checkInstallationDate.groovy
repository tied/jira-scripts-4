import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.security.roles.ProjectRoleManager;


def team = ComponentAccessor.groupManager.getGroup("team");
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
def isInGroup = ComponentAccessor.groupManager.getUserNamesInGroup(team).contains(currentUser);
def projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager);
def role = projectRoleManager.getProjectRole("Administrators");
def isProjectAdmin = projectRoleManager.isUserInProjectRole(currentUser, role, underlyingIssue.getProjectObject());


CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
def dd = getFieldById("customfield_11400");

if (!isInGroup && !isProjectAdmin){
    dd.setReadOnly(true);
} else {
    dd.setReadOnly(false);
}