package org.amaze.server.streams.nodes;

import java.util.List;

import org.amaze.db.hibernate.objects.Nodes;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.streams.exceptions.StreamStartException;

public class NodesStarter
{
	private static String OS = System.getProperty( "os.name" ).toLowerCase();

	public void startNodes()
	{
		List<Nodes> nodes = HibernateSession.getAllObjects( Nodes.class );
		String xdHome = System.getProperty( "XD_HOME" );
		if( xdHome == null || xdHome.equals( "" ) )
			throw new StreamStartException( "XD Home is not set" );
		for( Nodes eachNode : nodes )
			startNodeInDist( eachNode );
	}

	private void startNodeInDist( Nodes eachNode )
	{
		
	}

	public static boolean isWindows()
	{

		return ( OS.indexOf( "win" ) >= 0 );

	}

	public static boolean isMac()
	{

		return ( OS.indexOf( "mac" ) >= 0 );

	}

	public static boolean isUnix()
	{

		return ( OS.indexOf( "nix" ) >= 0 || OS.indexOf( "nux" ) >= 0 || OS.indexOf( "aix" ) > 0 );

	}

	public static boolean isSolaris()
	{

		return ( OS.indexOf( "sunos" ) >= 0 );

	}

}
