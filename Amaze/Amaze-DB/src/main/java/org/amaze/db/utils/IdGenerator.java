package org.amaze.db.utils;

import org.hibernate.HibernateException;

public interface IdGenerator
{
    public Long generate( Class objectKey ) throws HibernateException;
}