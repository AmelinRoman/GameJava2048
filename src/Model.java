import move.Move;
import move.MoveEfficiency;
import tile.Tile;

import java.util.*;

public class Model {
    // Ширина поля
    private static final int FIELD_WIDTH = 4;
    // Игровое поле
    private Tile[][] gameTiles;
    // Текущий счет в игру
    protected int score;
    // Флаг, что можно сохранять состояние игры
    private boolean isSaveNeeded = true;
    // Значение максимальной плитки
    protected int maxTile;
    // ArrayDeque для сохранения состояния игры
    private ArrayDeque<Tile[][]> previousStates = new ArrayDeque<>();
    // Для сохранения счета
    private ArrayDeque<Integer> previousScores = new ArrayDeque<>();

    public Model() {
        resetGame();
        this.score = 0;
        this.maxTile = 0;
    }

    public Tile[][] getGameTiles() { return gameTiles; }

    // Метод для получения пустых клеток
    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        // Проходим по каждой клетке игрового поля
        for (Tile[] gameTile : gameTiles) {
            for (Tile tile : gameTile) {
                // Проверяем его значение value - проверка на пустоту
                if (tile.isEmpty()) {
                    emptyTiles.add(tile); // добавляем в List пустых клеток
                }
            }
        }
        return emptyTiles;
    }
    // Метод создания поля, а так же для начала игры сначала
    public void resetGame() {
        this.gameTiles = new Tile[FIELD_WIDTH] [FIELD_WIDTH]; // Создаем поле Tile размерность 4х4
        // Проходим по каждой клетке
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameTiles[i][j] = new Tile(); // Создаем в каждой клетке Tile с 0 значением
            }
        }
        // Создаем 3 плитки в случайном месте со случайным значением
        addTile();
        addTile();
        addTile();
    }
    // Метод добавление плиток на игровое поле
    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles(); // получаем пустые клетки
        if (!emptyTiles.isEmpty()) { // проверяем есть ли в List пустые клетки
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size(); // получаем случайный индекс пустой клетки в пределах размера List
            Tile emptyTile = emptyTiles.get(index); // Берем пустую клетку;
            emptyTile.value = Math.random() < 0.9 ? 2 : 4; // Присваиваем клетке случайную плитку 2 или 4
        }
    }

// Метод компресса плиток
    private boolean compressTiles(Tile[] tiles) {
        int insertPosition = 0; // Создаем переменную для вставления позиции
        boolean result = false; // изначально флаг ложный

        for (int i = 0; i < FIELD_WIDTH; i++) { // Проходимся по массиву
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) { // проверяем что данная ячейка не пуста и что индекс плитки не совпадает с номером для вставления плитку
                    tiles[insertPosition] = tiles[i]; // Смещаем плиты на заданную позицию insertPosition
                    tiles[i] = new Tile(); // На старом месте создаем плитку с 0 значением
                    result = true; // выставляем флаг что можем сместить плиты
                     // увеличиваем позицию для вставки плитки
                }
                insertPosition++;
            }
        }
        return result; // возвращаем значение флага, смогли ли сместить плиты
    }
