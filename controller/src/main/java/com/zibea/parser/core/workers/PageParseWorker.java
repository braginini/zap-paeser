package com.zibea.parser.core.workers;

import com.zibea.parser.core.task.Task;
import com.zibea.parser.core.utils.ZapOfferListingParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * @author: Mikhail Bragin
 */
public class PageParseWorker extends Worker {

    private OfferParseWorker offerParseWorker;

    private static final int workersAmount = 3;

    public PageParseWorker(OfferParseWorker offerParseWorker) {
        super(workersAmount, "page-parse-worker");
        this.offerParseWorker = offerParseWorker;
    }

    @Override
    public void start() {
        for (int i = 0; i < workersAmount; i++) {

            pool.submit(new ParseWorker(tasks) {

                @Override
                public void processTask() throws InterruptedException {
                    int attempt = 0;

                    while (attempt < 3) {

                        try {
                            attempt++;

                            //testUrl(this.currentTask.getUrl()); //already tested before in another thread
                            Document doc = Jsoup.connect(task.getUrl()).get(); //parseWithProxy();//
                            ZapOfferListingParser parser = new ZapOfferListingParser(doc, task, new HashSet<Long>()); //todo add ids
                            List<Task> preparedTasks = parser.parse();

                            for (Task task : preparedTasks) {
                                offerParseWorker.addTask(task);
                                tasksProduced.incrementAndGet();
                            }

                            return;

                        } catch (IOException e) {
                            Thread.sleep(1000);
                        }
                    }
                }
            });
        }
    }
}
