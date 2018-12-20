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
def group, groupResponsible

if(requestType == 'SOMEREQUEST'){
group = groupManager.getGroup("SOMETEAM")
}



groupResponsible = group

log.debug group.name
log.debug groupResponsible.name

if(requestType == 'Incidencia Paquete Dinamico' || requestType == 'Solicitud Paquete Dinamico' || requestType == 'Configurar un descuento o una regla de negocio de Paquete Dinamico' || requestType == 'Precios y comisiones de Paquete Dinamico' || requestType == 'Otros - Paquete Dinamico')
	groupResponsible = groupManager.getGroup("Equipo Paquete Dinamico")
else if(requestType == 'Guias destino' || requestType == 'Ficha Hotel Caribe' 
	|| requestType == 'Cobros y pagos Caribe' || requestType == 'Error gastos cancelacion Caribe' 
	|| requestType == 'Configurar un descuento o una regla de negocio caribe' || requestType == 'Precios y comisiones caribe')
	groupResponsible = groupManager.getGroup("Equipo Caribe")
else if(requestType == 'Cobros y pagos Circuitos' || requestType == 'Error gastos cancelacion Circuitos' 
	|| requestType == 'Configurar un descuento o una regla de negocio circuitos' || requestType == 'Precios y comisiones circuitos')
	groupResponsible = groupManager.getGroup("Equipo Circuitos")
else if(requestType == 'Desactivar cliente' || requestType == 'Reasignacion oficinas' 
	|| requestType == 'Consulta GDPR' || requestType == 'Otros - Cliente')
	groupResponsible = groupManager.getGroup("Equipo Cliente")
else if(requestType == 'Incidencia Cruceros')
	groupResponsible = groupManager.getGroup("Equipo Cruceros Producto")
else if(requestType == 'Consulta Front' || requestType == 'Configuracion de los Service Fee')
	groupResponsible = groupManager.getGroup("Equipo Front")
else if(requestType == 'Configurar un descuento o una regla de negocio hoteles' || requestType == 'Precios y comisiones hoteles')
	groupResponsible = groupManager.getGroup("Equipo Integraciones Hoteles")
else if(requestType == 'Configurar un descuento o una regla de negocio paquete' || requestType == 'Precios y comisiones paquete')
	groupResponsible = groupManager.getGroup("Equipo Booking Engine")
else if(requestType == 'Incidencia Paquete Dinamico' || requestType == 'Solicitud Paquete Dinamico' 
	|| requestType == 'Configurar un descuento o una regla de negocio de paquete dinamico' 
	|| requestType == 'Precios y comisiones de paquete dinamico' || requestType == 'Otros - Paquete Dinamico')
	groupResponsible = groupManager.getGroup("Equipo Paquete Dinamico")
else if(requestType == 'Configurar un descuento o una regla de negocio transporte' || requestType == 'Precios y comisiones transporte')
	groupResponsible = groupManager.getGroup("Equipo Integraciones Transportes")

groupAssignee.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(groupAssignee), [group]), new DefaultIssueChangeHolder())
productResponsible.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(productResponsible), [groupResponsible]), new DefaultIssueChangeHolder())

