package org.amaze.db.usage.objects;
import java.io.Serializable;
import org.amaze.db.usage.AbstractUsageObject;
import com.impetus.kundera.index.Index;
import com.impetus.kundera.index.IndexCollection;

@javax.persistence.Entity
@javax.persistence.Table(name = "login_event", schema = "amaze@systemusage")

@org.amaze.db.hibernate.annotations.Table( tableName = "login_event", tablePrefix = "let", indexes = {
	@org.amaze.db.hibernate.annotations.Index( indexName = "let_idx1", columnList = { "usr_id" } ),
} )
@IndexCollection(columns = { 
	@Index(name = "usr_id")
})

public class LoginEvent extends AbstractUsageObject implements Serializable 
{

    private static final long serialVersionUID = 1L;
	@javax.persistence.Id
	@javax.persistence.Column( name="let_id", nullable=false  )
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.TABLE)
	private Integer letId;
	public Integer getLetId() { return this.letId; }
	public void setLetId( int id ) { super.setId( id ); this.letId = id; }


	@javax.persistence.Column( name="usr_id", nullable=false  )
	private Integer usrId;
	public Integer getUsrId() { return this.usrId; }
	public void setUsrId( Integer val ) {this.usrId = val; }



	@javax.persistence.Column( name="let_logged_dttm", nullable=false  )
	private org.joda.time.DateTime letLoggedDttm;
	public org.joda.time.DateTime getLetLoggedDttm() { return this.letLoggedDttm; }
	public void setLetLoggedDttm( org.joda.time.DateTime val ) {this.letLoggedDttm = val; }



	@javax.persistence.Column( name="let_loggoff_dttm", nullable=true  )
	private org.joda.time.DateTime letLoggoffDttm;
	public org.joda.time.DateTime getLetLoggoffDttm() { return this.letLoggoffDttm; }
	public void setLetLoggoffDttm( org.joda.time.DateTime val ) {this.letLoggoffDttm = val; }



	@javax.persistence.Column( name="let_session_terminated", nullable=true  )
	private String letSessionTerminated;
	public String getLetSessionTerminated() { return this.letSessionTerminated; }
	public void setLetSessionTerminated( String val ) {this.letSessionTerminated = val; }



	@javax.persistence.Column( name="let_access_client", nullable=true  )
	private String letAccessClient;
	public String getLetAccessClient() { return this.letAccessClient; }
	public void setLetAccessClient( String val ) {this.letAccessClient = val; }



	@javax.persistence.Column( name="let_created_dttm", nullable=false  )
	private org.joda.time.DateTime letCreatedDttm;
	public org.joda.time.DateTime getLetCreatedDttm() { return this.letCreatedDttm; }
	public void setLetCreatedDttm( org.joda.time.DateTime val ) {this.letCreatedDttm = val; }


	@javax.persistence.Column( name="ptn_id", nullable=false  )
	private Integer ptnId;
	public Integer getPtnId() { return ptnId; }
	public void setPtnId( int ptnId ) { this.ptnId = ptnId; }



}