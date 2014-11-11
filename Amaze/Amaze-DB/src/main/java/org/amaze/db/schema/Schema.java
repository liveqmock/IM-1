package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.List;

import org.amaze.commons.xml.XMLTransform;
import org.amaze.db.schema.exceptions.SchemaLoadException;
import org.amaze.db.schema.exceptions.SchemaParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Schema implements Cloneable
{

	private static final Logger logger = LogManager.getLogger( Schema.class );

	public static XMLTransform transform = new XMLTransform();

	private Document doc;

	public String SchemaName;
	public String ServerPackageName;
	public int MajorVersion;
	public int MinorVersion;
	public int ServicePack;
	public boolean IsExtension;
	public String ParentName;
	public Boolean SystemSchema;

	public List<Database> Databases = new ArrayList<Database>();

	public Object clone() throws CloneNotSupportedException
	{
		Schema schema = new Schema();
		schema.SchemaName = SchemaName;
		schema.ServerPackageName = ServerPackageName;
		schema.MajorVersion = MajorVersion;
		schema.MinorVersion = MinorVersion;
		schema.ServicePack = ServicePack;
		schema.IsExtension = IsExtension;
		schema.ParentName = ParentName;
		schema.SystemSchema = SystemSchema;
		for ( Database database : Databases )
		{
			schema.Databases.add( ( Database ) database.clone() );
		}
		return schema;
	}

	public Table findTable( String databasename, String tablename )
	{
		Database database = findDatabase( databasename );
		if ( database == null )
			return null;
		return database.findTable( tablename );
	}

	public Database findDatabase( String databaseName )
	{
		for ( Database database : Databases )
		{
			if ( database.DatabaseName.equals( databaseName ) )
			{
				return database;
			}
		}
		return null;
	}

	public void mergeSchema( Schema mergeSchema )
	{
		for ( Database mergeDatabase : mergeSchema.Databases )
		{
			Database localDatabase = findDatabase( mergeDatabase.DatabaseName );
			if ( localDatabase == null )
			{
				Databases.add( mergeDatabase );
				continue;
			}
			for ( Table mergeTable : mergeDatabase.Tables )
			{
				Table table = localDatabase.findTable( mergeTable.TableName );
				if ( table == null )
				{
					localDatabase.Tables.add( mergeTable );
					mergeTable.Database = localDatabase;
					continue;
				}
				for ( Index mergeIndex : mergeTable.Indexes )
				{
					Index index = table.findIndex( mergeIndex.IndexName );
					if ( index == null )
					{
						table.Indexes.add( mergeIndex );
						mergeIndex.table = table;
						continue;
					}
				}
			}
		}
	}

	public void changeDatabaseName( String databaseName )
	{
		if ( Databases.size() > 1 )
		{
			throw new UnsupportedOperationException( "Cannot change database name on multiple database schema" );
		}
		Databases.get( 0 ).DatabaseName = databaseName;
	}

	public void loadSchema( String schemaFileName )
	{
		try
		{
			doc = transform.getXMLDocumentObj( schemaFileName, false );
			loadSchema( doc );
			NodeList extendsList = doc.getElementsByTagName( "Extends" );
			for ( int i = 0; i < extendsList.getLength(); i++ )
			{
				Schema schema = new Schema();
				Document doc = transform.getXMLDocumentObj( "./config/" + extendsList.item( i ).getNodeValue().replace( "[Version]", getVersionNumber() ), false );
				schema.loadSchema( doc );
				mergeSchema( schema );
			}
		}
		catch ( Exception e )
		{
			logger.fatal( "Could not load the Schema " + schemaFileName, e );
			throw new SchemaParseException( e );
		}
	}

	private String getVersionNumber()
	{
		return MajorVersion + "-" + MinorVersion + "-" + ServicePack;
	}

	public void loadSchema( Document doc )
	{
		this.doc = doc;
		Element schemaElement = doc.getDocumentElement();
		this.SchemaName = schemaElement.getAttribute( "SchemaName" );
		this.ServerPackageName = schemaElement.getAttribute( "ServerPackageName" );
		this.MajorVersion = Integer.valueOf( schemaElement.getAttribute( "MajorVersion" ) );
		this.MinorVersion = Integer.valueOf( schemaElement.getAttribute( "MinorVersion" ) );
		this.ServicePack = Integer.valueOf( schemaElement.getAttribute( "ServicePack" ) );
		this.IsExtension = Boolean.valueOf( schemaElement.getAttribute( "IsExtension" ) );
		this.ParentName = schemaElement.getAttribute( "ParentName" );
		this.SystemSchema = Boolean.valueOf( schemaElement.getAttribute( "SystemSchema" ) );
		loadDatabase( schemaElement.getChildNodes() );
	}

	private void loadDatabase( NodeList databases )
	{
		if ( databases.getLength() <= 0 )
		{
			throw new SchemaLoadException( "No Database Configured for the schema" );
		}
		for ( int i = 0; i < databases.getLength(); i++ )
		{
			if ( databases.item( i ) instanceof Element && ( ( Element ) databases.item( i ) ).getTagName().equals( "Database" ) )
			{
				Element databaseElement = ( Element ) databases.item( i );
				Database database = new Database();
				database.DatabaseName = databaseElement.getAttribute( "DatabaseName" );
				database.Schema = this;
				loadTables( database, databaseElement.getChildNodes() );
				this.Databases.add( database );
			}
		}
	}

	private void loadTables( Database database, NodeList tables )
	{
		tables = tables.item( 0 ).getNextSibling().getChildNodes();
		if ( tables.getLength() <= 0 )
		{
			throw new SchemaLoadException( "No Tables Configured for the schema" );
		}
		for ( int i = 0; i < tables.getLength(); i++ )
		{
			if ( tables.item( i ) instanceof Element && ( ( Element ) tables.item( i ) ).getTagName().equals( "Table" ) )
			{
				Element tableElement = ( Element ) tables.item( i );
				Table table = new Table();
				table.TableName = tableElement.getAttribute( "TableName" );
				table.TablePrefix = tableElement.getAttribute( "TablePrefix" );
				table.Database = database;
				loadColumnAndIndexes( table, tableElement.getChildNodes() );
				database.Tables.add( table );
			}
		}
	}

	private void loadColumnAndIndexes( Table table, NodeList columnAndIndexes )
	{
		NodeList columns = columnAndIndexes.item( 0 ).getNextSibling().getChildNodes();
		NodeList indexes = columnAndIndexes.item( 2 ).getNextSibling().getChildNodes();
		if ( columns.getLength() <= 0 )
		{
			throw new SchemaLoadException( "No Columns Configured for the schema" );
		}
		for ( int i = 0; i < columns.getLength(); i++ )
		{
			if ( columns.item( i ) instanceof Element && ( ( Element ) columns.item( i ) ).getTagName().equals( "Column" ) )
			{
				Element columnElement = ( ( Element ) columns.item( i ) );
				Column column = new Column();
				column.ColumnName = columnElement.getAttribute( "ColumnName" );
				column.SequenceNumber = Integer.valueOf( columnElement.getAttribute( "SequenceNumber" ) );
				column.DataType = AmazeType.typeofString( columnElement.getAttribute( "DataType" ) );
				column.Length = Integer.valueOf( columnElement.getAttribute( "Length" ) );
				column.IsMandatory = Boolean.valueOf( columnElement.getAttribute( "IsMandatory" ) );
				column.IsPrimaryKey = Boolean.valueOf( columnElement.getAttribute( "IsPrimaryKey" ) );
				column.IsOneToOneNestedObject = Boolean.valueOf( columnElement.getAttribute( "IsOneToOneNestedObject" ) );
				column.NestedObject = columnElement.getAttribute( "NestedObject" );
				column.table = table;
				table.Columns.add( column );
			}
		}
		for ( int i = 0; i < indexes.getLength(); i++ )
		{
			if ( indexes.item( i ) instanceof Element && ( ( Element ) indexes.item( i ) ).getTagName().equals( "Index" ) )
			{
				Element indexElement = ( ( Element ) indexes.item( i ) );
				Index index = new Index();
				index.IndexName = indexElement.getAttribute( "IndexName" );
				index.IsUnique = Boolean.valueOf( indexElement.getAttribute( "IsUnique" ) );
				index.IsClustered = Boolean.valueOf( indexElement.getAttribute( "IsClustered" ) );
				index.IsBusinessConstraint = Boolean.valueOf( indexElement.getAttribute( "IsBusinessConstraint" ) );
				index.IsDisplayName = Boolean.valueOf( indexElement.getAttribute( "IsDisplayName" ) );
				index.ColumnList = indexElement.getAttribute( "ColumnList" );
				index.table = table;
				table.Indexes.add( index );
			}
		}

	}

	public static void main( String[] args )
	{
		new Schema().loadSchema( "./config/Amaze_Schema.xml" );
	}

}
