package org.amaze.db.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.amaze.commons.utils.StringUtils;
import org.amaze.commons.xml.XMLTransform;
import org.amaze.commons.xml.exceptions.XMLException;
import org.amaze.db.generator.exceptions.GeneratorException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.data.cassandra.mapping.Column;

public class GenerateUsageObjects
{
	XMLTransform transform = new XMLTransform();
	String packageName = null;
	String amazeVersion = null;
	Document doc = null;
	Map<String, String> tableNameTablePrefixMap = new HashMap<String, String>();
	String location;
	
	public void generateObjects( String schemaFile, String location )
	{
		this.location = location;
		try
		{
			doc = transform.getXMLDocumentObj( schemaFile, false );
			List<Node> tables = doc.selectNodes( "//Schema/Database/Tables/Table" ); 
			for( Node eachNode : tables )
			{
				tableNameTablePrefixMap.put( StringUtils.underScoreToCamelCase( ( (Element)eachNode).attributeValue( "TableName" ) ), (( Element)eachNode).attributeValue( "TablePrefix" ) );
			}
			validate( doc );
			clearOldSrcFiles();
			parseSchemaFile( doc );
			List<Node> extendsTag = doc.selectNodes( "//Schema/Extends" );
			for( Node eachTag : extendsTag )
			{
				Element element = ( Element ) eachTag;
				parseSchemaFile( transform.getXMLDocumentObj( element.getText(), false ) );
			}

		}
		catch ( XMLException | IOException e )
		{
			throw new GeneratorException( e );
		}
	}
	
	private void parseSchemaFile( Document doc2 )
	{
		List<Node> database = doc.selectNodes( "//Schema/Database" );
		if ( database.size() == 1 )
		{
			Node eachDatabase = database.get( 0 );
			if ( ( ( Element ) eachDatabase ).attributeValue( "DatabaseName" ).equals( "amazeUsage" ) )
			{
				parseDatabase( database.get( 0 ) );
			}
			else
				throw new GeneratorException( "No Amaze database configured" );
		}
		else
			throw new GeneratorException( "Multiple databases configured" );
	}

	private void parseDatabase( Node eachDatabase )
	{
		List<Node> tablesTags = eachDatabase.selectNodes( "//Schema/Database/Tables" );  
		if ( tablesTags.size() == 1 )
		{
			Node tablesTag = tablesTags.get( 0 );
			List<Node> tables = tablesTag.selectNodes( "Table" );
			for( Node eachTable : tables )
				parseTables( eachTable );
		}
		else
			throw new GeneratorException( "Multiple Tables tag configured" );
	}

	private void parseTables( Node tableTag )
	{
		Element table = ( Element ) tableTag;
		String tableName = table.attributeValue( "TableName" );
		String tablePrefix = table.attributeValue( "TablePrefix" );
		List<Node> columns = table.selectNodes( "Columns" );
		if ( columns.size() != 1 )
			throw new GeneratorException( "Multiple no of Columns tag configured" );
		columns = columns.get( 0 ).selectNodes( "Column" );
		List<Node> indexes = table.selectNodes( "Indexes" );
		if ( indexes.size() != 1 )
			throw new GeneratorException( "Multiple no of Indexes tag configured" );
		indexes = indexes.get( 0 ).selectNodes( "Index" );
		try
		{
			createUsageObject( tableName, tablePrefix, columns, indexes );
		}
		catch ( IOException e )
		{
			throw new GeneratorException( e );
		}
	}

	private void createUsageObject( String tableName, String tablePrefix, List<Node> columns, List<Node> indexes ) throws IOException
	{
		String classFileName = StringUtils.underScoreToCamelCase( tableName );
 		String srcFolder = location + File.separator + packageName.replace( ".", File.separator ) + File.separator;
		String srcFile = srcFolder + File.separator + classFileName + ".java";
		File srcFileObj = new File( srcFolder );
		if ( !srcFileObj.exists() )
			srcFileObj.mkdirs();
		srcFileObj = new File( srcFile );
		srcFileObj.createNewFile();
		Writer outWriter = new FileWriter( srcFileObj );
		BufferedWriter out = new BufferedWriter( outWriter );
		out.write( "package " + packageName + ";" );
		out.newLine();
		addImports( out );
		out.newLine();
		addEntityAnnotation( out, tableName, columns );
		out.newLine();
		createIndexAnnotations( out, indexes, tablePrefix, tableName );
		out.newLine();
		out.write( "public class " + classFileName + " extends AbstractUsageObject implements Serializable " );
		out.newLine();
		out.write( "{" );
		out.newLine();
		out.newLine();
		out.write( "    private static final long serialVersionUID = 1L;" );
		out.newLine();
		createFieldMappings( out, columns, tablePrefix );
		out.newLine();
		out.write( "}" );
		out.flush();
		out.close();
	}
	
