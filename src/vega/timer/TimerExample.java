package vega.timer;

/**
 * @author 1181219
 * Date: 06.10.2020, 20:58
 */

public class TimerExample implements ITicking {

    private ITimerInterruptor timerInterruptor;

    @Override
    public void OnStart(ITimerInterruptor timerInterruptor) {
        this.timerInterruptor = timerInterruptor;
        System.out.println("start");
    }

    @Override
    public void OnTick(long millisToEnd) {
        System.out.println(millisToEnd);
        if (millisToEnd == 4000) {
            this.timerInterruptor.stop();
        }
    }

    @Override
    public void OnEnd(boolean interrupted) {
        System.out.println(interrupted);
    }

    public static void main(String[] args) {
        new Timer(new TimerExample()).startSeconds(5);
    }
}
