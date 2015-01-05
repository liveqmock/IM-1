package org.amaze.server.streams;

import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.Taps;
import org.amaze.server.streams.rest.StreamApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TapsDeployer
{

	@Autowired
	TapCommandBuilder tapCommandBuilder;

	@Autowired
	StreamApiUtils apiUtils;
	
	public TapCommandBuilder getCommandBuilder()
	{
		return tapCommandBuilder;
	}

	public void setCommandBuilder( TapCommandBuilder commandBuilder )
	{
		this.tapCommandBuilder = commandBuilder;
	}

	public StreamApiUtils getApiUtils()
	{
		return apiUtils;
	}

	public void setApiUtils( StreamApiUtils apiUtils )
	{
		this.apiUtils = apiUtils;
	}

	public void createAndDeploy( Taps tap )
	{
		Map<String, String> commands = tapCommandBuilder.buildCommand( tap );
		apiUtils.post( "/streams/definitions", commands );
	}

	public void createAndDeploy( List<Taps> taps )
	{
		for ( Taps tap : taps )
			createAndDeploy( tap );
	}

	public void createAndDeploy( String tapName )
	{
		Map<String, String> commands = tapCommandBuilder.buildCommand( tapName );
		apiUtils.post( "/streams/definitions", commands );
	}

	public void createAndDeployAll()
	{
		List<Map<String, String>> commands = tapCommandBuilder.buildCommand();
		for ( Map<String, String> eachStreamCommand : commands )
			apiUtils.post( "/streams/definitions", eachStreamCommand );
	}
	
	public void deploy( Taps tap )
	{
		apiUtils.post( "/streams/deployments/" + tap.getTapName(), null );
	}
	
	public void deploy( String tapName )
	{
		apiUtils.post( "/streams/deployments/" + tapName, null );
	}
	
	public void unDeploy( Taps tap )
	{
		apiUtils.delete( "/streams/deployments/" + tap.getTapName() );
	}
	
	public void unDeploy( String tapName )
	{
		apiUtils.delete( "/streams/deployments/" + tapName );
	}
	
	public void unDeployAll()
	{
		apiUtils.delete( "/streams/deployments/" );
	}
	
	public String getStream( Taps tap )
	{
		return apiUtils.get( "/streams/deployments/" + tap.getTapName() );
	}
	
	public String getStream( String tapName )
	{
		return apiUtils.get( "/streams/deployments/" + tapName );
	}
	
	public String getStreamAll()
	{
		return apiUtils.get( "/streams/deployments/" );
	}

	public static void main( String[] args )
	{
//		StreamDeployer deployer = new StreamDeployer();
//		deployer.createAndDeployAll();
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "stream.xml" );
		( ( StreamDeployer ) ctx.getBean( "tapsDeployer" ) ).createAndDeployAll();
	}
}
