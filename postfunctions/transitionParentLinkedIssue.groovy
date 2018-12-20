import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.link.IssueLink;

def issueService = ComponentAccessor.getIssueService();
def linkType = ["clone"];

def user = ComponentAccessor.getJiraAuthenticationContext().getUser();
def linkMgr = ComponentAccessor.getIssueLinkManager();

for (IssueLink link in linkMgr.getInwardLinks(issue.id)) {
    def destIssue = link.getSourceObject();
    if (link.issueLinkType.name.contains(linkType)) {
        def destStatusObject = destIssue.getStatusObject()
	// Is the status of the linked issue "Installing" ?
        if (destStatusObject.name == "EN PROGRESO" || destStatusObject.name == "PENDIENTE") {
            // Prepare our input for the transition
            def issueInputParameters = issueService.newIssueInputParameters()
            issueInputParameters.with {
                setComment("Resuelta en proyecto Soporte Desarrollo")
                setSkipScreenCheck(true)
            }
            // Validate transitioning the linked issue to "Signs Needed"
            def validationResult = issueService.validateTransition(user, destIssue.id, 5, issueInputParameters)
            if (validationResult.isValid()) {
                // Perform the transition
                def issueResult = issueService.transition(user, validationResult)
                if (! issueResult.isValid()) {
                    log.warn("Failed to transition task ${destIssue.key}, errors: ${issueResult.errorCollection}")
                }
            } else {
                log.warn("Could not transition task ${destIssue.key}, errors: ${validationResult.errorCollection}")
            }
        } else {
            log.warn("Skipping link: ${link.issueLinkType.name} ${destIssue.key} ${destStatusObject.name} (wrong status)")
        }
    } else {
        log.warn("Skipping link: ${link.issueLinkType.name} ${destIssue.key} (wrong type)")
    }
}