	private void createFieldMappings( BufferedWriter out, List<Node> columns, String tablePrefix ) throws IOException
	{
		for ( int i = 0; i < columns.size(); i++ )
		{
			Node eachNode = columns.get( i );
			if ( eachNode instanceof Element )
			{
				String columnName = ( ( Element ) eachNode ).attributeValue( "ColumnName" );
				String dataType = ( ( Element ) eachNode ).attributeValue( "DataType" );
				String length = ( ( Element ) eachNode ).attributeValue( "Length" );
				String isMandatory = ( ( Element ) eachNode ).attributeValue( "IsMandatory" );
				String isPrimaryKey = ( ( Element ) eachNode ).attributeValue( "IsPrimaryKey" );
				String camelCaseColName = StringUtils.underScoreToCamelCase( columnName );
				String colName = camelCaseColName.substring( 0, 1 ).toLowerCase() + camelCaseColName.substring( 1, camelCaseColName.length() );
				if ( isPrimaryKey.equals( "true" ) )
				{
					out.write( "	@org.springframework.data.cassandra.mapping.PrimaryKeyColumn(name = \"id\", ordinal = 2, type = org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED, ordering = org.springframework.cassandra.core.Ordering.DESCENDING)" );
					out.newLine();
					out.write( "	public int get" + camelCaseColName + "() { return getId(); }" );
					out.newLine();
					out.write( "	public void set" + camelCaseColName + "( int id ) { setId( id ); } " );
					out.newLine();
					out.newLine();
					continue;
				}
				else if ( columnName.equals( "ptn_id" ) )
				{
					out.write( "	@org.springframework.data.cassandra.mapping.PrimaryKeyColumn(name = \"id\", ordinal = 2, type = org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED, ordering = org.springframework.cassandra.core.Ordering.DESCENDING)" );
					out.newLine();
					out.write( "	public int getPartitionId() { return super.getPartitionId(); }" );
					out.newLine();
					out.newLine();
					continue;
				}
				else
				{
					out.newLine();
					out.write( "	@org.springframework.data.cassandra.mapping.Column" );
					out.newLine();
					if ( dataType.equals( "String" ) )
					{
						out.write( "	private " + dataType + " " + colName + ";" );
						out.newLine();
						out.write( "	public String get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( String val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Integer" ) )
					{
						out.write( "	private " + dataType + " " + colName + ";" );
						out.newLine();
						out.write( "	public Integer get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Integer val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Boolean" ) )
					{
						out.write( "	private " + dataType + " " + colName + ";" );
						out.newLine();
						out.write( "	public Boolean get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Boolean val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "DateTime" ) )
					{
						out.write( "	private " + "org.joda.time.DateTime" + " " + colName + ";" );
						out.newLine();
						out.write( "	public org.joda.time.DateTime get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( org.joda.time.DateTime val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Long" ) )
					{
						out.write( "	private " + dataType + " " + colName + ";" );
						out.newLine();
						out.write( "	public Long get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Long val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					out.newLine();
				}
			}
		}
	}

	private void createIndexAnnotations( BufferedWriter out, List<Node> indexes, String tablePrefix, String tableName ) throws IOException
	{
		if ( indexes.size() > 0 )
		{
			out.write( "@org.amaze.db.hibernate.annotations.Table( tableName = \"" + tableName + "\", tablePrefix = \"" + tablePrefix + "\", indexes = {" );
			out.newLine();
			for ( int i = 0; i < indexes.size(); i++ )
			{
				Node eachNode = indexes.get( i );
				if ( eachNode instanceof Element )
				{
					String indexName = ( ( Element ) eachNode ).attributeValue( "IndexName" );
					String columnList = ( ( Element ) eachNode ).attributeValue( "ColumnList" );
					out.write( "	@org.amaze.db.hibernate.annotations.Index( indexName = \"" + indexName + "\", columnList = { \"" + columnList + "\" } )," );
					out.newLine();
				}
			}
			out.write( "} )" );
		}
	}

	private void addEntityAnnotation( BufferedWriter out, String tableName, List<Node> columns ) throws IOException
	{
		out.write( "@org.springframework.data.cassandra.mapping.Table" );
		out.newLine();
	}

	private void addImports( BufferedWriter out ) throws IOException
	{
		out.write( "import java.io.Serializable;" );
		out.newLine();
		out.write( "import org.amaze.db.usage.AbstractUsageObject;" );
		out.newLine();
	}

	private void clearOldSrcFiles() throws IOException
	{
		String srcFolder = "." + File.separator + "src" + File.separator + packageName.replace( ".", File.separator ) + File.separator;
		File file = new File( srcFolder );
		if ( file.isDirectory() )
		{
			File[] list = file.listFiles();
			for ( File eachFile : list )
				eachFile.delete();
		}
	}	
	
	private void validate( Document doc )
	{
		Element schemaElement = doc.getRootElement();
		if ( schemaElement.attributeValue( "SchemaName" ).equals( "AmazeUsage" ) )
		{
			packageName = schemaElement.attributeValue( "ServerPackageName" );
			amazeVersion = schemaElement.attributeValue( "MajorVersion" ) + "_" + schemaElement.attributeValue( "MinorVersion" ) + "_" + schemaElement.attributeValue( "ServicePack" );
		}
		else
		{
			throw new GeneratorException( "Schema file has invalid Database name" );
		}
	}
	
	public static void main( String[] args ) throws ParserConfigurationException, IOException
	{
		new GenerateUsageObjects().generateObjects( args[0], args[1] );
//		new GenerateUsageObjects().generateObjects( "/org/amaze/db/metadata/Amaze-UsageSchema.xml", "./src/main/java/" );
	}

}
