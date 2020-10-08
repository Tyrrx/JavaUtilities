package vega.timer;

/**
 * @author 1181219
 * Date: 06.10.2020, 20:05
 */

public interface ITicking {

    void OnStart(ITimerInterruptor timerInterruptor);

    void OnTick(long millisToEnd);

    void OnEnd(boolean interrupted);

}
