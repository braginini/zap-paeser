package com.zibea.parser.core.task;

import com.zibea.parser.model.domain.Offer;

/**
 * @author: Mikhail Bragin
 */
public class OfferTask {

    private Offer offer;

    public OfferTask(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}
