package com.zibea.parser.core.workers;

import com.zibea.parser.dao.RealtyDao;
import com.zibea.parser.model.domain.Offer;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: Mikhail Bragin
 */
public class OfferArchiver implements Runnable {

    private Set<Offer> batch;
    private Set<Long> savedOffers;

    private AtomicLong offersSaved = new AtomicLong();

    private RealtyDao dao;

    private OfferArchiveSupport support;

    public OfferArchiver(RealtyDao dao) {
        this.dao = dao;
        this.support = new OfferArchiveSupport();
        batch = Collections.synchronizedSet(new HashSet<Offer>());
        savedOffers = Collections.synchronizedSet(new HashSet<Long>());
    }

    @Override
    public void run() {
        support.doFlush(new OfferArchiveSupport.InsertAction() {

            @Override
            public void flush() throws SQLException {

                if (!batch.isEmpty()) {

                    checkDuplicates();

                    savedOffers.addAll(dao.saveBatch(batch));
                    offersSaved.getAndSet(offersSaved.get() + batch.size());
                }
            }

            @Override
            public void saveOneByOne() throws SQLException {

                for (Offer offer : batch) {
                    dao.saveOffer(offer);
                    offersSaved.incrementAndGet();
                }
            }

            @Override
            public void clearBatch() {
                batch.clear();
            }

        });
    }

    public void addToBatch(final Offer offer) {

        support.doAdd(new OfferArchiveSupport.AddAction() {
            @Override
            public void add() {
                batch.add(offer);
            }
        });
    }

    private void checkDuplicates() {
        Iterator<Offer> it = batch.iterator();

        while (it.hasNext()) {
            Offer offer = it.next();
            if (savedOffers.contains(offer.getId()))
                it.remove();
        }
    }

    public AtomicLong getOffersSaved() {
        return offersSaved;
    }
}
