package org.amaze.db.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.amaze.commons.utils.StringUtils;
import org.amaze.commons.xml.XMLTransform;
import org.amaze.commons.xml.exceptions.XMLException;
import org.amaze.db.generator.exceptions.GeneratorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GenerateObjects
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
		//		Document doc = transform.getXMLDocumentObj("org.amaze.database.hibernate.configuration.Amaze_Schema", false);
		try
		{
			doc = transform.getXMLDocumentObj( schemaFile, false );
			NodeList elements = doc.getElementsByTagName( "Table" );
			for ( int i = 0; i < elements.getLength(); i++ )
			{
				Element element = ( Element ) elements.item( i );
				;
				tableNameTablePrefixMap.put( StringUtils.underScoreToCamelCase( element.getAttribute( "TableName" ) ), element.getAttribute( "TablePrefix" ) );
			}
			validate( doc );
			clearOldSrcFiles();
			parseSchemaFile( doc );
			NodeList extendsTag = doc.getElementsByTagName( "Extends" );
			for ( int i = 0; i < extendsTag.getLength(); i++ )
			{
				Element element = ( Element ) extendsTag.item( i );
				parseSchemaFile( transform.getXMLDocumentObj( element.getNodeValue(), false ) );
			}
		}
		catch ( XMLException | IOException e )
		{
			throw new GeneratorException( e );
		}
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

	private void parseSchemaFile( Document doc )
	{
		NodeList database = doc.getElementsByTagName( "Database" );
		if ( database.getLength() == 1 )
		{
			Node eachDatabase = database.item( 0 );
			if ( ( ( Element ) eachDatabase ).getAttribute( "DatabaseName" ).equals( "amaze" ) )
			{
				parseDatabase( database.item( 0 ) );
			}
			else
				throw new GeneratorException( "No Amaze database configured" );
		}
		else
			throw new GeneratorException( "Multiple databases configured" );
	}

	private void parseDatabase( Node eachDatabase )
	{
		NodeList tablesTags = ( ( Element ) eachDatabase ).getElementsByTagName( "Tables" );
		if ( tablesTags.getLength() == 1 )
		{
			Node tablesTag = tablesTags.item( 0 );
			NodeList tables = ( ( Element ) tablesTag ).getElementsByTagName( "Table" );
			for ( int i = 0; i < tables.getLength(); i++ )
				parseTables( tables.item( i ) );
		}
		else
			throw new GeneratorException( "Multiple Tables tag configured" );
	}

	private void parseTables( Node tableTag )
	{
		Element table = ( Element ) tableTag;
		String tableName = table.getAttribute( "TableName" );
		String tablePrefix = table.getAttribute( "TablePrefix" );
		NodeList columns = table.getElementsByTagName( "Columns" );
		if ( columns.getLength() != 1 )
			throw new GeneratorException( "Multiple no of Columns tag configured" );
		columns = columns.item( 0 ).getChildNodes();
		NodeList indexes = table.getElementsByTagName( "Indexes" );
		if ( indexes.getLength() != 1 )
			throw new GeneratorException( "Multiple no of Indexes tag configured" );
		indexes = indexes.item( 0 ).getChildNodes();
		NodeList nestedCollections = table.getElementsByTagName( "NestedCollections" );
		if ( nestedCollections.getLength() > 1 )
			throw new GeneratorException( "Multiple no of NestedCollections tag configured" );
		if ( nestedCollections.getLength() != 0 )
			nestedCollections = nestedCollections.item( 0 ).getChildNodes();
		try
		{
			createHibernateObject( tableName, tablePrefix, columns, indexes, nestedCollections );
		}
		catch ( IOException e )
		{
			throw new GeneratorException( e );
		}
	}

	private void createHibernateObject( String tableName, String tablePrefix, NodeList columns, NodeList indexes, NodeList nestedCollections ) throws IOException
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
		out.write( "public class " + classFileName + " extends AbstractHibernateObject implements Serializable " );
		out.newLine();
		out.write( "{" );
		out.newLine();
		out.newLine();
		out.write( "    private static final long serialVersionUID = 1L;" );
		out.newLine();
		out.newLine();
		createFieldMappings( out, columns, tablePrefix );
		createNestedCollectionsMapping( out, nestedCollections, classFileName );
		out.write( "}" );
		out.flush();
		out.close();
	}

	private void createIndexAnnotations( BufferedWriter out, NodeList indexes, String tablePrefix, String tableName ) throws IOException
	{
		if ( indexes.getLength() > 0 )
		{
			out.write( "@org.amaze.db.hibernate.annotations.Table( tableName = \"" + tableName + "\", tablePrefix = \"" + tablePrefix + "\", indexes = {" );
			out.newLine();
			for ( int i = 0; i < indexes.getLength(); i++ )
			{
				Node eachNode = indexes.item( i );
				if ( eachNode instanceof Element )
				{
					String indexName = ( ( Element ) eachNode ).getAttribute( "IndexName" );
					String isUnique = ( ( Element ) eachNode ).getAttribute( "IsUnique" );
					String isClustered = ( ( Element ) eachNode ).getAttribute( "IsClustered" );
					String isBusinessConstraint = ( ( Element ) eachNode ).getAttribute( "IsBusinessConstraint" );
					String isDisplayName = ( ( Element ) eachNode ).getAttribute( "IsDisplayName" );
					String columnList = ( ( Element ) eachNode ).getAttribute( "ColumnList" );
					out.write( "	@org.amaze.db.hibernate.annotations.Index( indexName = \"" + indexName + "\", isUnique = \"" + isUnique + "\", isClustered = \"" + isClustered + "\", isBusinessConstraint = \"" + isBusinessConstraint + "\", displayName = \"" + isDisplayName + "\", columnNames = { \"" + columnList + "\" } )," );
					out.newLine();
				}
			}
			out.write( "} )" );
		}
	}

	private void createNestedCollectionsMapping( BufferedWriter out, NodeList nestedCollections, String currentObjectname ) throws IOException
	{
		for ( int i = 0; i < nestedCollections.getLength(); i++ )
		{
			Node eachNode = nestedCollections.item( i );
			if ( eachNode instanceof Element )
			{
				String propertyName = ( ( Element ) eachNode ).getAttribute( "PropertyName" );
				String foreignObjectMappingName = ( ( Element ) eachNode ).getAttribute( "ForeignObjectMappingName" );
				String foreignPropertyName = ( ( Element ) eachNode ).getAttribute( "ForeignPropertyName" );
				out.write( "	private java.util.Collection< " + foreignObjectMappingName + " > " + propertyName + " = new java.util.ArrayList< " + foreignObjectMappingName + " >();" );
				out.newLine();
				out.write( "	@javax.persistence.OneToMany( fetch = javax.persistence.FetchType.LAZY, mappedBy = \"" + foreignPropertyName + "\" )" );
				out.newLine();
				out.write( "	public java.util.Collection< " + foreignObjectMappingName + " > get" + foreignObjectMappingName + "s() { return " + propertyName + "; }" );
				out.newLine();
				out.write( "	public void add" + foreignObjectMappingName + "s( " + foreignObjectMappingName + " var  ) { " );
				out.write( "		var.set" + currentObjectname + "( this ); " );
				out.write( "		" + propertyName + ".add( var ); " );
				out.write( "	}" );
				out.newLine();
				out.write( "	public void set" + foreignObjectMappingName + "s( java.util.Collection< " + foreignObjectMappingName + " > val ) { this." + propertyName + " = val; }" );
				out.newLine();
			}
		}
	}

	private void createFieldMappings( BufferedWriter out, NodeList columns, String prefix ) throws IOException
	{
		for ( int i = 0; i < columns.getLength(); i++ )
		{
			Node eachNode = columns.item( i );
			if ( eachNode instanceof Element )
			{
				String columnName = ( ( Element ) eachNode ).getAttribute( "ColumnName" );
				String dataType = ( ( Element ) eachNode ).getAttribute( "DataType" );
				String length = ( ( Element ) eachNode ).getAttribute( "Length" );
				String isMandatory = ( ( Element ) eachNode ).getAttribute( "IsMandatory" );
				String isPrimaryKey = ( ( Element ) eachNode ).getAttribute( "IsPrimaryKey" );
				String isOneToOneNestedObject = ( ( Element ) eachNode ).getAttribute( "IsOneToOneNestedObject" );
				String nestedObject = ( ( Element ) eachNode ).getAttribute( "NestedObject" );
				String camelCaseColName = StringUtils.underScoreToCamelCase( columnName );
				String colName = camelCaseColName.substring( 0, 1 ).toLowerCase() + camelCaseColName.substring( 1, camelCaseColName.length() );
				if ( isPrimaryKey.equals( "true" ) )
				{
					out.write( "	@javax.persistence.Id" );
					out.newLine();
					out.write( "	@javax.validation.constraints.Min( 1 )" );
					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"" + columnName + "\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"int\" )" );
					out.newLine();
					out.write( "	public int get" + colName + "() { return getId(); }" );
					out.newLine();
					out.write( "	public void set" + colName + "( int id ) { setId( id ); } " );
					out.newLine();
					out.newLine();
					continue;
				}
				else if ( columnName.equals( prefix + "_version_id" ) )
				{
					out.write( "	@javax.persistence.Version" );
					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"" + columnName + "\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"int\" )" );
					out.newLine();
					out.write( "	public int getVersionId() { return super.getVersionId(); }" );
					out.newLine();
					out.newLine();
					continue;
				}
				else if ( columnName.equals( prefix + "_delete_fl" ) )
				{
					out.write( "	@javax.persistence.Basic" );
					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"" + columnName + "\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"yes_no\" )" );
					out.newLine();
					out.write( "	@javax.validation.constraints.NotNull" );
					out.newLine();
					out.write( "	public Boolean getDeleteFl() { return super.getDeleteFl(); }" );
					out.newLine();
					out.newLine();
					continue;
				}
				else if ( columnName.equals( "system_generated_fl" ) )
				{
					out.write( "	@javax.persistence.Basic" );
					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"system_generated_fl\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"yes_no\" )" );
					out.newLine();
					out.write( "	public Boolean getSystemGeneratedFl() { return super.getSystemGeneratedFl(); }" );
					out.newLine();
					out.newLine();
					continue;
				}
				else if ( columnName.equals( "ptn_id" ) )
				{
					out.write( "	@javax.persistence.Basic" );
					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"ptn_id\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"int\" )" );
					out.newLine();
					out.write( "	@javax.validation.constraints.Min( 0 )" );
					out.newLine();
					out.write( "	public int getPartitionId() { return super.getPartitionId(); }" );
					out.newLine();
					out.newLine();
					continue;
				}
				else
				{
					if ( dataType.equals( "DateTime" ) )
						out.write( "	private " + "org.joda.time.DateTime" + " " + colName + ";" );
					else
						out.write( "	private " + dataType + " " + colName + ";" );
					out.newLine();
					out.write( "	@javax.persistence.Basic" );
					out.newLine();
					boolean isEditable = isOneToOneNestedObject.equals( "true" ) ? false : true;
					out.write( "	@javax.persistence.Column( name = \"" + columnName + "\", nullable = " + isMandatory + ", insertable = " + isEditable + ", updatable = " + isEditable + " )" );
					out.newLine();
					if ( dataType.equals( "String" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"org.amaze.db.hibernate.types.StringType\" )" );
						out.newLine();
						out.write( "	@javax.validation.constraints.NotNull" );
						out.newLine();
						out.write( "	@org.hibernate.validator.constraints.NotEmpty" );
						out.newLine();
						out.write( "	@org.hibernate.validator.constraints.Length( max = " + length + " )" );
						out.newLine();
						out.write( "	public String get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( String val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Integer" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"int\" )" );
						out.newLine();
						out.write( "	public Integer get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Integer val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Boolean" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"yes_no\" )" );
						out.newLine();
						out.write( "	@javax.validation.constraints.NotNull" );
						out.newLine();
						out.write( "	public Boolean get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Boolean val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "DateTime" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"org.amaze.db.hibernate.types.DateTimeType\" )" );
						out.newLine();
						out.write( "	public org.joda.time.DateTime get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( org.joda.time.DateTime val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Long" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"Long\" )" );
						out.newLine();
						out.write( "	public Long get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Long val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					out.newLine();
					if ( isOneToOneNestedObject.equals( "true" ) )
					{
						String nestedTypeName =/* StringUtils.camelCaseToUnderScore(*/ nestedObject /*)*/;
						String nestedVarName = nestedTypeName.substring( 0, 1 ).toLowerCase() + nestedTypeName.substring( 1, nestedTypeName.length() );
						out.write( "	@javax.persistence.ManyToOne( fetch = javax.persistence.FetchType.LAZY )" );
						out.newLine();
						out.write( "	@javax.persistence.JoinColumn( name = \"" + tableNameTablePrefixMap.get( nestedTypeName ) + "_id" + "\", nullable = " + isMandatory + ", insertable = true, updatable = true )" );
						out.newLine();
						out.write( "	@org.hibernate.annotations.NotFound( action = org.hibernate.annotations.NotFoundAction.IGNORE )" );
						out.newLine();
						out.write( "	private " + nestedTypeName + " " + nestedVarName + " ;" );
						out.newLine();
						out.write( "	public void set" + nestedTypeName + "( " + nestedTypeName + " " + nestedVarName + ")" );
						out.write( "	{" );
						out.write( "		this." + nestedVarName + " = " + nestedVarName + ";" );
						out.write( "	}" );
						out.newLine();
						out.write( "	public " + nestedTypeName + " get" + nestedTypeName + "()" );
						out.write( "	{" );
						out.write( "		return this." + nestedVarName + ";" );
						out.write( "	}" );
						out.newLine();
					}
					out.newLine();
				}
			}
		}
	}

	private void addEntityAnnotation( BufferedWriter out, String tableName, NodeList columns ) throws IOException
	{
		out.write( "@javax.persistence.Entity" );
		out.newLine();
		out.write( "@javax.persistence.Table( name = \"" + tableName + "\" )" );
		out.newLine();
		String deleteFlName = null;
		for ( int i = 0; i < columns.getLength(); i++ )
		{
			if ( columns.item( i ).getNextSibling().getAttributes() != null )
			{
				deleteFlName = ( columns.item( i ).getNextSibling() ).getAttributes().getNamedItem( "ColumnName" ).getNodeValue();
				if ( deleteFlName.endsWith( "_delete_fl" ) )
				{
					break;
				}
			}
		}
		out.write( "@org.hibernate.annotations.Filters( { " + "@org.hibernate.annotations.Filter( name = \"partitionFilter\" , condition = \"ptn_id = :partitionId\" ), " + "@org.hibernate.annotations.Filter( name = \"deletedFilter\", condition = \"" + deleteFlName + " = :deleteFl\" ) " + "} )" );
		out.newLine();
	}

	private void addImports( BufferedWriter out ) throws IOException
	{
		out.write( "import java.io.Serializable;" );
		out.newLine();
		out.write( "import org.amaze.db.hibernate.AbstractHibernateObject;" );
		out.newLine();
	}

	private void validate( Document doc )
	{
		Element schemaElement = doc.getDocumentElement();
		if ( schemaElement.getAttribute( "SchemaName" ).equals( "Amaze" ) )
		{
			packageName = schemaElement.getAttribute( "ServerPackageName" );
			amazeVersion = schemaElement.getAttribute( "MajorVersion" ) + "_" + schemaElement.getAttribute( "MinorVersion" ) + "_" + schemaElement.getAttribute( "ServicePack" );
		}
		else
		{
			throw new GeneratorException( "Schema file has invalid Database name" );
		}
	}

	public static void main( String[] args ) throws ParserConfigurationException, SAXException, IOException
	{
		new GenerateObjects().generateObjects( args[0], args[1] );
//		new GenerateObjects().generateObjects( "/org/amaze/db/metadata/Amaze-Schema.xml" );
	}

}