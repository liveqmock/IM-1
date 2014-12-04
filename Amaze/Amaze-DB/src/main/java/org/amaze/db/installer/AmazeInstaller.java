package org.amaze.db.installer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.amaze.commons.scripts.ScriptRunner;
import org.amaze.commons.utils.StringUtils;
import org.amaze.commons.xml.XMLTransform;
import org.amaze.db.hibernate.objects.Tables;
import org.amaze.db.hibernate.objects.Version;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.installer.exceptions.AmazeInstallerException;
import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Database;
import org.amaze.db.schema.Schema;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.DataSource;
import org.amaze.db.utils.DataSourceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AmazeInstaller
{
	public enum AmazeInstallerState
	{
		NewInstallation, UpdateInstallation, SeedUpdate, LicenceUpdate;

		public String toString()
		{
			if ( this == NewInstallation )
				return "NewInstallation";
			else if ( this == UpdateInstallation )
				return "UpdateInstallation";
			else if ( this == SeedUpdate )
				return "SeedUpdate";
			else if ( this == LicenceUpdate )
				return "LicenceUpdate";
			else
				throw new AmazeInstallerException( "Invalid Task configured in the Installer... " );
		}

		public static AmazeInstallerState getAmazeState( String eachTask )
		{
			if ( eachTask.equals( "NewInstallation" ) )
				return NewInstallation;
			else if ( eachTask.equals( "UpdateInstallation" ) )
				return UpdateInstallation;
			else if ( eachTask.equals( "SeedUpdate" ) )
				return SeedUpdate;
			else if ( eachTask.equals( "LicenceUpdate" ) )
				return LicenceUpdate;
			else
				throw new AmazeInstallerException( "Invalid Task configured in the Installer... " );
		};
	}

	private static final Logger logger = LogManager.getLogger( AmazeInstaller.class );
	
	private List<AmazeInstallerState> states = new ArrayList<AmazeInstallerState>();
	private Integer taskCompletion;

	private void install( String[] args ) throws AmazeInstallerException
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		DataSource dataSource = DataSourceUtils.getSystemDB();
		Schema schema = dataSource.getSchema();
		if ( args.length == 0 )
		{
			if ( dataSource.tableExists( "Version" ) )
			{
				List<Version> versions = HibernateSession.query( "from Version ver where ver.verCurrent = true", new Object[]
				{} );
				if ( versions.size() == 1 )
				{
					Version existingVersion = ( Version ) versions.get( 0 );
					if ( schema.majorVersion > existingVersion.getVerMajor() )
						if ( schema.minorVersion > existingVersion.getVerMinor() )
							if ( schema.servicePack > existingVersion.getVerSpk() )
								states.add( AmazeInstallerState.UpdateInstallation );
				}
			}
			else
			{
				states.add( AmazeInstallerState.NewInstallation );
			}
		}
		else
		{
			for ( String eachTask : args )
				states.add( AmazeInstallerState.getAmazeState( eachTask ) );
		}
		doInstallation( schema, dataSource );
		ctx.registerShutdownHook();
		ctx.close();
	}

	private void doInstallation( Schema schema, DataSource dataSource )
	{
		for ( AmazeInstallerState eachTask : states )
			if ( eachTask.equals( AmazeInstallerState.NewInstallation ) )
			{
				installNewTables( schema, dataSource );
				installSeedUpdateScript( dataSource );
				for ( Database eachDatabase : schema.databases )
					installNewTableDfn( dataSource, eachDatabase.tables );
				installVersion( schema, dataSource );
			}
			else if ( eachTask.equals( AmazeInstallerState.UpdateInstallation ) )
			{
				installUpdateTable( schema, dataSource );
				for ( Database eachDatabase : schema.databases )
					installUpdateTableDfn( dataSource, eachDatabase.tables );
				installUpdateVersion( schema, dataSource );
			}
			else if( eachTask.equals( AmazeInstallerState.SeedUpdate ) )
			{
				installSeedUpdateScript( dataSource );
			}
			else if( eachTask.equals( AmazeInstallerState.LicenceUpdate ) )
			{
				
			}
	}

	private void installSeedUpdateScript( DataSource dataSource )
	{
		try
		{
			ScriptRunner.executeSqlScript( dataSource.getConnection(), "/org/amaze/db/metadata/Amaze-Seed.sql", ";" );
		}
		catch ( SQLException e )
		{
			logger.error( "Could not complete the Seed installation... ", e );
		}
	}

	private void installUpdateVersion( Schema schema, DataSource dataSource )
	{
		HibernateSession.update( "update Version ver set ver.verCurrent=:verCurrent ", new String[]
		{ "verCurrent" }, new Object[]
		{ false } );
		installVersion( schema, dataSource );
	}

	private void installUpdateTableDfn( DataSource dataSource, List<Table> tables )
	{
		for ( Table eachTable : tables )
		{
			List<Tables> dbTables = HibernateSession.find( "from Tables tab where tab.tabDisplayName='" + eachTable.displayName + "'" );
			if ( dbTables.size() == 1 )
				dataSource.updateDBTableFromSchemaTable( eachTable, dbTables.get( 0 ) );
			else
				throw new AmazeInstallerException( " Could not do the table dfn updation for the table " + eachTable.tableName );
		}
	}

	private void installUpdateTable( Schema schema, DataSource dataSource )
	{
		for ( Database database : schema.databases )
			for ( Table table : database.tables )
			{
				List<Tables> tables = HibernateSession.query( "from Tables tab where tab.tabName=:TableName", "TableName", table.tableName );
				if ( tables.size() == 1 )
				{
					dataSource.updateTable( Table.loadTableFromDbTable( database, tables.get( 0 ) ), table, "" );
				}
				else
					throw new AmazeInstallerException( " No or more than one tables found for the updation of the existing table  " + table.tableName );
			}
	}

	private void installVersion( Schema schema, DataSource dataSource )
	{
		Version version = HibernateSession.createObject( Version.class );
		version.setVerName( schema.schemaName );
		version.setVerMajor( schema.majorVersion );
		version.setVerMinor( schema.minorVersion );
		version.setVerSpk( schema.servicePack );
		version.setVerCurrent( true );
		version.setVerExtensionFl( true );
		version.setVerCreatedDttm( new DateTime() );
		version.setDeleteFl( false );
		HibernateSession.save( version );
	}

	private void installNewTables( Schema schema, DataSource dataSource )
	{
		for ( Database eachDatabase : schema.databases )
		{
			for ( Table eachTable : eachDatabase.tables )
			{
				dataSource.createTable( eachTable, "", "", true );
			}
		}
	}

	private void installNewTableDfn( DataSource dataSource, List<Table> tables )
	{
		for ( Table eachTable : tables )
			dataSource.createTableEntry( eachTable, "system", "system" );
	}

	public void installNewSeeds( DataSource dataSource )
	{
		Document doc = new XMLTransform().getXMLDocumentObj( "/org/amaze/db/metadata/Amaze-Seed.xml", false );
		Element rootElement = doc.getRootElement();
		if ( !rootElement.getName().equals( "SeedData" ) )
			throw new AmazeInstallerException( "The Root Element or XMl declaration is not correct in the Seed Configuration file " );
		List<Element> childrens = rootElement.elements();
		if ( childrens.size() > 0 )
		{
			for ( int i = 0; i < childrens.size(); i++ )
				if ( childrens.get( i ) instanceof Element )
				{
					Element eachSeed = childrens.get( i );
					String parentTagName = eachSeed.getName();
					String tableName = "org.amaze.db.hibernate.objects." + parentTagName.substring( 0, parentTagName.length() - 1 );
					Class< ? > cls = null;
					Table table = Table.getTableFromTableName( dataSource, parentTagName.substring( 0, parentTagName.length() - 1 ) );
					if ( table != null )
					{
						List<Column> columns = table.columns;
						try
						{
							List<Element> childNodes = eachSeed.elements();
							for ( int j = 0; j < childNodes.size(); j++ )
							{
								Object object = HibernateSession.createObject( Class.forName( tableName ) );
								if ( childNodes.get( j ) instanceof Element )
								{
									Element eachSeedElement = childNodes.get( j );
									List<Attribute> attributes = eachSeedElement.attributes();
									for ( Attribute eachAttribute : attributes )
									{
										String name = eachAttribute.getName();
										Boolean isRefType = name.endsWith( "Ref" );
										if ( isRefType == true )
											name = name.substring( 0, name.indexOf( "Ref" ) );
										String columnName = StringUtils.camelCaseToUnderScore( name );
										String value = eachAttribute.getValue();
										AmazeType dataType = null;
										for ( Column eachCol : columns )
											if ( eachCol.columnName.equals( columnName ) )
											{
												dataType = eachCol.dataType;
												break;
											}
										if ( !isRefType )
										{
											//											Method setterMethod = cls.getDeclaredMethod( "set" + name, Class.forName( dataType.toString() ) );
											//											setterMethod.invoke( object,  );
										}
										else
										{

										}
									}

									Tables tables = ( Tables ) HibernateSession.find( "from Tables tab where tab.tableName='" + childNodes.get( i ).getName() + "'" ).get( 0 );
									//									List<Columns> columns = tables.getColumnss();
									List<Attribute> attribs = childNodes.get( 1 ).attributes();
									cls = Class.forName( tableName );
									//									AbstractHibernateObject object = (AbstractHibernateObject) HibernateSession.createObject( cls );
									for ( int k = 0; k < attribs.size(); k++ )
									{
										Attribute attrib = attribs.get( k );
										String name = attrib.getName();
										String val = attrib.getValue();
										//										Method[] methods = object.getClass().getDeclaredMethod( "set" + name, null );
									}

								}
							}
						}
						catch ( ClassNotFoundException e )
						{
							System.out.println( e );
						}
						Object sessionObject = HibernateSession.createObject( cls );
					}
				}
		}
	}

	public static void main( String[] args )
	{
		new AmazeInstaller().install( args );
		//		new AmazeInstaller().installNewSeeds( null );
	}

}
