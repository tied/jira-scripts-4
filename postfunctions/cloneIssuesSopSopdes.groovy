//https://jira.barceloviajes.com/browse/CIS-1487

// Post functions en el workflow (son dos)
// Clonar incidencia SOP a SOPDES incidencia --> bug
// Clonar incidencia SOP a SOPDES !incidencia --> task

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;


// Clonar incidencia SOP a SOPDES incidencia --> bug
if(issue.projectObject.key.equals('SOP')
		&& !issue.issueType.name.toLowerCase().equals('incidence')
) {
	return checkGroupAssignee();
}

return false;


// Clonar incidencia SOP a SOPDES !incidencia --> task
if(issue.projectObject.key.equals('SOP')
		&& !issue.issueType.name.toLowerCase().equals('incidence')
) {
	return checkGroupAssignee();
}

return false;


// Not tested with the outside function.
// https://jira.barceloviajes.com/secure/admin/workflows/EditWorkflowTransitionPostFunctionParams!default.jspa
boolean checkGroupAssignee() {
	String currentGroupAssignee;
	String groupAssigneeFieldId = "customfield_10205";
	def groupAssigneeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(groupAssigneeFieldId);
	def custom = (List<ImmutableGroup>)issue.getCustomFieldValue(groupAssigneeField);
	custom.each { v ->
		currentGroupAssignee = ((ImmutableGroup)v).getName();
	}

	if(null != currentGroupAssignee && (
			"Equipo HelpDesk".equals(currentGroupAssignee)
					|| "Equipo Operaciones".equals(currentGroupAssignee)
					|| "Equipo Cruceros Producto".equals(currentGroupAssignee)
	)) {
		return false;
	}

	return true;
}
