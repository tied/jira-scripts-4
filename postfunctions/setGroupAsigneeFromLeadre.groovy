import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.customfields.view.CustomFieldParams
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.customfields.view.CustomFieldParams
import com.atlassian.jira.issue.customfields.impl.CascadingSelectCFType
import com.atlassian.jira.issue.customfields.option.LazyLoadedOption

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
CustomField groupLeader = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12400");
def groupManager = ComponentAccessor.getGroupManager()
def group
Object value = issue.getCustomFieldValue(groupLeader);
log.debug "groupLeader = " + value.toString();

group = groupManager.getGroup(value.toString())


groupAssignee.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(groupAssignee), [group]), new DefaultIssueChangeHolder())
