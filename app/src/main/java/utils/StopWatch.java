package utils;

import java.io.Serializable;

public class StopWatch implements Serializable {

    private static final long serialVersionUID = 2701771838184363414L;
    private long startTime = 0;
    private long stopTime = 0;

    private long pauseStart = 0;
    private long pauseStop = 0;
    private long pausedTime = 0;
    private boolean running = false;


    public void start() {
        if(!running) {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }
    }

    public void stop() {
        if(running) {
            this.stopTime = System.currentTimeMillis();
            this.running = false;
        }
    }

    public void pause()
    {
        this.pauseStart = System.currentTimeMillis();
        this.pauseStop = 0;
        this.running = false;
    }

    public void resume() {
        this.pauseStop = System.currentTimeMillis();
        this.pausedTime += (this.pauseStop - this.pauseStart);
        this.running = true;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }


    //elapsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        long currentMillis = System.currentTimeMillis();

        if (running) {
            elapsed = (currentMillis - startTime);

            // Če nismo nadeljevali s pavzo, odštejemo trenutni čas in čas začetka pavze
            if(this.pauseStart > 0 && this.pauseStop == 0)
                elapsed -= (currentMillis - this.pauseStart);
        }
        else {
            // Če nismo nadeljevali s pavzo, je čas začetka pavze končni čas
            if(this.pauseStart > 0 && this.pauseStop == 0)
                this.stopTime = this.pauseStart;

            elapsed = (stopTime - startTime);
        }

        elapsed -= pausedTime;

        return elapsed;
    }


    //elapsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        long currentMillis = System.currentTimeMillis();

        if (running) {
            elapsed = ((currentMillis - startTime) / 1000);

            // Če nismo nadeljevali s pavzo, odštejemo trenutni čas in čas začetka pavze
            if(this.pauseStart > 0 && this.pauseStop == 0)
                elapsed -= ((currentMillis - this.pauseStart) / 1000);
        }
        else {
            // Če nismo nadeljevali s pavzo, je čas začetka pavze končni čas
            if(this.pauseStart > 0 && this.pauseStop == 0)
                this.stopTime = this.pauseStart;

            elapsed = ((stopTime - startTime) / 1000);
        }

        elapsed -= pausedTime / 1000;

        return elapsed;
    }

    public boolean isRunning() {
        return running;
    }

}

