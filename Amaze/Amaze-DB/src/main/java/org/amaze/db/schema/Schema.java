package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.List;

import org.amaze.commons.xml.XMLTransform;
import org.amaze.db.schema.exceptions.SchemaLoadException;
import org.amaze.db.schema.exceptions.SchemaParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

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
			List<Node> extendsList = doc.selectNodes( "Extends" );
			for ( int i = 0; i < extendsList.size(); i++ )
			{
				Schema schema = new Schema();
				Document doc = transform.getXMLDocumentObj( "./config/" + extendsList.get( i ).getText().replace( "[Version]", getVersionNumber() ), false );
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
		Element schemaElement = doc.getRootElement();
		this.SchemaName = schemaElement.attributeValue( "SchemaName" );
		this.ServerPackageName = schemaElement.attributeValue( "ServerPackageName" );
		this.MajorVersion = Integer.valueOf( schemaElement.attributeValue( "MajorVersion" ) );
		this.MinorVersion = Integer.valueOf( schemaElement.attributeValue( "MinorVersion" ) );
		this.ServicePack = Integer.valueOf( schemaElement.attributeValue( "ServicePack" ) );
		this.IsExtension = Boolean.valueOf( schemaElement.attributeValue( "IsExtension" ) );
		this.ParentName = schemaElement.attributeValue( "ParentName" );
		this.SystemSchema = Boolean.valueOf( schemaElement.attributeValue( "SystemSchema" ) );
		loadDatabase( schemaElement.selectNodes( "//Schema/Database" ) );
	}

	private void loadDatabase( List<Node> databases )
	{
		if ( databases.size() <= 0 )
		{
			throw new SchemaLoadException( "No Database Configured for the schema" );
		}
		for ( int i = 0; i < databases.size(); i++ )
		{
			if ( databases.get( i ) instanceof Element && ( ( Element ) databases.get( i ) ).getName().equals( "Database" ) )
			{
				Element databaseElement = ( Element ) databases.get( i );
				Database database = new Database();
				database.DatabaseName = databaseElement.attributeValue( "DatabaseName" );
				database.Schema = this;
				loadTables( database, databaseElement.selectNodes( "Tables" ) );
				this.Databases.add( database );
			}
		}
	}

	private void loadTables( Database database, List<Node> tables )
	{
		if ( tables.size() <= 0 )
		{
			throw new SchemaLoadException( "No Tables Configured for the schema" );
		}
		for ( int i = 0; i < tables.size(); i++ )
		{
			if ( tables.get( i ) instanceof Element && ( ( Element ) tables.get( i ) ).getName().equals( "Tables" ) )
			{
				List<Node> tableTags = tables.get( i ).selectNodes( "Table" );
				for( Node eachNode : tableTags )
				{
					Element tableElement = ( Element ) eachNode;
					Table table = new Table();
					table.TableName = tableElement.attributeValue( "TableName" );
					table.TablePrefix = tableElement.attributeValue( "TablePrefix" );
					table.Database = database;
					loadColumnAndIndexes( table, tableElement.content() );
					database.Tables.add( table );
				}
			}
		}
	}

	private void loadColumnAndIndexes( Table table, List<Node> columnAndIndexes )
	{
//		NodeList columns = columnAndIndexes.get( 0 ).getNextSibling().getChildNodes();
//		NodeList indexes = columnAndIndexes.get( 2 ).getNextSibling().getChildNodes();
		List<Node> columns = columnAndIndexes.get( 1 ).selectNodes( "Column" );
		List<Node> indexes = columnAndIndexes.get( 3 ).selectNodes( "Index" );
		if ( columns.size() <= 0 )
		{
			throw new SchemaLoadException( "No Columns Configured for the schema" );
		}
		for ( int i = 0; i < columns.size(); i++ )
		{
			if ( columns.get( i ) instanceof Element && ( ( Element ) columns.get( i ) ).getName().equals( "Column" ) )
			{
				Element columnElement = ( ( Element ) columns.get( i ) );
				Column column = new Column();
				column.ColumnName = columnElement.attributeValue( "ColumnName" );
				column.SequenceNumber = Integer.valueOf( columnElement.attributeValue( "SequenceNumber" ) );
				column.DataType = AmazeType.typeofString( columnElement.attributeValue( "DataType" ) );
				column.Length = Integer.valueOf( columnElement.attributeValue( "Length" ) );
				column.IsMandatory = Boolean.valueOf( columnElement.attributeValue( "IsMandatory" ) );
				column.IsPrimaryKey = Boolean.valueOf( columnElement.attributeValue( "IsPrimaryKey" ) );
				column.IsOneToOneNestedObject = Boolean.valueOf( columnElement.attributeValue( "IsOneToOneNestedObject" ) );
				column.NestedObject = columnElement.attributeValue( "NestedObject" );
				column.table = table;
				table.Columns.add( column );
			}
		}
		for ( int i = 0; i < indexes.size(); i++ )
		{
			if ( indexes.get( i ) instanceof Element && ( ( Element ) indexes.get( i ) ).getName().equals( "Index" ) )
			{
				Element indexElement = ( ( Element ) indexes.get( i ) );
				Index index = new Index();
				index.IndexName = indexElement.attributeValue( "IndexName" );
				index.IsUnique = Boolean.valueOf( indexElement.attributeValue( "IsUnique" ) );
				index.IsClustered = Boolean.valueOf( indexElement.attributeValue( "IsClustered" ) );
				index.IsBusinessConstraint = Boolean.valueOf( indexElement.attributeValue( "IsBusinessConstraint" ) );
				index.IsDisplayName = Boolean.valueOf( indexElement.attributeValue( "IsDisplayName" ) );
				index.ColumnList = indexElement.attributeValue( "ColumnList" );
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
	