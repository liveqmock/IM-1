package org.amaze.server.streams.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amaze.server.streams.exceptions.AmazeStreamException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;

public class StreamApiUtils
{
	
	@Autowired
	String restBaseUrl;
	
	public StreamApiUtils()
	{
		
	}
	
	public StreamApiUtils( String restBaseUrl )
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
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeStreamException( "The base url for the admin Rest service is not set " );
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet( restBaseUrl + resource );
			HttpResponse response = client.execute( request );
			if ( !( response.getStatusLine().getStatusCode() == 200 ) )
				throw new AmazeStreamException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
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
			throw new AmazeStreamException( e );
		}
	}

	public String post( String resource, Map<String, String> values )
	{
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeStreamException( "The base url for the admin Rest service is not set " );
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
				throw new AmazeStreamException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
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
			throw new AmazeStreamException( e );
		}
	}

	public String delete( String resource )
	{
		if ( restBaseUrl == null || restBaseUrl.equals( "" ) )
			throw new AmazeStreamException( "The base url for the admin Rest service is not set " );
		try
		{
			HttpClient client = HttpClientBuilder.create().build();
			HttpDelete delete = new HttpDelete( restBaseUrl + resource );
			HttpResponse response = client.execute( delete );
			if ( !( response.getStatusLine().getStatusCode() == 200 ) )
				throw new AmazeStreamException( " Http Error : " + response.getStatusLine().getReasonPhrase() + " Http Error Code : " + response.getStatusLine().getStatusCode() );
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
			throw new AmazeStreamException( e );
		}
	}

}
