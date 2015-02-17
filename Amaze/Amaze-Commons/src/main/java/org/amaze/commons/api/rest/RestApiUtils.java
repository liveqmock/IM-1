package org.amaze.commons.api.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amaze.commons.exceptions.AmazeException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class RestApiUtils
{

	private static final Logger logger  = LogManager.getLogger( RestApiUtils.class );

	@Autowired
	String restBaseUrl;
	
	public RestApiUtils()
	{
		
	}
	
	public RestApiUtils( String restBaseUrl )
	{
		this.restBaseUrl = restBaseUrl;
	}

	public String getRestBaseUrl()
	{
		return restBaseUrl;
	}

	public void setRestBaseUrl( String restBaseUrl )
	{
		this.restBaseUrl = restBaseUrl;
	}

	public String get( String resource )
	{
		logger.info( "API call to " + resource + " made... " );
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeException( "The base url for the Rest service is not set " );
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet( restBaseUrl + resource );
			HttpResponse response = client.execute( request );
			if ( !( response.getStatusLine().getStatusCode() == 200 ) )
			{
				logger.error( "API call to " + resource + " failed.. " +  " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() + "..." + " Check container node logs for more details... "  );
				throw new AmazeException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
			}
			BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
			StringBuffer result = new StringBuffer();
			String line = "";
			while ( ( line = rd.readLine() ) != null )
			{
				result.append( line );
			}
			return result.toString();
		}
		catch ( IOException e )
		{
			logger.error( "Extracting data from the API call " + resource + " failed... ", e );
			throw new AmazeException( e );
		}
	}

	public String post( String resource, Map<String, String> values )
	{
		logger.info( "API call to " + resource + " command " + values + " made... " );
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeException( "The base url for the admin Rest service is not set " );
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost( restBaseUrl + resource );
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			if ( values != null )
			{
				for ( Map.Entry<String, String> eachEntry : values.entrySet() )
					urlParameters.add( new BasicNameValuePair( eachEntry.getKey(), eachEntry.getValue() ) );
				post.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			}
			HttpResponse response = client.execute( post );
			if ( !( response.getStatusLine().getStatusCode() == 200 ||  response.getStatusLine().getStatusCode() == 201 ) )
			{
				logger.error( "API call to " + resource + " command " + values + " failed.. " +  " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() + "..." + " Check container node logs for more details... "  );
				throw new AmazeException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
			}
			BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
			StringBuffer result = new StringBuffer();
			String line = "";
			while ( ( line = rd.readLine() ) != null )
			{
				result.append( line );
			}
			return result.toString();
		}
		catch ( IOException e )
		{
			logger.error( "Extracting data from the API call " + resource + " " + " command " + values + " failed... ", e );
			throw new AmazeException( e );
		}
	}

	public String delete( String resource )
	{
		logger.info( "API call to " + resource + " made... " );
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeException( "The base url for the admin Rest service is not set " );
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpDelete delete = new HttpDelete( restBaseUrl + resource );
			HttpResponse response = client.execute( delete );
			if ( !( response.getStatusLine().getStatusCode() == 200 ) )
			{
				logger.error( "API call to " + resource + " failed.. " +  " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() + "..." + " Check container node logs for more details... "  );
				throw new AmazeException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
			}
			BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
			StringBuffer result = new StringBuffer();
			String line = "";
			while ( ( line = rd.readLine() ) != null )
			{
				result.append( line );
			}
			return result.toString();
		}
		catch ( IOException e )
		{
			logger.error( "Extracting data from the API call " + resource + " " + " failed... ", e );
			throw new AmazeException( e );
		}
	}

}
