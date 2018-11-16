package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;

import java.util.TimerTask;

public class GameUpdateTask extends TimerTask {

    private GameView _view;

    public GameUpdateTask(GameView view)
    {
        _view = view;
    }

    public void run() {
        _view.post(new Runnable() {
            @Override
            public void run() {
                if(_view.Lerp < _view.UpdateDelay) {
                    _view.Lerp++;
                    _view.Redraw();
                }
                else {
                    _view.Lerp = 0;
                    _view.Update();
                }
            }
        });
    }
}
