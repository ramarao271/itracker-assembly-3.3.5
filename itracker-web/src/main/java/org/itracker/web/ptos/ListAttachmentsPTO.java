/**
 *
 */
package org.itracker.web.ptos;

import org.itracker.model.IssueAttachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class represents ListAttachmentsPTO.
 *
 * @author Anton Kozak
 */
public class ListAttachmentsPTO {
    boolean hasAttachments = false;
    long sizeOfAllAttachments = 0;
    List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();

    /**
     * @return the hasAttachments
     */
    public boolean isHasAttachments() {
        return hasAttachments;
    }

    /**
     * @param hasAttachments the hasAttachments to set
     */
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    /**
     * @return the sizeOfAllAttachments
     */
    public long getSizeOfAllAttachments() {
        return sizeOfAllAttachments;
    }

    /**
     * @param sizeOfAllAttachments the sizeOfAllAttachments to set
     */
    public void setSizeOfAllAttachments(long sizeOfAllAttachments) {
        this.sizeOfAllAttachments = sizeOfAllAttachments;
    }

    /**
     * @return the attachments
     */
    public List<IssueAttachment> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<IssueAttachment> attachments) {
        this.attachments = attachments;
        if (attachments.size() > 0) {
            hasAttachments = true;
            Collections.sort(attachments, IssueAttachment.ID_COMPARATOR);
            for (IssueAttachment issueAttachment : attachments) {
                sizeOfAllAttachments += issueAttachment.getSize();
            }
        }
    }


}
