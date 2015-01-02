package org.amaze.server.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.TapModules;
import org.amaze.db.hibernate.objects.Taps;
import org.amaze.db.hibernate.utils.HibernateSession;

public class TapCommandBuilder
{

	public Map<String, String> buildCommand( Taps tap )
	{
		Map<String, String> commands = new HashMap<String, String>();
		StringBuffer command = new StringBuffer();
		command.append( "tap:stream:" );
		command.append( tap.getSmdIdStreamModules().getStmIdStream().getStmName() );
		command.append( "." );
		command.append( tap.getSmdIdStreamModules().getSmdValue() );
		command.append( " > " );
		command.append( getCommandWithProperties( tap.getTapSinkTapModules() ) );
		commands.put( "definition", "\"" + command.toString() + "\"" );
		commands.put( "name", tap.getTapName() );
		commands.put( "deploy", tap.getTapDeployOnLoad().toString() );
		return commands;
	}
	
	private String getCommandWithProperties( TapModules eachModule )
	{
		StringBuffer moduleProperties = new StringBuffer();
		String moduleName = eachModule.getTmdValue();
		if ( eachModule.getPvgIdPropertyValueGroup() != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", eachModule.getPvgIdPropertyValueGroup().getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				moduleProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() );
		}
		return moduleName + " " + moduleProperties;
	}
	
	public Map<String, String> buildCommand( String tapName )
	{
		return buildCommand( ( Taps ) HibernateSession.queryExpectExactlyOneRow( "from Taps tap where tap.tapName = :tapName", "tapName", tapName ) );
	}

	public List<Map<String, String>> buildCommand( List<Taps> taps )
	{
		List<Map<String, String>> commands = new ArrayList<Map<String, String>>();
		for ( Taps eachTap : taps )
			commands.add( buildCommand( ( Taps ) eachTap ) );
		return commands;
	}
	
	public List<Map<String, String>> buildCommand()
	{
		List<Taps> taps = HibernateSession.query( "from Taps tap where tap.tapCreateOnLoad = :tapCreateOnLoad", "tapCreateOnLoad", true ); 
		return buildCommand( taps );
	}


}
