package org.amaze.commons.scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.amaze.commons.exceptions.AmazeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptRunner
{

	private static Logger logger = LogManager.getLogger( ScriptRunner.class );

	public static void executeSqlScript( Connection conn, String script, String delimiter ) throws SQLException
	{
		Scanner scanner = null;
		try
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( ScriptRunner.class.getResourceAsStream( script ) ) );
			scanner = new Scanner( reader );
			scanner.useDelimiter( delimiter );
			Statement statement = null;
			while ( scanner.hasNext() )
			{
				String rawStatement = scanner.next();
				statement = conn.createStatement();
				statement.execute( rawStatement );
				statement.close();
			}
			logger.info( " DB Script " + script + " succesfully executed. " );
		}
		catch ( SQLException e )
		{
			logger.fatal( "The Schema for the Server could not be prepared... /n There is a serious SQLException occured while Preparing DB.. ", e );
			throw new AmazeException( e );
		}
		finally
		{
			if ( scanner != null )
				scanner.close();
		}
	}

}