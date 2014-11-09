package org.amaze.db.hibernate.utils;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

public class HibernateScrollableResults {
	private Session session;
	private ScrollableResults scrollableResults;

	private int currentRow = 0;

	HibernateScrollableResults(Session session,
			ScrollableResults scrollableResults) {
		this.session = session;
		this.scrollableResults = scrollableResults;
	}

	public boolean next() throws HibernateException {
		return scrollableResults.next();
	}

	public Object[] get() throws HibernateException {
		currentRow++;

		if (currentRow % 1000 == 0) {
			session.clear();
		}

		return scrollableResults.get();
	}

	public ScrollableResults getScrollableResults() {
		return scrollableResults;
	}

	public void close() throws HibernateException {
		session.close();
	}
}
