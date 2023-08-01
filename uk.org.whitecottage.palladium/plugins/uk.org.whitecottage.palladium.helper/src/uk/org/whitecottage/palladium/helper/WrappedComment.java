package uk.org.whitecottage.palladium.helper;

import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;

import uk.org.whitecottage.palladium.util.profile.ProfileUtil;

public class WrappedComment {
	protected Comment comment = null;
	protected String body;
	protected boolean isNewComment;
	protected boolean isDocumentation;
	protected boolean wasDocumentation;
	
	public WrappedComment() {
		body = "";
		isNewComment = true;
		isDocumentation = true;
		wasDocumentation = true;
	}

	public WrappedComment(String text) {
		this();
		body = text;
	}

	public WrappedComment(Comment comment) {
		this();
		this.comment = comment;
		body = comment.getBody();
		isNewComment = false;

		Profile profile = ProfileUtil.getProfile(comment.getNearestPackage());
		Stereotype documentationStereotype = profile.getOwnedStereotype("Documentation");
		isDocumentation = comment.isStereotypeApplied(documentationStereotype);
		wasDocumentation = isDocumentation;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		if (body != null) {
			this.body = body;
		} else {
			this.body = "";
		}
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
		setBody(comment.getBody());
	}
	
	public boolean isContentChanged() {
		if (comment.getBody() != null) {
			return !body.equals(comment.getBody());
		} else {
			return !body.contentEquals("");
		}
	}
	
	public boolean isStereotypeChanged() {
		return(isDocumentation != wasDocumentation);
	}
	
	public boolean isNewComment() {
		return isNewComment;
	}

	public void setNewComment(boolean isNewComment) {
		this.isNewComment = isNewComment;
	}

	public boolean isDocumentation() {
		return isDocumentation;
	}

	public void setDocumentation(boolean isDocumentation) {
		this.isDocumentation = isDocumentation;
	}
}
