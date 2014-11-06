package org.amaze.db.hibernate.objects;
import java.io.Serializable;
import org.amaze.db.hibernate.AbstractHibernateObject;

@javax.persistence.Entity
@javax.persistence.Table( name = "test" )
@org.hibernate.annotations.Filters( { @org.hibernate.annotations.Filter( name = "partitionFilter" , condition = "ptn_id = :partitionId" ), @org.hibernate.annotations.Filter( name = "deletedFilter", condition = "tst_delete_fl = :deleteFl" ) } )

@org.amaze.db.hibernate.annotations.Table( tableName = "test", tablePrefix = "tst", indexes = {
	@org.amaze.db.hibernate.annotations.Index( indexName = "test_pk", isUnique = "true", isClustered = "false", isBusinessConstraint = "false", displayName = "false", columnNames = { "tst_id" } ),
	@org.amaze.db.hibernate.annotations.Index( indexName = "test_ss1", isUnique = "true", isClustered = "false", isBusinessConstraint = "false", displayName = "false", columnNames = { "tst_id,tst_name" } ),
	@org.amaze.db.hibernate.annotations.Index( indexName = "test_ss2", isUnique = "true", isClustered = "false", isBusinessConstraint = "true", displayName = "true", columnNames = { "tst_id,tst_created_dttm" } ),
	@org.amaze.db.hibernate.annotations.Index( indexName = "test_ss3", isUnique = "false", isClustered = "false", isBusinessConstraint = "false", displayName = "false", columnNames = { "tst_name,tst_created_dttm" } ),
} )
public class Test extends AbstractHibernateObject implements Serializable 
{

    private static final long serialVersionUID = 1L;

	@javax.persistence.Id
	@javax.validation.constraints.Min( 1 )
	@javax.persistence.Column( name = "tst_id" )
	@org.hibernate.annotations.Type( type = "int" )
	public int gettstId() { return getId(); }
	public void settstId( int id ) { setId( id ); } 

	private String tstName;
	@javax.persistence.Basic
	@javax.persistence.Column( name = "tst_name", nullable = true, insertable = true, updatable = true )
	@org.hibernate.annotations.Type( type = "org.amaze.db.hibernate.types.StringType" )
	@javax.validation.constraints.NotNull
	@org.hibernate.validator.constraints.NotEmpty
	@org.hibernate.validator.constraints.Length( max = 255 )
	public String getTstName() { return this.tstName; }
	public void setTstName( String val ) {this.tstName = val; }


	private org.joda.time.DateTime tstCreatedDttm;
	@javax.persistence.Basic
	@javax.persistence.Column( name = "tst_created_dttm", nullable = false, insertable = true, updatable = true )
	@org.hibernate.annotations.Type( type = "org.amaze.db.hibernate.types.DateTimeType" )
	public org.joda.time.DateTime getTstCreatedDttm() { return this.tstCreatedDttm; }
	public void setTstCreatedDttm( org.joda.time.DateTime val ) {this.tstCreatedDttm = val; }


	@javax.persistence.Basic
	@javax.persistence.Column( name = "tst_delete_fl" )
	@org.hibernate.annotations.Type( type = "yes_no" )
	@javax.validation.constraints.NotNull
	public Boolean getDeleteFl() { return super.getDeleteFl(); }

	@javax.persistence.Version
	@javax.persistence.Column( name = "tst_version_id" )
	@org.hibernate.annotations.Type( type = "int" )
	public int getVersionId() { return super.getVersionId(); }

	@javax.persistence.Basic
	@javax.persistence.Column( name = "ptn_id" )
	@org.hibernate.annotations.Type( type = "int" )
	@javax.validation.constraints.Min( 0 )
	public int getPartitionId() { return super.getPartitionId(); }

	private java.util.Collection< Other > others = new java.util.ArrayList< Other >();
	@javax.persistence.OneToMany( fetch = javax.persistence.FetchType.LAZY, mappedBy = "tstId" )
	public java.util.Collection< Other > getOthers() { return others; }
	public void addOthers( Other var  ) { 		var.setTest( this ); 		others.add( var ); 	}
	public void setOthers( java.util.Collection< Other > val ) { this.others = val; }
}