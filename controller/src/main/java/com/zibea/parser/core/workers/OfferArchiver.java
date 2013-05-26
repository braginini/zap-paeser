package com.zibea.parser.core.workers;

import com.zibea.parser.dao.RealtyDao;
import com.zibea.parser.model.domain.Offer;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: Mikhail Bragin
 */
public class OfferArchiver implements Runnable {

    private Set<Offer> batch;
    private Set<Long> savedOffers;

    private RealtyDao dao;

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    Lock r = readWriteLock.readLock();
    Lock w = readWriteLock.writeLock();


    public OfferArchiver(RealtyDao dao) {
        this.dao = dao;
        batch = Collections.synchronizedSet(new HashSet<Offer>());
        savedOffers = Collections.synchronizedSet(new HashSet<Long>());
    }

    @Override
    public void run() {
        try {
            w.lock();
            savedOffers.addAll(dao.saveBatch(batch));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            w.unlock();
        }
    }

    public void addToBatch(Offer offer) {
        try {
            r.lock();
            batch.add(offer);
        } finally {
            r.unlock();
        }
    }
}
