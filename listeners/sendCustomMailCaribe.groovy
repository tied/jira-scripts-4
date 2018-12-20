import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField;
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.crowd.embedded.impl.ImmutableGroup;

def log = Logger.getLogger("com.acme.CreateSubtask")
Logger.getLogger("com.onresolve.jira.groovy").setLevel(Level.DEBUG)

log.setLevel(Level.DEBUG)
String groupAssigneeFieldId = "customfield_10205"
String currentGroupAssignee
def groupAssigneeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(groupAssigneeFieldId)
def custom = (List<ImmutableGroup>)issue.getCustomFieldValue(groupAssigneeField)
def result = false
custom.each { v ->
	currentGroupAssignee = ((ImmutableGroup)v).getName()
    log.debug currentGroupAssignee
	if (currentGroupAssignee == 'team')
    	result = true
}
log.debug result

result == true

//email template
La tarea de tipo <b>$issue.issueType.name</b> https://jira.barceloviajes.com/browse/$issue.key ha sido creada en SOPDES y asignada a Equipo Caribe</br>

<ul>
<li><b>Título:</b> $issue.summary</li>
<li><b>Descripción:</b> $issue.description</li>
</ul>

<em>Jira</em>