// Метод слияния плиток
    private boolean mergeTiles(Tile[] tiles) {
        boolean result = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) { // Проходим в цикле по tiles
            if (tiles[i].isEmpty()) { // Если ячейка пуста идем на следующую итерацию
                continue;
            }

            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) { // проверяем не вышли ли мы за рамки массива и равны ли значения плиток
                int updatedValue = tiles[i].value * 2; // получаем значения соединенных плиток
                if (updatedValue > maxTile) { // проверяем максимальная ли это плитка
                    maxTile = updatedValue; // обновляем значение
                }
                score += updatedValue; // Добавляем очки к текущему рекорду
                tilesList.addLast(new Tile(updatedValue)); // Добавляем плитку с новым значением в лист
                tiles[i + 1].value = 0; // Обнуляем значение плитки
                result = true; // выставляем флаг
            } else { // иначе просто добавляем новую плитку лист
                tilesList.addLast(new Tile(tiles[i].value));
            }
            tiles[i].value = 0; // обнуляем значение плитки
        }

        for (int i = 0; i < tilesList.size(); i++) { // проходимся по LinkedList и tiles
            tiles[i] = tilesList.get(i); // устанавливаем новое значение
        }
        return result; // возвращаем то что мы можем произвести слияние
    }
    // Метод вращения поля на 90 градусов по часовой стрелке
    private Tile[][] rotateClockwise(Tile[][] tiles) {
        final int LENGTH = tiles.length; // Создаем константу длинной в массив tiles
        Tile[][] resultTiles = new Tile[LENGTH][LENGTH]; // Создаем новый массив который будет хранить результат
        for (int i = 0; i < LENGTH; i++) { // Идем по длине массива
            for (int j = 0; j < LENGTH; j++) {
                resultTiles[j][LENGTH - 1 - i] = tiles[i][j]; // делаем разворот
            }
        }
        return resultTiles; // Возвращаем результат поворота
    }
    // Метод движения вправо
    public void left() {
        boolean flag = false; // флаг для проверки смогли ли мы сходить направо
        if (isSaveNeeded) { saveState(gameTiles); } // Если можно сохранить, сохраняем
        for (int i = 0; i < FIELD_WIDTH; i++) { // проходимся по константной длине массива
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) { // проверяем можем ли мы сделать одно из действий, сначала идет проверки слияния, а потом уже компресса
                flag = true; // Если смогли выставляем флаг
            }
        }
        if (flag) { // проверка флага
            addTile(); // добавление одной новой плитки
        }
        isSaveNeeded = true;
    }
    // Метод движения налево
    public void right() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }
    // Метод движения вверх
    public void up() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
    }
    // Метод движения вниз
    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }
    // Проверка есть ли еще свободные клетки на поле
    private boolean isFull() {
        return getEmptyTiles().size() > 0;
    }
    // Метод для проверки не проиграл ли игрок
    public boolean canMove() {
        if (isFull()) { // Проверяем есть ли еще пустые плиты
            return true;
        }
        // Проходим по игровому полю и проверяем можем ли мы сделать ход, что бы сложить плиты
        for (int indexX = 0; indexX < FIELD_WIDTH; indexX++) {
            for (int indexY = 0; indexY < FIELD_WIDTH; indexY++) {
                Tile tileElement = gameTiles[indexX][indexY];
                if ((indexX < FIELD_WIDTH - 1 && tileElement.value == gameTiles[indexX+1][indexY].value)
                    || ((indexY < FIELD_WIDTH - 1) && tileElement.value == gameTiles[indexX][indexY + 1].value)) {
                    return true; // если можем возвращаем что можем еще играть
                }
            }
        }
        return false; // иначе проиграл
    }
    // Метод сохранения игры
    public void saveState(Tile[][] tiles) {
        Tile[][] saveTiles = new Tile[FIELD_WIDTH] [FIELD_WIDTH]; // Создаем новый массив

        for (int i = 0; i < tiles.length; i++) { // Проходимся по переданному массиву
            for (int j = 0; j < tiles[i].length; j++) {
                saveTiles[i][j] = new Tile(tiles[i][j].value); // передаем значение плитки в новый массив, сохраняем состояние
            }
        }
        previousStates.push(saveTiles);
        previousScores.push(score);
        isSaveNeeded = false; // Выставляем флаг, что сохранять состояние не надо
    }
    // Метод возврата хода назад
    public void rollback() {
        // Проверяем ArrayDeque не пустые
        if (!previousScores.isEmpty() && !previousStates.isEmpty()) {
            // возвращаем поле и счет в состояние назад
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }
    // Метод случайного хода
    public void randomMove() {
        int randomMove = (int) (Math.random() * 100) % 4; // получаем случайное число от 0 до 4
        switch (randomMove) { // Делаем ход в зависимости от случайного числа
            case 0 -> left();
            case 1 -> right();
            case 2 -> up();
            case 3 -> down();
        }
    }
    // Метод проверки изменилось ли поле
    public boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousStates.peek()[i][j].value) {
                    return true;
                }
            }

        }
        return false;
    }
    // Получаем эффективный ход
    public MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveEfficiency;
    }
    // Метод умного хода
    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));

        priorityQueue.peek().getMove().move();
    }


}
