package game2048;

import static game2048.Side.*;


public class Game {


    public Game(Model model, InputSource source) {
        _model = model;
        _source = source;
        _playing = true;
    }

    /** Return true iff we have not received a Quit command. */
    boolean playing() {
        return _playing;
    }

 
    void playGame() {
        _model.clear();
        _model.addTile(getValidNewTile());
        while (_playing) {
            if (!_model.gameOver()) {
                _model.addTile(getValidNewTile());
                _model.notifyObservers();
            }

            boolean moved;
            moved = false;
            while (!moved) {
                String cmnd = _source.getKey();
                switch (cmnd) {
                    case "Quit":
                        _playing = false;
                        return;
                    case "New Game":
                        return;
                    case "Up": case "Down": case "Left": case "Right":
                    case "\u2190": case "\u2191": case "\u2192": case "\u2193":
                        if (!_model.gameOver() && _model.tilt(keyToSide(cmnd))) {
                            _model.notifyObservers(cmnd);
                            moved = true;
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }


    private Side keyToSide(String key) {
        switch (key) {
            case "Up": case "\u2191":
                return NORTH;
            case "Down": case "\u2193":
                return SOUTH;
            case "Left": case "\u2190":
                return WEST;
            case "Right": case "\u2192":
                return EAST;
            default:
                throw new IllegalArgumentException("unknown key designation");
        }
    }


    private Tile getValidNewTile() {
        while (true) {
            Tile tile = _source.getNewTile(_model.size());
            if (_model.tile(tile.col(), tile.row()) == null) {
                return tile;
            }
        }
    }

    /** The playing board. */
    private Model _model;

    /** Input source from standard input. */
    private InputSource _source;

    /** True while user is still willing to play. */
    private boolean _playing;

}
