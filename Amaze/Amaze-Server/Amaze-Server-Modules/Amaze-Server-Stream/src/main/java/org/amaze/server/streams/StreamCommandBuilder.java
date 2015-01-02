package org.amaze.server.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.Stream;
import org.amaze.db.hibernate.objects.StreamModules;
import org.amaze.db.hibernate.utils.HibernateSession;

public class StreamCommandBuilder
{
	
	public Map<String, String> buildCommand( Stream stream )
	{
		Map<String, String> commands = new HashMap<String, String>();
		StringBuffer command = new StringBuffer();
		List<StreamModules> modules = stream.getStreamModuless();
		for ( StreamModules eachModule : modules )
		{
			command.append( getCommandWithProperties( eachModule ) ).append( " | " );
		}
		commands.put( "definition", "\"" + command.toString().substring( 0, command.toString().length() - 2 ) + "\"" );
		commands.put( "name", stream.getStmName() );
		commands.put( "deploy", stream.getStmDeployOnLoad().toString() );
		return commands;
	}

	private String getCommandWithProperties( StreamModules eachModule )
	{
		StringBuffer moduleProperties = new StringBuffer();
		String moduleName = eachModule.getSmdValue();
		if ( eachModule.getPvgIdPropertyValueGroup() != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", eachModule.getPvgIdPropertyValueGroup().getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				moduleProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() );
		}
		return moduleName + " " + moduleProperties;
	}

	public Map<String, String> buildCommand( String streamName )
	{
		return buildCommand( ( Stream ) HibernateSession.queryExpectExactlyOneRow( "from Stream stm where stm.stmName = :stmName", "stmName", streamName ) );
	}

	public List<Map<String, String>> buildCommand( List<Stream> streams )
	{
		List<Map<String, String>> commands = new ArrayList<Map<String, String>>();
		for ( Stream eachStream : streams )
			commands.add( buildCommand( ( Stream ) eachStream ) );
		return commands;
	}
	
	public List<Map<String, String>> buildCommand()
	{
		List<Stream> streams = HibernateSession.query( "from Stream stm where stm.stmCreateOnLoad = :stmCreateOnLoad", "stmCreateOnLoad", true ); 
		return buildCommand( streams );
	}

}
