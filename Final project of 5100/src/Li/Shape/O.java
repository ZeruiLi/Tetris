package Li.Shape;
import Li.App.Tetris;
import Li.Block.Cell;
import Li.Block.Tetromino;
public class O extends Tetromino {
    public O() {
        cells[0] = new Cell(0, 4, Tetris.O);
        cells[1] = new Cell(0, 5, Tetris.O);
        cells[2] = new Cell(1, 4, Tetris.O);
        cells[3] = new Cell(1, 5, Tetris.O);

        //无旋转状态
        states = new State[0];
    }
}

