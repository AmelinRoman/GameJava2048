import tile.Tile;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Controller extends KeyAdapter { // Наследуемся от KeyAdapter для работы с клавиатурой
    private Model model;
    private View view;
    private static final int WINNING_TILE = 2048; // Номер победной плитки

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this); // Инициализируем View текущим контроллером
    }

    public Tile[][] getGameTiles() { return  model.getGameTiles(); } // Получаем игровое поле от модели
    public int getScore() { return model.score; } // Получаем от модели текущий счет
    public View getView() { return view; } // Получаем представление
    // Метод для загрузки игры сначала
    public void resetGame() {
        this.model.score = 0;
        // флаги определяющие победил игрок или проиграл
        view.isLoseGame = false;
        view.isWinnerGame = false;
        // обновляем поле игры через модель
        model.resetGame();
    }

    // Переопределяем метод KeyAdapter для обработки нажатий  на клавиатуру
    @Override
    public void keyPressed(KeyEvent e) {
        // Если пользователь нажал ESC, перезагружаем игру
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        }
        if (!model.canMove()) { // Если не можем сделать ход
            view.isLoseGame = true; // Выставляем флаг проиграл
        }
        // Проверяем что мы не победили и не проиграли и обрабатываем нажатия на клавиатуру определенными методами
        if (!view.isLoseGame && !view.isWinnerGame) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> model.right();
                case KeyEvent.VK_LEFT -> model.left();
                case KeyEvent.VK_UP -> model.up();
                case KeyEvent.VK_DOWN -> model.down();
                case KeyEvent.VK_Z -> model.rollback();
                case KeyEvent.VK_R -> model.randomMove();
                case KeyEvent.VK_A -> model.autoMove();
            }
        }
        // Проверяем если максимальная плитка равна 2048, если равна выставляем флаг победителя
        if (model.maxTile == WINNING_TILE) {
            view.isWinnerGame = true;
        }
        view.repaint(); // Обновляем
    }
}
