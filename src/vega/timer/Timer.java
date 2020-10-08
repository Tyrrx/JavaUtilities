package vega.timer;

/**
 * @author 1181219
 * Date: 06.10.2020, 20:03
 */

public class Timer implements Runnable {

    private Thread timerThread;
    private long timerIterations;
    private long timerDelay;
    private ITicking timerTicking;
    private TimerState currentTimerState;
    private ITimerInterruptor timerInterruptor;

    public Timer(ITicking timerTicking) {
        this.timerTicking = timerTicking;
        this.timerThread = new Thread(this);
        this.timerInterruptor = new TimerInterruptor(this.timerThread);
        this.currentTimerState = TimerState.Ready;
    }

    public ITimerInterruptor startSeconds(long seconds) {
        return this.start(1000, seconds);
    }

    public ITimerInterruptor start(long iterationDelay, long iterationCount) {
        this.timerDelay = iterationDelay;
        this.timerIterations = iterationCount;
        this.currentTimerState = TimerState.Running;
        this.timerThread.start();
        return this.timerInterruptor;
    }

    public boolean isReady() {
        return this.currentTimerState == TimerState.Ready;
    }

    public boolean isRunning() {
        return this.currentTimerState == TimerState.Running;
    }

    public TimerState getCurrentTimerState() {
        return currentTimerState;
    }

    public Thread getTimerThread() {
        return timerThread;
    }

    @Override
    public void run() {
        try {
            timerTicking.OnStart(this.timerInterruptor);
            for (int iterations = 0; iterations < timerIterations; iterations++) {
                timerTicking.OnTick((this.timerIterations * this.timerDelay) - (iterations * this.timerDelay));
                Thread.yield();
                Thread.sleep(this.timerDelay);
            }
        } catch (InterruptedException e) {
            this.currentTimerState = TimerState.Interrupted;
            timerTicking.OnEnd(true);
        } finally {
            if (this.currentTimerState != TimerState.Interrupted) {
                timerTicking.OnEnd(false);
            }
            this.currentTimerState = TimerState.Ready;
        }
    }

    public static class TimerInterruptor implements ITimerInterruptor {

        private Thread timerThread;

        public TimerInterruptor(Thread timerThread) {
            this.timerThread = timerThread;
        }

        public void stop() {
            this.timerThread.interrupt();
        }
    }

    public static enum TimerState {
        Ready,
        Running,
        Interrupted,
    }
}
