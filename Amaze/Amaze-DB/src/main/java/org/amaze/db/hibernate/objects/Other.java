package org.amaze.db.hibernate.objects;
import java.io.Serializable;
import org.amaze.db.hibernate.AbstractHibernateObject;

@javax.persistence.Entity
@javax.persistence.Table( name = "other" )
@org.hibernate.annotations.Filters( { @org.hibernate.annotations.Filter( name = "partitionFilter" , condition = "ptn_id = :partitionId" ), @org.hibernate.annotations.Filter( name = "deletedFilter", condition = "oth_delete_fl = :deleteFl" ) } )

@org.amaze.db.hibernate.annotations.Table( tableName = "other", tablePrefix = "oth", indexes = {
	@org.amaze.db.hibernate.annotations.Index( indexName = "other_pk", isUnique = "true", isClustered = "false", isBusinessConstraint = "false", displayName = "false", columnNames = { "oth_id" } ),
	@org.amaze.db.hibernate.annotations.Index( indexName = "other_ss1", isUnique = "true", isClustered = "false", isBusinessConstraint = "false", displayName = "false", columnNames = { "oth_id,oth_name" } ),
} )
public class Other extends AbstractHibernateObject implements Serializable 
{

    private static final long serialVersionUID = 1L;

	@javax.persistence.Id
	@javax.validation.constraints.Min( 1 )
	@javax.persistence.Column( name = "oth_id" )
	@org.hibernate.annotations.Type( type = "int" )
	public int getothId() { return getId(); }
	public void setothId( int id ) { setId( id ); } 

	private String othName;
	@javax.persistence.Basic
	@javax.persistence.Column( name = "oth_name", nullable = true, insertable = true, updatable = true )
	@org.hibernate.annotations.Type( type = "org.amaze.db.hibernate.types.StringType" )
	@javax.validation.constraints.NotNull
	@org.hibernate.validator.constraints.NotEmpty
	@org.hibernate.validator.constraints.Length( max = 255 )
	public String getOthName() { return this.othName; }
	public void setOthName( String val ) {this.othName = val; }


	private org.joda.time.DateTime othCreatedDttm;
	@javax.persistence.Basic
	@javax.persistence.Column( name = "oth_created_dttm", nullable = false, insertable = true, updatable = true )
	@org.hibernate.annotations.Type( type = "org.amaze.db.hibernate.types.DateTimeType" )
	public org.joda.time.DateTime getOthCreatedDttm() { return this.othCreatedDttm; }
	public void setOthCreatedDttm( org.joda.time.DateTime val ) {this.othCreatedDttm = val; }


	private Integer tstId;
	@javax.persistence.Basic
	@javax.persistence.Column( name = "tst_id", nullable = true, insertable = false, updatable = false )
	@org.hibernate.annotations.Type( type = "int" )
	public Integer getTstId() { return this.tstId; }
	public void setTstId( Integer val ) {this.tstId = val; }

	@javax.persistence.ManyToOne( fetch = javax.persistence.FetchType.LAZY )
	@javax.persistence.JoinColumn( name = "tst_id", nullable = true, insertable = true, updatable = true )
	@org.hibernate.annotations.NotFound( action = org.hibernate.annotations.NotFoundAction.IGNORE )
	private Test test ;
	public void setTest( Test test)	{		this.test = test;	}
	public Test getTest()	{		return this.test;	}

	@javax.persistence.Basic
	@javax.persistence.Column( name = "oth_delete_fl" )
	@org.hibernate.annotations.Type( type = "yes_no" )
	@javax.validation.constraints.NotNull
	public Boolean getDeleteFl() { return super.getDeleteFl(); }

	@javax.persistence.Version
	@javax.persistence.Column( name = "oth_version_id" )
	@org.hibernate.annotations.Type( type = "int" )
	public int getVersionId() { return super.getVersionId(); }

	@javax.persistence.Basic
	@javax.persistence.Column( name = "ptn_id" )
	@org.hibernate.annotations.Type( type = "int" )
	@javax.validation.constraints.Min( 0 )
	public int getPartitionId() { return super.getPartitionId(); }

}