import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.opensymphony.workflow.InvalidInputException

 def customFieldManager = ComponentAccessor.getCustomFieldManager()
 def epicLinkCf = customFieldManager.getCustomFieldObjectByName("Epic Link")
 def epicIssue = issue.getCustomFieldValue(epicLinkCf) as Issue

 if (!epicIssue) {
	return  true;
 }
 else if (epicIssue && epicIssue.status.name.equalsIgnoreCase("EN PROGRESO")) {
	return  true;
 }
 else{
 	return false;
 }
