package uk.org.whitecottage.palladium.helper;

import org.eclipse.uml2.uml.Comment;

public class WrappedComment {
	Comment comment = null;
	String body;
	boolean isNewComment;
	
	public WrappedComment() {
		body = "";
		isNewComment = true;
	}

	public WrappedComment(String text) {
		body = text;
		isNewComment = true;
	}

	public WrappedComment(Comment comment) {
		this.comment = comment;
		body = comment.getBody();
		isNewComment = false;
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
	
	public boolean isChanged() {
		if (comment.getBody() != null) {
			return !body.equals(comment.getBody());
		} else {
			return !body.contentEquals("");
		}
	}
	
	public boolean isNewComment() {
		return isNewComment;
	}

	public void setNewComment(boolean isNewComment) {
		this.isNewComment = isNewComment;
	}
}
