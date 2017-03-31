package com.jlabs.accidentector.Listeners;

/**
 * Created by hhsn8 on 3/31/2017.
 */

public class CustomSensorEvent
{
    public CustomSensorEvent(float[] pValues, long pTimeStamp)
    {
        values = new float[3];
        values[0] = pValues[0];
        values[1] = pValues[1];
        values[2] = pValues[2];

        timeStamp = pTimeStamp;
    }
    public float[] values;
    public long timeStamp;
}
