package org.knoesis.url.extractions;

public class AnnotationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mMessage;
	
	public AnnotationException(String message) {
		super();
		this.mMessage = message;
	}
	
	public AnnotationException(Throwable cause) {
		super(cause);
	}

	public AnnotationException(String message,Throwable cause) {
		super(message, cause);
	}

	@Override
	public String toString() {
		return "EXCEPTION: "+this.mMessage+"\n"+super.toString();
	}
}
