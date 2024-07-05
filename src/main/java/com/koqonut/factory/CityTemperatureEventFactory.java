package com.koqonut.factory;

import com.koqonut.model.CityTemperatureEvent;
import com.lmax.disruptor.EventFactory;

// Event factory to create CityTemperatureEvent instances
public class CityTemperatureEventFactory implements EventFactory<CityTemperatureEvent> {
    @Override
    public CityTemperatureEvent newInstance() {
        return new CityTemperatureEvent();
    }
}