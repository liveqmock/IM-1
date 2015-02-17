package org.amaze.commons.task;

import java.util.ArrayList;
import java.util.List;

public class TaskResult
{

	private String result;

	private List<String> errorList = new ArrayList<String>();

	private Boolean hasError;

	private Boolean hasWarning;

	private String warning;

	public String getResult()
	{
		return result;
	}

	public void setResult( String result )
	{
		this.result = result;
	}

	public List<String> getErrorList()
	{
		return errorList;
	}

	public void setErrorList( List<String> errorList )
	{
		this.errorList = errorList;
	}

	public Boolean getHasError()
	{
		return hasError;
	}

	public void setHasError( Boolean hasError )
	{
		this.hasError = hasError;
	}

	public Boolean getHasWarning()
	{
		return hasWarning;
	}

	public void setHasWarning( Boolean hasWarning )
	{
		this.hasWarning = hasWarning;
	}

	public String getWarning()
	{
		return warning;
	}

	public void setWarning( String warning )
	{
		this.warning = warning;
	}

}
