package org.amaze.db.usage.objects;
import java.io.Serializable;
import org.amaze.db.usage.AbstractUsageObject;

@org.springframework.data.cassandra.mapping.Table

@org.amaze.db.hibernate.annotations.Table( tableName = "login_event", tablePrefix = "let", indexes = {
	@org.amaze.db.hibernate.annotations.Index( indexName = "let_idx1", columnList = { "tab_id" } ),
} )
public class LoginEvent extends AbstractUsageObject implements Serializable 
{

    private static final long serialVersionUID = 1L;
	@org.springframework.data.cassandra.mapping.PrimaryKeyColumn(name = "id", ordinal = 2, type = org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED, ordering = org.springframework.cassandra.core.Ordering.DESCENDING)
	public int getLetId() { return getId(); }
	public void setLetId( int id ) { setId( id ); } 


	@org.springframework.data.cassandra.mapping.Column
	private Integer usrId;
	public Integer getUsrId() { return this.usrId; }
	public void setUsrId( Integer val ) {this.usrId = val; }


	@org.springframework.data.cassandra.mapping.Column
	private org.joda.time.DateTime letLoggedDttm;
	public org.joda.time.DateTime getLetLoggedDttm() { return this.letLoggedDttm; }
	public void setLetLoggedDttm( org.joda.time.DateTime val ) {this.letLoggedDttm = val; }


	@org.springframework.data.cassandra.mapping.Column
	private org.joda.time.DateTime letLoggoffDttm;
	public org.joda.time.DateTime getLetLoggoffDttm() { return this.letLoggoffDttm; }
	public void setLetLoggoffDttm( org.joda.time.DateTime val ) {this.letLoggoffDttm = val; }


	@org.springframework.data.cassandra.mapping.Column
	private Boolean letSessionTerminated;
	public Boolean getLetSessionTerminated() { return this.letSessionTerminated; }
	public void setLetSessionTerminated( Boolean val ) {this.letSessionTerminated = val; }


	@org.springframework.data.cassandra.mapping.Column
	private Integer letAccessClient;
	public Integer getLetAccessClient() { return this.letAccessClient; }
	public void setLetAccessClient( Integer val ) {this.letAccessClient = val; }


	@org.springframework.data.cassandra.mapping.Column
	private org.joda.time.DateTime letCreatedDttm;
	public org.joda.time.DateTime getLetCreatedDttm() { return this.letCreatedDttm; }
	public void setLetCreatedDttm( org.joda.time.DateTime val ) {this.letCreatedDttm = val; }

	@org.springframework.data.cassandra.mapping.PrimaryKeyColumn(name = "id", ordinal = 2, type = org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED, ordering = org.springframework.cassandra.core.Ordering.DESCENDING)
	public int getPtnId() { return getId(); }
	public void setPtnId( int id ) { setId( id ); } 


}