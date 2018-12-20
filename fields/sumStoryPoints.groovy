import com.atlassian.jira.ComponentManager
import com.atlassian.jira.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor;
def componentManager = ComponentManager.getInstance()
def issueLinkManager = ComponentAccessor.getIssueLinkManager()
def cfManager = ComponentAccessor.getCustomFieldManager()
double totalSP = 0
customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10006");
enableCache = {-> false}

issueLinkManager.getOutwardLinks(issue.id)?.each {issueLink ->
if (issueLink.issueLinkType.name == "Epic-Story Link" && !issueLink.getDestinationObject().getStatus().getName().equalsIgnoreCase('HECHA') && !issueLink.getDestinationObject().getStatus().getName().equalsIgnoreCase('DESCARTADA')){
double SP = (double)(issueLink.destinationObject.getCustomFieldValue(customField) ?: 0)
totalSP = SP + totalSP;
}}
return totalSP
