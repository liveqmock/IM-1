package org.amaze.db.usage.cassandra.repository;

import java.util.List;

import org.amaze.db.usage.cassandra.repository.objects.LoginEvent;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("loginEventRepository")
public interface LoginEventRepository extends CrudRepository<LoginEvent, Long>
{

	@Query("FROM LoginEvent")
    List<LoginEvent> findAllLoginEvent();
	
//	@Query( "select o from LoginEvent o where inventoryId in :ids" )
//	void findByInventoryIds(@Param("ids") List<Long> inventoryIdList);
	
	
}
