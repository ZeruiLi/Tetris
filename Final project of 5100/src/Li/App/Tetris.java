package Li.App;

import Li.Block.Cell;
import Li.Block.Tetromino;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class Tetris extends JPanel {

    //正在下落的方块
    private Tetromino currentOne = Tetromino.randomOne();
    //将要下落的方块
    private Tetromino nextOne = Tetromino.randomOne();
    //游戏主区域
    private Cell[][] wall = new Cell[18][9];
    //声明单元格的值
    private static final int CELL_SIZE = 48;

    //游戏分数池
    int[] scores_pool = {0, 1, 2, 5, 10};
    //当前游戏的分数
    private int totalScore = 0;
    //当前消除的行数
    private int totalLine = 0;

    //游戏三种状态 游戏中、暂停、结束
    public static final int PLING = 0;
    public static final int STOP = 1;
    public static final int OVER = 2;
    //当前游戏状态值
    private int game_state;
    //显示游戏状态
    String[] show_state = {"P[pause]", "C[continue]", "S[replay]"};

    //载入方块图片
    public static BufferedImage I;
    public static BufferedImage J;
    public static BufferedImage L;
    public static BufferedImage O;
    public static BufferedImage S;
    public static BufferedImage T;
    public static BufferedImage Z;
    public static BufferedImage background;
    private static BufferedImage startImage;
    private static JButton startButton;
    private static JFrame frame;


    static {
        try {
            I = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/I.png"));
            J = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/J.png"));
            L = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/L.png"));
            O = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/O.png"));
            S = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/S.png"));
            T = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Tetris-master\\Tetris-master\\images/T.png"));
            Z = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/Z.png"));
            background = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/background.png"));
            startImage = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Final project of 5100\\images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void initStartScreen(JFrame frame) {
        try {
            startImage = ImageIO.read(new File("C:\\Users\\cris li\\IdeaProjects\\Tetris-master\\Tetris-master\\images/background.png"));
            JLabel backgroundLabel = new JLabel(new ImageIcon(startImage));
            backgroundLabel.setLayout(new GridBagLayout()); // 设置布局为 GridBagLayout

            startButton = new JButton("Start");
            startButton.setPreferredSize(new Dimension(200, 60)); // 设置按钮大小
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER; // 按钮在行的最后
            gbc.anchor = GridBagConstraints.CENTER; // 按钮居中

            // 为 "Start" 按钮添加事件监听器
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 移除开始界面的组件
                    frame.getContentPane().removeAll();
                    Tetris gamePanel = new Tetris();
                    frame.setContentPane(gamePanel); // 添加游戏面板
                    frame.revalidate();
                    frame.repaint();
                    gamePanel.start(); // 开始游戏逻辑
                    gamePanel.requestFocusInWindow(); // 请求键盘焦点
                }
            });

            backgroundLabel.add(startButton, gbc); // 将按钮添加到背景标签中
            frame.setContentPane(backgroundLabel); // 设置背景标签为内容面板
            frame.setSize(startImage.getWidth(), startImage.getHeight());
            frame.setLocationRelativeTo(null);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常
        }
    }



    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        //平移坐标轴
        g.translate(22, 15);
        //绘制游戏主区域
        paintWall(g);
        //绘制正在下落的四方格
        paintCurrentOne(g);
        //绘制下一个将要下落的四方格
        paintNextOne(g);
        //绘制游戏得分
        paintSource(g);
        //绘制当前游戏状态
        paintState(g);
    }
    public void start() {
        game_state = PLING;
        // 设置键盘监听器
        setKeyListener();

        // 启动游戏线程
        new Thread(this::gameLoop).start();
    }
    private void setKeyListener() {
        KeyListener l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_DOWN:
                        sortDropActive();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveleftActive();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRightActive();
                        break;
                    case KeyEvent.VK_UP:
                        rotateRightActive();
                        break;
                    case KeyEvent.VK_SPACE:
                        hadnDropActive();
                        break;
                    case KeyEvent.VK_P:
                        //判断当前游戏状态
                        if (game_state == PLING) {
                            game_state = STOP;
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (game_state == STOP) {
                            game_state = PLING;
                        }
                        break;
                    case KeyEvent.VK_S:
                        //重新开始
                        game_state = PLING;
                        wall = new Cell[18][9];
                        currentOne = Tetromino.randomOne();
                        nextOne = Tetromino.randomOne();
                        totalScore = 0;
                        totalLine = 0;
                        break;
                }
            }
        };
        //将窗口设置为焦点
        this.addKeyListener(l);
        this.requestFocus();
    }
    private void gameLoop() {
        while (game_state != OVER) {
            try {
                if (game_state == PLING) {
                    // Game logic
                    if (camDrop()) {
                        currentOne.moveDrop();
                    } else {
                        landToWall();
                        destroyLine();
                        if (isGameOver()) {
                            game_state = OVER;
                        } else {
                            currentOne = nextOne;
                            nextOne = Tetromino.randomOne();
                        }
                    }
                }
                Thread.sleep(500); // Game speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SwingUtilities.invokeLater(this::repaint);
            try {
                Thread.sleep(500); // 控制游戏速度
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //创建顺时针旋转
    public void rotateRightActive() {
        currentOne.rotateRight();
        if (outOFBounds() || coincide()) {
            currentOne.rotateLeft();
        }
    }

    //瞬间下落
    public void hadnDropActive() {
        while (true) {
            //判断能否下落
            if (camDrop()) {
                currentOne.moveDrop();
            } else {
                break;
            }
        }
        //嵌入到墙中
        landToWall();
        destroyLine();
        if (isGameOver()) {
            game_state = OVER;
        } else {
            //游戏没有结束
            currentOne = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    //按键一次，下落一格
    public void sortDropActive() {
        if (camDrop()) {
            //当前四方格下落一格
            currentOne.moveDrop();
        } else {
            landToWall();
            destroyLine();
            if (isGameOver()) {
                game_state = OVER;
            } else {
                //游戏没有结束
                currentOne = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    //单元格嵌入墙中
    private void landToWall() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    //判断四方格能否下落
    public boolean camDrop() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            //判断是否到达底部
            if (row == wall.length - 1) {
                return false;
            } else if (wall[row + 1][col] != null) {
                return false;
            }
        }
        return true;
    }

    //消除行
    public void destroyLine() {
        int line = 0;
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            if (isFullLine(row)) {
                line++;
                for (int i = row; i > 0; i--) {
                    System.arraycopy(wall[i - 1], 0, wall[i], 0, wall[0].length);
                }
                wall[0] = new Cell[9];
            }
        }
        //分数池获取分数，累加到总分
        totalScore += scores_pool[line];
        //总行数
        totalLine += line;
    }

    //判断当前行是否已经满了
    public boolean isFullLine(int row) {
        Cell[] cells = wall[row];
        for (Cell cell : cells) {
            if (cell == null) {
                return false;
            }
        }
        return true;
    }

    //判断游戏是否结束
    public boolean isGameOver() {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    private void paintState(Graphics g) {
        if (game_state == PLING) {
            g.drawString(show_state[PLING], 500, 660);
        } else if (game_state == STOP) {
            g.drawString(show_state[STOP], 500, 660);
        } else {
            g.drawString(show_state[OVER], 500, 660);
            g.setColor(Color.RED);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 60));
            g.drawString("GAME OVER!", 30, 400);
        }
    }

    private void paintSource(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        g.drawString("points: " + totalScore, 500, 250);
        g.drawString("lines: " + totalLine, 500, 430);
    }

    private void paintNextOne(Graphics g) {
        Cell[] cells = nextOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE + 370;
            int y = cell.getRow() * CELL_SIZE + 25;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintCurrentOne(Graphics g) {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(), x, y, null);
        }
    }

    private void paintWall(Graphics g) {
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                Cell cell = wall[i][j];
                //判断是否有小方块
                if (cell == null) {
                    g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawImage(cell.getImage(), x, y, null);
                }
            }
        }
    }

    //判断是否出界
    public boolean outOFBounds() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int col = cell.getCol();
            int row = cell.getRow();
            if (row < 0 || row > wall.length - 1 || col < 0 || col > wall[0].length-1) {
                return true;
            }
        }
        return false;
    }

    //按键一次，左移一次
    public void moveleftActive() {
        currentOne.moveLeft();
        //判断是否越界或重合
        if (outOFBounds() || coincide()) {
            currentOne.moveRight();
        }
    }

    //按键一次，右移一次
    public void moveRightActive() {
        currentOne.moveRight();
        //判断是否越界或重合
        if (outOFBounds() || coincide()) {
            currentOne.moveLeft();
        }
    }

    //判断是否重合
    public boolean coincide() {
        Cell[] cells = currentOne.cells;
        for (Cell cell : cells) {
            int row = cell.getRow();
            int col = cell.getCol();
            if (wall[row][col] != null) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        frame = new JFrame("Tetris");
        initStartScreen(frame); // Initialize and display the start screen

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); // Show the frame
    }

}
