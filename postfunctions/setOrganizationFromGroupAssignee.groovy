import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.servicedesk.api.organization.CustomerOrganization;
import com.atlassian.servicedesk.api.organization.*;
import com.atlassian.servicedesk.api.ServiceDeskManager;
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.servicedesk.api.organization.OrganizationService
import com.atlassian.servicedesk.api.organization.OrganizationsQuery
import com.atlassian.fugue.Option
import com.atlassian.servicedesk.api.util.paging.LimitedPagedRequest
import com.atlassian.servicedesk.api.util.paging.LimitedPagedRequestImpl
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;

@PluginModule
ServiceDeskManager serviceDeskManager;
@PluginModule
OrganizationService organizationService

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def serviceDeskProject = serviceDeskManager.getServiceDeskForProject(issue.getProjectObject());
def serviceDeskId = serviceDeskProject.right.id as Integer
def CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

CustomField cf = customFieldManager.getCustomFieldObject("customfield_10103");
CustomField groupAssignee = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10205");
def groupAssigneeValue = (List<ImmutableGroup>) issue.getCustomFieldValue(groupAssignee);
def groupAssigneeValueString
if(null != groupAssigneeValue && null != groupAssigneeValue.get(0))
	groupAssigneeValueString = groupAssigneeValue.get(0).getName().toString();

if(null!= groupAssigneeValueString && (groupAssigneeValueString.equalsIgnoreCase("SOMETEAM"))){
    def organizationsQuery = new OrganizationsQuery() {
	 @Override
	 Option < Integer > serviceDeskId() {
	  return new Option.Some <Integer>(serviceDeskId)
	 }
	 @Override
	 LimitedPagedRequest pagedRequest() {
	  return new LimitedPagedRequestImpl(0, 50, 100)
	 }
	}

def organizationsToAdd
if(groupAssigneeValueString.contains("SOMETEAM"))
	organizationsToAdd = organizationService.getOrganizations(currentUser, organizationsQuery).right.results.find {it.name == "SOMETEAM"}

cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), [organizationsToAdd]), new DefaultIssueChangeHolder())
}

