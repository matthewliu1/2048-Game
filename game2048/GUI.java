package game2048;

import ucb.gui2.TopLevel;
import ucb.gui2.LayoutSpec;

import java.util.Observable;
import java.util.Observer;

import java.util.concurrent.ArrayBlockingQueue;

import java.awt.event.KeyEvent;


class GUI extends TopLevel implements Observer {

    /** Minimum size of board in pixels. */
    private static final int MIN_SIZE = 500;

    /** A new window with given TITLE providing a view of MODEL. */
    GUI(String title, Model model) {
        super(title, true);
        addMenuButton("Game->New", this::newGame);
        addMenuButton("Game->Quit", this::quit);

        addLabel("", "Score", new LayoutSpec("y", 1));

        _model = model;
        _model.addObserver(this);

        _widget = new BoardWidget(model.size());
        add(_widget,
            new LayoutSpec("y", 0,
                           "height", "REMAINDER",
                           "width", "REMAINDER"));

        _widget.requestFocusInWindow();
        _widget.setKeyHandler("keypress", this::keyPressed);
        setPreferredFocus(_widget);
        setScore(0, 0);
    }


    public void quit(String dummy) {
        _pendingKeys.offer("Quit");
        _widget.requestFocusInWindow();
    }


    public void newGame(String dummy) {
        _pendingKeys.offer("New Game");
        _widget.requestFocusInWindow();
    }


    public void keyPressed(String unused, KeyEvent e) {
        _pendingKeys.offer(e.getKeyText(e.getKeyCode()));
    }


    String readKey() {
        try {
            return _pendingKeys.take();
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }


    public void setScore(int score, int maxScore) {
        setLabel("Score", String.format("Score: %6d / Max score: %6d",
                                        score, maxScore));
    }


    @Override
    public void update(Observable model, Object arg) {
        _widget.update(_model);
        setScore(_model.score(), _model.maxScore());
    }


    private BoardWidget _widget;

    private Model _model;

    /** Queue of pending key presses. */
    private ArrayBlockingQueue<String> _pendingKeys =
        new ArrayBlockingQueue<>(5);

}
