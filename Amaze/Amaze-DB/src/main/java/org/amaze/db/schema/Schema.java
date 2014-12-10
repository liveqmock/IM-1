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

@SuppressWarnings( "unchecked" )
public class Schema implements Cloneable
{
	private static final Logger logger = LogManager.getLogger( Schema.class );
	public static XMLTransform transform = new XMLTransform();
	
	public String schemaName;
	public String serverPackageName;
	public int majorVersion;
	public int minorVersion;
	public int servicePack;
	public boolean isExtension;
	public String parentName;
	public Boolean systemSchema;
	
	private Document document;

	public List<Database> databases = new ArrayList<Database>();

	public Object clone() throws CloneNotSupportedException
	{
		Schema schema = new Schema();
		schema.schemaName = schemaName;
		schema.serverPackageName = serverPackageName;
		schema.majorVersion = majorVersion;
		schema.minorVersion = minorVersion;
		schema.servicePack = servicePack;
		schema.isExtension = isExtension;
		schema.parentName = parentName;
		schema.systemSchema = systemSchema;
		for ( Database database : databases )
		{
			schema.databases.add( ( Database ) database.clone() );
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
		for ( Database database : databases )
		{
			if ( database.databaseName.equals( databaseName ) )
			{
				return database;
			}
		}
		return null;
	}

	public void mergeSchema( Schema mergeSchema )
	{
		for ( Database mergeDatabase : mergeSchema.databases )
		{
			Database localDatabase = findDatabase( mergeDatabase.databaseName );
			if ( localDatabase == null )
			{
				databases.add( mergeDatabase );
				continue;
			}
			for ( Table mergeTable : mergeDatabase.tables )
			{
				Table table = localDatabase.findTable( mergeTable.tableName );
				if ( table == null )
				{
					localDatabase.tables.add( mergeTable );
					mergeTable.database = localDatabase;
					continue;
				}
				for ( Index mergeIndex : mergeTable.indexes )
				{
					Index index = table.findIndex( mergeIndex.indexName );
					if ( index == null )
					{
						table.indexes.add( mergeIndex );
						mergeIndex.table = table;
						continue;
					}
				}
			}
		}
	}

	public void changeDatabaseName( String databaseName )
	{
		if ( databases.size() > 1 )
		{
			throw new UnsupportedOperationException( "Cannot change database name on multiple database schema" );
		}
		databases.get( 0 ).databaseName = databaseName;
	}

	public void loadSchema( String schemaFileName )
	{
		try
		{
			document = transform.getXMLDocumentObj( schemaFileName, false );
			loadSchema( document );
			List<Node> extendsList = document.selectNodes( "Extends" );
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
		return majorVersion + "-" + minorVersion + "-" + servicePack;
	}

	public void loadSchema( Document doc )
	{
		this.document = doc;
		Element schemaElement = doc.getRootElement();
		this.schemaName = schemaElement.attributeValue( "SchemaName" );
		this.serverPackageName = schemaElement.attributeValue( "ServerPackageName" );
		this.majorVersion = Integer.valueOf( schemaElement.attributeValue( "MajorVersion" ) );
		this.minorVersion = Integer.valueOf( schemaElement.attributeValue( "MinorVersion" ) );
		this.servicePack = Integer.valueOf( schemaElement.attributeValue( "ServicePack" ) );
		this.isExtension = Boolean.valueOf( schemaElement.attributeValue( "IsExtension" ) );
		this.parentName = schemaElement.attributeValue( "ParentName" );
		this.systemSchema = Boolean.valueOf( schemaElement.attributeValue( "SystemSchema" ) );
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
				database.databaseName = databaseElement.attributeValue( "DatabaseName" );
				database.schema = this;
				loadTables( database, databaseElement.selectNodes( "Tables" ) );
				this.databases.add( database );
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
					table.tableName = tableElement.attributeValue( "TableName" );
					table.tablePrefix = tableElement.attributeValue( "TablePrefix" );
					table.displayName = tableElement.attributeValue( "DisplayName" );
					table.database = database;
					loadColumnAndIndexes( table, tableElement.content() );
					database.tables.add( table );
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
				column.columnName = columnElement.attributeValue( "ColumnName" );
				column.dataType = AmazeType.typeofString( columnElement.attributeValue( "DataType" ) );
				column.length = Integer.valueOf( columnElement.attributeValue( "Length" ) );
				column.isMandatory = Boolean.valueOf( columnElement.attributeValue( "IsMandatory" ) );
				column.isPrimaryKey = Boolean.valueOf( columnElement.attributeValue( "IsPrimaryKey" ) );
				column.nestedObject = columnElement.attributeValue( "NestedObject" );
				column.table = table;
				table.columns.add( column );
			}
		}
		for ( int i = 0; i < indexes.size(); i++ )
		{
			if ( indexes.get( i ) instanceof Element && ( ( Element ) indexes.get( i ) ).getName().equals( "Index" ) )
			{
				Element indexElement = ( ( Element ) indexes.get( i ) );
				Index index = new Index();
				index.indexName = indexElement.attributeValue( "IndexName" );
				index.isUnique = Boolean.valueOf( indexElement.attributeValue( "IsUnique" ) );
				index.isClustered = Boolean.valueOf( indexElement.attributeValue( "IsClustered" ) );
				index.isBusinessConstraint = Boolean.valueOf( indexElement.attributeValue( "IsBusinessConstraint" ) );
				index.columnList = indexElement.attributeValue( "ColumnList" );
				index.condition = indexElement.attributeValue( "Condition" );
				index.table = table;
				table.indexes.add( index );
			}
		}

	}

	public static void main( String[] args )
	{
		new Schema().loadSchema( "./config/Amaze_Schema.xml" );
	}

}
	