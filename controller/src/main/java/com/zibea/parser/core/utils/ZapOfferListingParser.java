package com.zibea.parser.core.utils;

import com.zibea.parser.core.task.Task;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: Mikhail Bragin
 */
public class ZapOfferListingParser extends HtmlDocumentParser<List<Task>> {

    Task currentTask;

    Set<Long> savedIds;

    public ZapOfferListingParser(Document doc, Task currentTask, Set<Long> savedIds) {
        super(doc);
        this.currentTask = currentTask;
        this.savedIds = savedIds;
    }

    @Override
    public List<Task> parse() {
        List<Task> preparedTasks = new ArrayList<>();
        Elements resultOffers = doc.getElementsByClass("result-ofertas");   //should be one here
        for (Element element : resultOffers) {
            for (Element offer : element.getElementsByClass("item")) {
                Element urlEl = offer.getElementsByTag("a").get(0);
                String url = urlEl.attr("href");

                //exclude already saved offers
                Long offerId = extractOfferId(url);
                if (!savedIds.contains(offerId)) {
                    //testUrl(url);
                    preparedTasks.add(new Task(
                            currentTask.getState(),
                            currentTask.getCity(),
                            currentTask.getApartment(),
                            currentTask.getTransaction(),
                            currentTask.getDistrict(), url));
                }
            }
        }

        return preparedTasks;
    }

}
