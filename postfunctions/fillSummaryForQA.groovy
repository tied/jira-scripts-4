import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.UpdateIssueRequest;
import com.atlassian.servicedesk.api.requesttype.RequestTypeService
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.avoristravel.fillSummaryForQA")
log.setLevel(Level.DEBUG);

@WithPlugin("com.atlassian.servicedesk")
def requestTypeService = ComponentAccessor.getOSGiComponentInstanceOfType(RequestTypeService);
def reqQ = requestTypeService.newQueryBuilder().issue(issue.id).build();
def reqT = requestTypeService.getRequestTypes(ComponentAccessor.getJiraAuthenticationContext().getUser(), reqQ);
def requestType = reqT.right.results[0].getName();

//Issue issue = ComponentAccessor.getIssueManager().getIssueObject('SOP-6481');
Issue issue = issue;
log.debug "Event issue = " + issue.getKey();
//log.debug("Request type = " + requestType);

if(requestType == "Nueva Entrada en SD") {
	String projectField = "Proyecto";
	String firstLevel = "1er Nivel";
	String secondLevel = "2do Nivel";
	CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

	log.debug projectField + " : " + customFieldManager.getCustomFieldObjectByName(projectField).getValue(issue);
	log.debug firstLevel + " : " + customFieldManager.getCustomFieldObjectByName(firstLevel).getValue(issue);
	log.debug secondLevel + " : " + customFieldManager.getCustomFieldObjectByName(secondLevel).getValue(issue);

	issue.setSummary(issue.summary + " " + customFieldManager.getCustomFieldObjectByName(projectField).getValue(issue) + "/" + customFieldManager.getCustomFieldObjectByName(firstLevel).getValue(issue) + "/" + customFieldManager.getCustomFieldObjectByName(secondLevel).getValue(issue));

	ComponentAccessor.getIssueManager().updateIssue(
			ComponentAccessor.getJiraAuthenticationContext().getUser()
			, issue
			, UpdateIssueRequest.builder().eventDispatchOption(EventDispatchOption.ISSUE_UPDATED).sendMail(false).build()
	);
}
