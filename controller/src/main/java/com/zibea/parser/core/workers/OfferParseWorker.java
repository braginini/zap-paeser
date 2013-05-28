package com.zibea.parser.core.workers;

import com.zibea.parser.core.task.Task;
import com.zibea.parser.core.utils.ZapOfferParser;
import com.zibea.parser.model.domain.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Mikhail Bragin
 */
public class OfferParseWorker implements Worker {

    private ExecutorService offerParsePool;

    private OfferArchiver offerArchiver;

    private LinkedBlockingQueue<Task> tasks;

    private AtomicLong tasksProduced = new AtomicLong();

    public OfferParseWorker(OfferArchiver offerArchiver) {
        this.offerParsePool = Executors.newFixedThreadPool(1, new CustomThreadFactory("offer-parse-worker"));
        this.offerArchiver = offerArchiver;
        this.tasks = new LinkedBlockingQueue<>();

        /*for (int i = 0; i < 1; i++)*/
            start();
    }

    @Override
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void start() {

        offerParsePool.submit(new NewParseWorker(tasks) {

            @Override
            public void processTask() throws InterruptedException {
                int attempt = 0;

                while (attempt < 3) {

                    try {
                        attempt++;

                        Document doc = Jsoup.connect(task.getUrl()).timeout(20 * 1000).get();
                        ZapOfferParser parser = new ZapOfferParser(doc);
                        Offer offer = parser.parse();

                        offer.setState(task.getState());
                        offer.setCity(task.getCity());
                        offer.setDistrict(task.getDistrict());
                        offer.setApartment(task.getApartment());
                        offer.setTransaction(task.getTransaction());

                        offerArchiver.addToBatch(offer); //put task to archiving queue
                        tasksProduced.incrementAndGet();
                        return;
                    } catch (IOException e) {
                        Thread.sleep(2000);
                    }
                }
            }
        });
    }

    @Override
    public long getTasksProduced() {
        return tasksProduced.get();
    }

    public int getQueueSize() {
        return tasks.size();
    }
}
