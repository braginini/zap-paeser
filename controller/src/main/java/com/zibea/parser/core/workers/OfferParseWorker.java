package com.zibea.parser.core.workers;

import com.zibea.parser.core.utils.ZapOfferParser;
import com.zibea.parser.model.domain.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author: Mikhail Bragin
 */
public class OfferParseWorker extends Worker {

    private OfferArchiver offerArchiver;

    private static final int workersAmount = 3;

    public OfferParseWorker(OfferArchiver offerArchiver) {
        super(workersAmount, "offer-parse-worker");
        this.offerArchiver = offerArchiver;
    }

    public void start() {

        for (int i = 0; i < workersAmount; i++) {
            pool.submit(new ParseWorker(tasks) {

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
    }
}
