package com.zibea.parser.core.workers;

/**
 * @author: Mikhail Bragin
 */
public class MonitorThread implements Runnable {

    PageSearchWorker searchPageWorker;
    PageParseWorker pageParseWorker;
    OfferParseWorker offerParseWorker;
    OfferArchiver offerArchiver;

    public MonitorThread(PageSearchWorker searchPageWorker,
                         PageParseWorker pageParseWorker,
                         OfferParseWorker offerParseWorker,
                         OfferArchiver offerArchiver) {
        this.searchPageWorker = searchPageWorker;
        this.pageParseWorker = pageParseWorker;
        this.offerParseWorker = offerParseWorker;
        this.offerArchiver = offerArchiver;
    }

    @Override
    public void run() {
        System.out.println(searchPageWorker.getClass().getSimpleName() + " done=" + searchPageWorker.getTasksProduced());
        System.out.println(pageParseWorker.getClass().getSimpleName() + " done=" + pageParseWorker.getTasksProduced());
        System.out.println(offerParseWorker.getClass().getSimpleName() + " done=" + offerParseWorker.getTasksProduced());
        System.out.println(offerArchiver.getClass().getSimpleName() + " done=" + offerArchiver.getOffersSaved());
    }
}
