package com.zibea.parser.core.workers;

import com.zibea.parser.core.exception.PageNotFoundException;
import com.zibea.parser.core.task.Task;

import java.io.IOException;

/**
 * @author: Mikhail Bragin
 */
public class PageSearchWorker extends Worker {

    private static final String pageParam = "?pag=";

    private PageParseWorker pageParseWorker;

    private static final int workersAmount = 1;

    public PageSearchWorker(PageParseWorker pageParseWorker) {
        super(workersAmount, "page-search-worker");
        this.pageParseWorker = pageParseWorker;
    }

    public void start() {
        for (int i = 0; i < workersAmount; i++) {

            pool.submit(new ParseWorker(tasks) {

                @Override
                public void processTask() throws InterruptedException {

                    for (int i = 1; i <= Short.MAX_VALUE; i++) {

                        int attempt = 0;

                        while (attempt < 3) {

                            attempt++;
                            String newTaskUrl = task.getUrl() + pageParam + i;

                            try {
                                testUrl(newTaskUrl);

                                pageParseWorker.addTask(new Task(task.getState(),
                                        task.getCity(),
                                        task.getApartment(),
                                        task.getTransaction(),
                                        task.getDistrict(),
                                        newTaskUrl));
                                tasksProduced.incrementAndGet();

                                Thread.sleep(1000);
                                break;
                            } catch (PageNotFoundException e) {
                                return;  //if it was non existing page index, finish creating search page tasks
                            } catch (IOException e) {
                                Thread.sleep(2000);
                            }
                        }
                    }
                }
            });
        }
    }
}
