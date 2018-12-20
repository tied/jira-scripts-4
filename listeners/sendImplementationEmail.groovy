import com.atlassian.jira.component.ComponentAccessor;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;

def log = Logger.getLogger("com.avoristravel.cis.SendImplementationEmail");
log.setLevel(Level.DEBUG);

//Issue issue = ComponentAccessor.getIssueManager().getIssueObject('IMP-9337');
Issue issue = event.issue;
//log.debug "Event " + event.toString();
String groupAssigneeFieldId = "customfield_10205";
def deployId = 10000;
def deployProId = 10002;
String currentGroupAssignee;
String recipients = "alejandro.alvarez@avoristravel.com,j.vich@avoristravel.com,joan.barcelo@avoristravel.com," +
		"instalaciones@avoristravel.com,qa.resp@avoristravel.com";
def assignation = ["Equipo Transportes":"transportenginesystems@avoristravel.com",
                   "Equipo Leo":"leo@avoristravel.com",
                   "Equipo Booking Engine":"bookingengine@avoristravel.com",
                   "Equipo Integraciones Hoteles":"polaris.dev@avoristravel.com",
                   "Equipo Front": "front@avoristravel.com",
                   "Equipo Maestros": "maestros@avoristravel.com"];

String projectKey = issue.getProjectObject().getOriginalKey();
log.info("Issue = " + issue.getKey() + " that belongs to " + projectKey + " project");

def groupAssigneeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(groupAssigneeFieldId);
def custom = (List<ImmutableGroup>) issue.getCustomFieldValue(groupAssigneeField);
custom.each { v ->
	currentGroupAssignee = ((ImmutableGroup) v).getName()
	log.debug "Current group assignee : " + currentGroupAssignee;
	if (assignation.containsKey(currentGroupAssignee)) {
		log.debug "Send email to " + assignation.get(currentGroupAssignee);
		recipients += "," + assignation.get(currentGroupAssignee);
	}
}

log.debug "event.getEventTypeId() : " + event.getEventTypeId();
String dueDateString = "";
if(deployId == event.getEventTypeId() && issue.getDueDate()) {
	dueDateString = "Fecha prevista de instalaci&oacute;n: " + issue.getDueDate().format("dd-MM-yyyy");
}
log.debug "dueDateString : " + dueDateString;


// [$!issue.getIssueType().name] PRUEBAS - [$!customfield_10205] - [$!issue.getComponents().get(0).name] - [$!issue.summary]
StringBuilder summaryBuilder = new StringBuilder()
	.append("[${issue.getIssueType().getName()}]");
if(deployProId == event.getEventTypeId()) {
	summaryBuilder.append(" PRODUCCIÓN - ");
} else {
	summaryBuilder.append(" PRUEBAS - ");
}
summaryBuilder.append("[${currentGroupAssignee}] - [${issue.getComponents()[0].getName()}] - [${issue.getSummary()}]");

String description = "";
if(issue.getDescription()){
	description = issue.getDescription();
	summaryBuilder.append("[${description}]");
}

log.debug summaryBuilder.toString();

StringBuilder subtaskBuilder = new StringBuilder();
def subTasks = issue.getSubTaskObjects();
for(def s : subTasks) {
	log.debug "subtask " + s.getKey()
	subtaskBuilder.append("<ol style=\"padding-top:1.2em;\"><a title=\"${s.getKey()}\" href=\"jira.barceloviajes.com/browse/${s.getKey()}\">${s.getSummary()}</a></ol>")
			.append(System.lineSeparator());
}

// add values to config to get accessible via template
config.groupAssignee = currentGroupAssignee;
config.issue = issue;
config.dueDateString = dueDateString;
config.subTasks = subtaskBuilder.toString();
config.summary = summaryBuilder.toString();
config.description = description;
mail.setTo(recipients);

return true;

