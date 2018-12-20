import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.servicedesk.api.organization.OrganizationService
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
import com.atlassian.servicedesk.api.ServiceDeskManager;
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.crowd.embedded.api.Group


@PluginModule
ServiceDeskManager serviceDeskManager;
@PluginModule
OrganizationService organizationService

def log = Logger.getLogger("com.cis.log")
log.setLevel(Level.DEBUG)

def reporter = sourceIssue.reporter

def organisationService = ComponentAccessor.getOSGiComponentInstanceOfType(OrganizationService)
def serviceDeskProject = serviceDeskManager.getServiceDeskForProject(sourceIssue.getProjectObject());
def serviceDeskId = serviceDeskProject.right.id as Integer

log.debug "id del proyecto: " + serviceDeskId

def organizationQuery = organisationService.newOrganizationsQueryBuilder().serviceDeskId(serviceDeskId).build()
def organizationsInProjectResult = organisationService.getOrganizations(reporter, organizationQuery)
if (organizationsInProjectResult.isLeft()) {
    log.error organizationsInProjectResult.left().get()
    return
}

log.debug "organizationsInProjectResult: " + organizationsInProjectResult

def organizationPaquete = organizationsInProjectResult.right.results.find {it.name == "Equipo Integraciones Paquete"}
def organizationTransporte = organizationsInProjectResult.right.results.find {it.name == "Equipo Integraciones Transportes"}

def usersInOrganizationPaqueteQuery = organisationService.newUsersInOrganizationQuery().customerOrganization(organizationPaquete).build()
def usersInOrganizationTransporteQuery = organisationService.newUsersInOrganizationQuery().customerOrganization(organizationTransporte).build()

def usersInOrganizationPaquete = organisationService.getUsersInOrganization(reporter, usersInOrganizationPaqueteQuery)
if (usersInOrganizationPaquete.isLeft()) {
    log.error usersInOrganizationPaquete.left().get()
}

def usersInOrganizationTransporte = organisationService.getUsersInOrganization(reporter, usersInOrganizationTransporteQuery)
if (usersInOrganizationTransporte.isLeft()) {
    log.error usersInOrganizationPaquete.left().get()
}

def isUserInOrganizationPaquete = usersInOrganizationPaquete.right.results.find {it.key == reporter.key} ? true : false
log.debug "Is user in organizaion ? $isUserInOrganizationPaquete"
def isUserInOrganizationTransporte = usersInOrganizationTransporte.right.results.find {it.key == reporter.key} ? true : false
log.debug "Is user in organizaion ? $isUserInOrganizationTransporte"

def issue = issue as MutableIssue
def cf = ComponentAccessor.customFieldManager.getCustomFieldObjects(issue).find {it.name == 'Grupo Asignado'}
log.debug "cf group assignee: " + cf

def groupManager = ComponentAccessor.getGroupManager()


if(isUserInOrganizationPaquete){
	def group = groupManager.getGroup("Equipo Integraciones Paquete")
    issue.setCustomFieldValue(cf, [group])
}

if(isUserInOrganizationTransporte){
	def group = groupManager.getGroup("Equipo Integraciones Transportes")
    issue.setCustomFieldValue(cf, [group])
}

