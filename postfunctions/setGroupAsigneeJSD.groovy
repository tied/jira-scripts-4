import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.cis.log")
log.setLevel(Level.DEBUG)

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

@WithPlugin("com.atlassian.servicedesk")
def requestTypeService = ComponentAccessor.getOSGiComponentInstanceOfType(RequestTypeService)
def reqQ = requestTypeService.newQueryBuilder().issue(issue.id).build()
def reqT = requestTypeService.getRequestTypes(currentUser, reqQ)
def requestType = reqT.right.results[0].getName()

def issue = issue as MutableIssue
log.debug "Issue key = " + issue.getKey();
log.debug "Request type = " + requestType;

CustomField groupAssignee = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10205");
CustomField productResponsible = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12002");

def groupManager = ComponentAccessor.getGroupManager()
def group,groupResponsible

if(requestType == 'SOMEREQUESTYPE'){
	group = groupManager.getGroup("SOMETEAM")
	groupResponsible = group
}



log.debug group.name
log.debug groupResponsible.name

groupAssignee.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(groupAssignee), [group]), new DefaultIssueChangeHolder())
productResponsible.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(productResponsible), [groupResponsible]), new DefaultIssueChangeHolder())
