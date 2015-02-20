package org.amaze.server.streams;

import java.util.List;
import java.util.Map;

import org.amaze.commons.api.rest.RestApiUtils;
import org.amaze.db.hibernate.objects.Stream;
import org.amaze.server.jobs.JobDeployer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StreamDeployer
{
	
	@Autowired
	StreamCommandBuilder streamCommandBuilder;

	@Autowired
	RestApiUtils apiUtils;
	
	public StreamCommandBuilder getCommandBuilder()
	{
		return streamCommandBuilder;
	}

	public void setCommandBuilder( StreamCommandBuilder commandBuilder )
	{
		this.streamCommandBuilder = commandBuilder;
	}

	public RestApiUtils getApiUtils()
	{
		return apiUtils;
	}

	public void setApiUtils( RestApiUtils apiUtils )
	{
		this.apiUtils = apiUtils;
	}

	public void createAndDeploy( Stream stream )
	{
		
		Map<String, String> commands = streamCommandBuilder.buildCommand( stream );
		apiUtils.post( "/streams/definitions", commands );
	}

	public void createAndDeploy( List<Stream> streams )
	{
		for ( Stream stream : streams )
			createAndDeploy( stream );
	}

	public void createAndDeploy( String streamName )
	{
		Map<String, String> commands = streamCommandBuilder.buildCommand( streamName );
		apiUtils.post( "/streams/definitions", commands );
	}

	public void createAndDeployAll()
	{
		List<Map<String, String>> commands = streamCommandBuilder.buildCommand();
		for ( Map<String, String> eachStreamCommand : commands )
			apiUtils.post( "/streams/definitions", eachStreamCommand );
	}
	
	public void deploy( Stream stream )
	{
		apiUtils.post( "/streams/deployments/" + stream.getStmName(), null );
	}
	
	public void deploy( String streamName )
	{
		apiUtils.post( "/streams/deployments/" + streamName, null );
	}
	
	public void unDeploy( Stream stream )
	{
		apiUtils.delete( "/streams/deployments/" + stream.getStmName() );
	}
	
	public void unDeploy( String streamName )
	{
		apiUtils.delete( "/streams/deployments/" + streamName );
	}
	
	public void unDeployAll()
	{
		apiUtils.delete( "/streams/deployments/" );
	}
	
	public String getStream( Stream stream )
	{
		return apiUtils.get( "/streams/deployments/" + stream.getStmName() );
	}
	
	public String getStream( String streamName )
	{
		return apiUtils.get( "/streams/deployments/" + streamName );
	}
	
	public String getStreamAll()
	{
		return apiUtils.get( "/streams/deployments/" );
	}

	public static void main( String[] args )
	{
//		StreamDeployer deployer = new StreamDeployer();
//		deployer.createAndDeployAll();
		
		@SuppressWarnings( "resource" )
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "stream.xml" );
//		( ( StreamDeployer ) ctx.getBean( "streamDeployer" ) ).createAndDeployAll();
		( ( JobDeployer ) ctx.getBean( "jobDeployer" )).createAndDeployAll();
		System.exit( 0 );
	}
}
