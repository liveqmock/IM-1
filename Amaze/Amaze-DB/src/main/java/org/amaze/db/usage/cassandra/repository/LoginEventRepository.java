package org.amaze.db.usage.cassandra.repository;

import org.amaze.db.usage.cassandra.repository.objects.LoginEvent;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("loginEventRepository")
public interface LoginEventRepository extends CrudRepository<LoginEvent, Long>{
	
}
