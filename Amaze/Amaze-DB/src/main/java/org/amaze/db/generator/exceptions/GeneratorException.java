package org.amaze.db.generator.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class GeneratorException extends AmazeException{

	private static final long serialVersionUID = 1L;
	
	public GeneratorException() {
		super();
	}
	
	public GeneratorException(String s){
		super(s);
	}
	
	public GeneratorException(Exception e){
		super(e);
	}

	public GeneratorException(Throwable t) {
		super(t);
	}

	public GeneratorException( String str, Object... args )
    {
        super( AmazeString.create( str, args ) );
    }

    public GeneratorException( String str, Throwable t, Object... args )
    {
        super( AmazeString.create( str, args ), t );
    }

}
