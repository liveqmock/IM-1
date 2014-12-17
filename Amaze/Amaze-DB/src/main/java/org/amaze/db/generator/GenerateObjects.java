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

@SuppressWarnings( "unchecked" )
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
				parseSchemaFile( transform.getXMLDocumentObj( "/org/amaze/db/metadata/" + element.getText(), false ) );
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
		for( Object eachNode : doc.selectNodes( "//Schema/Database" ) )
		{
			Node eachDatabase = (Node) eachNode;
			if ( ( ( Element ) eachDatabase ).attributeValue( "DatabaseName" ).equals( "amaze" ) )
			{
				parseDatabase( eachDatabase );
			}
		}
	}

	private void parseDatabase( Node eachDatabase )
	{
		List<Node> tablesTags = eachDatabase.selectNodes( "Tables" );  
		if ( tablesTags.size() == 1 )
		{
			Node tablesTag = tablesTags.get( 0 );
			for( Object eachTable : tablesTag.selectNodes( "Table" ) )
				parseTables( (Node) eachTable );
		}
		else
			throw new GeneratorException( "Multiple Tables tag configured" );
	}

	private void parseTables( Node tableTag )
	{
		Element table = ( Element ) tableTag;
		String tableName = table.attributeValue( "TableName" );
		String tablePrefix = table.attributeValue( "TablePrefix" );
		String displayName = table.attributeValue( "DisplayName" );
		List<Node> columns = table.selectNodes( "Columns" );
		if ( columns.size() != 1 )
			throw new GeneratorException( "Multiple no of Columns tag configured" );
		columns = columns.get( 0 ).selectNodes( "Column" );
		List<Node> indexes = table.selectNodes( "Indexes" );
		if ( indexes.size() != 1 )
			throw new GeneratorException( "Multiple no of Indexes tag configured" );
		indexes = indexes.get( 0 ).selectNodes( "Index" );
		List<Node> nestedCollections = table.selectNodes( "NestedCollections" );
		if ( nestedCollections.size() > 1 )
			throw new GeneratorException( "Multiple no of NestedCollections tag configured" );
		if ( nestedCollections.size() != 0 )
			nestedCollections = nestedCollections.get( 0 ).selectNodes( "NestedCollection" );
		try
		{
			createHibernateObject( tableName, tablePrefix, displayName, columns, indexes, nestedCollections );
		}
		catch ( IOException e )
		{
			throw new GeneratorException( e );
		}
	}

	private void createHibernateObject( String tableName, String tablePrefix, String displayName, List<Node> columns, List<Node> indexes, List<Node> nestedCollections ) throws IOException
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
		createIndexAnnotations( out, indexes, tablePrefix, tableName, displayName );
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

	private void createIndexAnnotations( BufferedWriter out, List<Node> indexes, String tablePrefix, String tableName, String displayName ) throws IOException
	{
		if ( indexes.size() > 0 )
		{
			out.write( "@org.amaze.db.hibernate.annotations.Table( tableName = \"" + tableName + "\", tablePrefix = \"" + tablePrefix + "\", displayName = \"" + tablePrefix + "\", indexes = {" );
			out.newLine();
			for ( int i = 0; i < indexes.size(); i++ )
			{
				Node eachNode = indexes.get( i );
				if ( eachNode instanceof Element )
				{
					String indexName = ( ( Element ) eachNode ).attributeValue( "IndexName" );
					String isUnique = ( ( Element ) eachNode ).attributeValue( "IsUnique" );
					String isClustered = ( ( Element ) eachNode ).attributeValue( "IsClustered" );
					String isBusinessConstraint = ( ( Element ) eachNode ).attributeValue( "IsBusinessConstraint" );
					String columnList = ( ( Element ) eachNode ).attributeValue( "ColumnList" );
					String condition = ( ( Element ) eachNode ).attributeValue( "Condition" );
					if( !( i + 1 == indexes.size() ) )
						out.write( "	@org.amaze.db.hibernate.annotations.Index( indexName = \"" + indexName + "\", isUnique = \"" + isUnique + "\", isClustered = \"" + isClustered + "\", isBusinessConstraint = \"" + isBusinessConstraint + "\", columnList = { \"" + columnList + "\" }, condition = \"" + condition + "\" ), " );
					else
						out.write( "	@org.amaze.db.hibernate.annotations.Index( indexName = \"" + indexName + "\", isUnique = \"" + isUnique + "\", isClustered = \"" + isClustered + "\", isBusinessConstraint = \"" + isBusinessConstraint + "\", columnList = { \"" + columnList + "\" }, condition = \"" + condition + "\" ) " );
					out.newLine();
				}
			}
			out.write( "} )" );
		}
	}

	private void createNestedCollectionsMapping( BufferedWriter out, List<Node> nestedCollections, String currentObjectname ) throws IOException
	{
//		private java.util.List< UserRoleMap > userRoleMaps = new java.util.ArrayList< UserRoleMap >();
//		@javax.persistence.OneToMany( fetch = javax.persistence.FetchType.EAGER, mappedBy = "users" )
//		public java.util.List< UserRoleMap > getUserRoleMaps() { return userRoleMaps; }
//		public void setUserRoleMaps( java.util.List< UserRoleMap > val ) { this.userRoleMaps = val; }
//		public void addUserRoleMaps( UserRoleMap var  ) { 		var.setUsers( this ); 		userRoleMaps.add( var ); 	}
		
		
		
		for ( int i = 0; i < nestedCollections.size(); i++ )
		{
			Node eachNode = nestedCollections.get( i );
			if ( eachNode instanceof Element )
			{
				String propertyName = ( ( Element ) eachNode ).attributeValue( "PropertyName" );
				String foreignObjectMappingName = ( ( Element ) eachNode ).attributeValue( "ForeignObjectMappingName" );
				String foreignPropertyName = ( ( Element ) eachNode ).attributeValue( "ForeignPropertyName" );
				out.write( "	private java.util.List< " + foreignObjectMappingName + " > " + propertyName + " = new java.util.ArrayList< " + foreignObjectMappingName + " >();" );
				out.newLine();
				out.write( "	@javax.persistence.OneToMany( fetch = javax.persistence.FetchType.EAGER, mappedBy = \"" + foreignPropertyName + "\" )" );
				out.newLine();
				out.write( "	public java.util.List< " + foreignObjectMappingName + " > get" + foreignObjectMappingName + "s() { return " + propertyName + "; }" );
				out.newLine();
				out.write( "	public void set" + foreignObjectMappingName + "s( java.util.List< " + foreignObjectMappingName + " > val ) { this." + propertyName + " = val; }" );
				out.newLine();
				out.write( "	public void add" + foreignObjectMappingName + "s( " + foreignObjectMappingName + " var  ) { " );
				out.write( "		var.set" + currentObjectname + "( this ); " );
				out.write( "		" + propertyName + ".add( var ); " );
				out.write( "	}" );
				out.newLine();

			}
		}
	}

	private void createFieldMappings( BufferedWriter out, List<Node> columns, String prefix ) throws IOException
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
				String nestedObject = ( ( Element ) eachNode ).attributeValue( "NestedObject" );
				String camelCaseColName = StringUtils.underScoreToCamelCase( columnName );
				String colName = camelCaseColName.substring( 0, 1 ).toLowerCase() + camelCaseColName.substring( 1, camelCaseColName.length() );
				if ( isPrimaryKey.equals( "true" ) )
				{
					out.write( "	@javax.persistence.Id" );
					out.newLine();
					out.write( "	@javax.persistence.GeneratedValue" );
					out.newLine();
//					out.write( "	@javax.validation.constraints.Min( 1 )" );
//					out.newLine();
					out.write( "	@javax.persistence.Column( name = \"" + columnName + "\" )" );
					out.newLine();
					out.write( "	@org.hibernate.annotations.Type( type = \"int\" )" );
					out.newLine();
					out.write( "	public int get" + camelCaseColName + "() { return getId(); }" );
					out.newLine();
					out.write( "	public void set" + camelCaseColName + "( int id ) { setId( id ); } " );
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
					out.write( "	public Boolean get" + camelCaseColName + "() { return super.getDeleteFl(); }" );
					out.newLine();
					out.write( "	public void set" + camelCaseColName + "( Boolean flag ) { super.setDeleteFl( flag ); } " );
					out.newLine();
					out.newLine();
					continue;
				}
				else
				{
					if( dataType.equals( "DateTime" ) )
						out.write( "	private " + "org.joda.time.DateTime" + " " + colName + ";" );
					else
						out.write( "	private " + dataType + " " + colName + ";" );
					out.newLine();
					out.newLine();
					out.write( "	@javax.persistence.Basic" );
					out.newLine();
					boolean isEditable = !nestedObject.equals( "" ) ? false : true;
					if( isEditable )
						out.write( "	@javax.persistence.Column( name = \"" + columnName + "\", nullable = " + isMandatory + ", insertable = " + isEditable + ", updatable = " + isEditable + " )" );
					else
						out.write( "	@javax.persistence.Column( name = \"" + columnName + "\", nullable = " + isMandatory + ", insertable = " + "false" + ", updatable = " + "false" + " )" );
					out.newLine();
					if ( dataType.equals( "String" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"org.amaze.db.hibernate.types.StringType\" )" );
						out.newLine();
						if( nestedObject.equals( "" ) && isMandatory.equals( "true" ) )
						{
							out.write( "	@javax.validation.constraints.NotNull" );
							out.newLine();
							out.write( "	@org.hibernate.validator.constraints.NotEmpty" );
							out.newLine();
						}
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
						if( nestedObject.equals( "" ) && isMandatory.equals( "true" ) )
						{
							out.write( "	@javax.validation.constraints.NotNull" );
							out.newLine();
						}
						out.write( "	public Integer get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Integer val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Boolean" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"yes_no\" )" );
						out.newLine();
						if( nestedObject.equals( "" ) && isMandatory.equals( "true" ) )
						{
							out.write( "	@javax.validation.constraints.NotNull" );
							out.newLine();
						}
						out.write( "	public Boolean get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Boolean val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "DateTime" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"org.amaze.db.hibernate.types.DateTimeType\" )" );
						out.newLine();
						if( nestedObject.equals( "" ) && isMandatory.equals( "true" ) )
						{
							out.write( "	@javax.validation.constraints.NotNull" );
							out.newLine();
						}
						out.write( "	public org.joda.time.DateTime get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( org.joda.time.DateTime val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					else if ( dataType.equals( "Long" ) )
					{
						out.write( "	@org.hibernate.annotations.Type( type = \"Long\" )" );
						out.newLine();
						if( nestedObject.equals( "" ) && isMandatory.equals( "true" ) )
						{
							out.write( "	@javax.validation.constraints.NotNull" );
							out.newLine();
						}
						out.write( "	public Long get" + camelCaseColName + "() { return this." + colName + "; }" );
						out.newLine();
						out.write( "	public void set" + camelCaseColName + "( Long val ) {this." + colName + " = val; }" );
						out.newLine();
					}
					out.newLine();
					if ( !nestedObject.equals( "" ) )
					{
						String nestedTypeName =/* StringUtils.camelCaseToUnderScore(*/ nestedObject /*)*/;
						String nestedVarName = nestedTypeName.substring( 0, 1 ).toLowerCase() + nestedTypeName.substring( 1, nestedTypeName.length() );
						out.write( "	private " + nestedTypeName + " " + nestedVarName + " ;" );
						out.newLine();
						out.newLine();
						out.write( "	@javax.persistence.ManyToOne( fetch = javax.persistence.FetchType.LAZY )" );
						out.newLine();
						out.write( "	@javax.persistence.JoinColumn( name = \"" + tableNameTablePrefixMap.get( nestedTypeName ) + "_id" + "\", nullable = " + isMandatory + ", insertable = true, updatable = true )" );
						out.newLine();
						out.write( "	@org.hibernate.annotations.NotFound( action = org.hibernate.annotations.NotFoundAction.IGNORE )" );
						out.newLine();
						out.write( "	public " + nestedTypeName + " get" + nestedTypeName + "()" );
						out.write( "	{" );
						out.write( "		return this." + nestedVarName + ";" );
						out.write( "	}" );
						out.newLine();
						out.write( "	public void set" + nestedTypeName + "( " + nestedTypeName + " " + nestedVarName + ")" );
						out.write( "	{" );
						out.write( "		this." + nestedVarName + " = " + nestedVarName + ";" );
						out.write( "	}" );
						out.newLine();
					}
					out.newLine();
				}
			}
		}
	}

	private void addEntityAnnotation( BufferedWriter out, String tableName, List<Node> columns ) throws IOException
	{
		out.write( "@javax.persistence.Entity" );
		out.newLine();
		out.write( "@javax.persistence.Table( name = \"" + tableName + "\" )" );
		out.newLine();
		String deleteFlName = null;
		for ( int i = 0; i < columns.size(); i++ )
		{
			if ( ( (Element) columns.get( i ) ).attributes() != null )
			{
				deleteFlName = ( (Element)columns.get( i )).attribute( "ColumnName" ).getText();
				if ( deleteFlName.endsWith( "_delete_fl" ) )
				{
					break;
				}
			}
		}
		out.write("@org.hibernate.annotations.FilterDefs( {");
		out.write( "	@org.hibernate.annotations.FilterDef( name = \"deletedFilter\" , parameters={ @org.hibernate.annotations.ParamDef(name=\"delete_fl\", type=\"java.lang.Boolean\") } )" );
		out.write("} )");
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
		Element schemaElement = doc.getRootElement();
		if ( schemaElement.attributeValue( "SchemaName" ).equals( "Amaze" ) )
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
//		new GenerateObjects().generateObjects( args[0], args[1] );
		new GenerateObjects().generateObjects( "/org/amaze/db/metadata/Amaze-Schema.xml", "./src/main/java/" );
	}

}