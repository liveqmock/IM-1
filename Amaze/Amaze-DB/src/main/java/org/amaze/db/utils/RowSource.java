package org.amaze.db.utils;

public interface RowSource<T>
{
    public boolean next();

    public T get();

    public void beforeFirst();
}
