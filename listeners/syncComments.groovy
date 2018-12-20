import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.AbstractIssueEventListener
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.user.DelegatingApplicationUser;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.managers.DefaultAttachmentManager;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.util.PathUtils;
import org.apache.log4j.Logger
import org.apache.log4j.Level
  
def log = Logger.getLogger("com.acme.CreateSubtask")
log.setLevel(Level.DEBUG)
final SD_PUBLIC_COMMENT = "sd.public.comment";

def issue = event.issue; // event is an IssueEvent
log.debug "Event issue = " + issue.getKey();
def issueLinkManager = ComponentAccessor.getIssueLinkManager();
def issueIndexManager = ComponentAccessor.getComponent(IssueIndexManager)
def commentManager = ComponentAccessor.getCommentManager();
def issueManager = ComponentAccessor.getIssueManager();
def attachmentManager = ComponentAccessor.getAttachmentManager();
def pathManager = ComponentAccessor.getAttachmentPathManager();
def roleLevelId
if(null != event.comment)
	roleLevelId = event.comment.getRoleLevelId()
log.debug("roleLevelId: " + roleLevelId)

def isInternal = true
if(null != roleLevelId && roleLevelId == 10500)
	isInternal = false

def internalAttachments = [:];
attachmentManager.getAttachments(issue).each {attachment ->
	internalAttachments.put(attachment.getFilename(), attachment);
}

def outwardAttachments = [:];
issueLinkManager.getOutwardLinks(issue.id).each {issueLinkOutward ->
    if(issueLinkOutward.getIssueLinkType().getName().contains("Clone")){
        def comments = ComponentAccessor.getCommentManager()?.getComments(issue);
        if (comments) {
        	if(null != event.comment && null != event.comment.body && !event.comment.body.startsWith("SOPORTE: ")){
                def properties = [(SD_PUBLIC_COMMENT): new JSONObject(["internal": isInternal] as Map)];
                commentManager.create(issueLinkOutward.getDestinationObject(), event.comment.authorApplicationUser, "DESARROLLO: "+event.comment.body, null, null, new Date(), properties, true);
            }
        }

	    attachmentManager.getAttachments(issueLinkOutward.getDestinationObject()).each { attachment ->
		    outwardAttachments.put(attachment.getFilename(), attachment);
	    }
	    internalAttachments.each { k,v ->
		    if(!outwardAttachments.containsKey(k)) {
			    copyAttachments((DefaultAttachmentManager)attachmentManager,
					    (Attachment)v,
					    (Issue)issue,
					    pathManager,
					    issueLinkOutward.getDestinationObject().getKey().toString(),
					    (DelegatingApplicationUser)ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
			    );
		    }
	    }
    }
}

def inwardAttachments = [:];
issueLinkManager.getInwardLinks(issue.id).each {issueLinkInward ->
    if(issueLinkInward.getIssueLinkType().getName().contains("Clone")){
        def comments = ComponentAccessor.getCommentManager()?.getComments(issue);
        if (comments) {
        	if(null!= event.comment && null != event.comment.body && !event.comment.body.startsWith("DESARROLLO: ")){
        		def properties = [(SD_PUBLIC_COMMENT): new JSONObject(["internal": isInternal] as Map)];
            	commentManager.create(issueLinkInward.getSourceObject(), event.comment.authorApplicationUser, "SOPORTE: "+event.comment.body, null, null, new Date(), properties, true);
        		}
            }
        //finally copy attachments
	    attachmentManager.getAttachments(issueLinkInward.getSourceObject()).each { attachment ->
		    inwardAttachments.put(attachment.getFilename(), attachment);
	    }
	    internalAttachments.each { k,v ->
		    if(!inwardAttachments.containsKey(k)) {
			    copyAttachments((DefaultAttachmentManager)attachmentManager,
					    (Attachment)v,
					    (Issue)issue,
					    pathManager,
					    issueLinkInward.getSourceObject().getKey().toString(),
					    (DelegatingApplicationUser)ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
			    );
		    }
	    }
	}
}

def copyAttachments(DefaultAttachmentManager manager,
                    Attachment attachment,
                    Issue issue,
                    AttachmentPathManager pathManager,
                    String destination,
                    DelegatingApplicationUser user) {

	def log = Logger.getLogger("com.acme.CreateSubtask.copyAttachments")
	log.setLevel(Level.DEBUG)
	log.debug "Copy " + attachment.getFilename() + " to " + destination;
	String[] buckets = ["10000", "20000", "30000", "40000", "50000"];
	for(int i = 0; i < buckets.size(); i++){
		def finalProjectKey = issue.projectObject.key;
		if(issue.projectObject.key.equals("SOP")){
			finalProjectKey = "SOP2017";
		}
		else if(issue.projectObject.key.equals("SOPDES")){
			finalProjectKey = "SOPDES2017";
		}
		def finalIssueKey = issue.key;
		if(issue.key.contains("SOP-")){
			finalIssueKey = "SOP2017-"+issue.key.split('-')[1];
		}
		else if(issue.key.contains("SOPDES-")){
			finalIssueKey = "SOPDES2017-"+issue.key.split('-')[1];
		}
		def filePath = PathUtils.joinPaths(pathManager.attachmentPath, finalProjectKey, buckets[i], finalIssueKey, attachment.id.toString());
		def atFile = new File(filePath);
		if (atFile.exists()) {
			try {
				if (atFile.canRead()) {
					manager.copyAttachment(attachment, user, destination);
				}
			} catch (SecurityException se) {
				log.debug("Could not read attachment file. Not copying. (${se.message})")
			}
		} else {
			log.debug("Attachment file does not exist where it should. Not copying.")
		}
	}
}

