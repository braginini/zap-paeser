package com.zibea.parser.core.workers;

import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: Mikhail Bragin
 */
public class OfferArchiveSupport {

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    Lock w = readWriteLock.writeLock();
    Lock r = readWriteLock.readLock();

    public void doAdd(AddAction action) {

        try {
            r.lock();
            action.add();
        } finally {
            r.unlock();
        }
    }

    public void doFlush(InsertAction action) {

        try {
            w.lock();

            action.flush();
            action.clearBatch();
        } catch (SQLException e) {

            checkException(e);

            action.saveOneByOne();
            action.clearBatch();

        } finally {
            w.unlock();
        }

    }

    public void checkException(SQLException e) {
        if (!isIdConstraintViolation(e.getMessage())) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private boolean isIdConstraintViolation(String errMsg) {
        return errMsg.contains("Violation of PRIMARY KEY constraint");
    }

    public static interface InsertAction {

        void flush() throws SQLException;

        void saveOneByOne();

        void clearBatch();
    }

    public static interface AddAction {

        void add();
    }
}
