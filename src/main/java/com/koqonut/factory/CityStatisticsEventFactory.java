package com.koqonut.factory;

import com.koqonut.model.CityStatisticsEvent;
import com.lmax.disruptor.EventFactory;

// Event factory to create CityStatisticsEvent instances
public class CityStatisticsEventFactory implements EventFactory<CityStatisticsEvent> {
    @Override
    public CityStatisticsEvent newInstance() {
        return new CityStatisticsEvent();
    }
}