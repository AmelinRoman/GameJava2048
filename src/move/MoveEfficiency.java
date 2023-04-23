package move;

// Класс для эффективного хода
public class MoveEfficiency implements Comparable<MoveEfficiency>{

    private int numberOfEmptyTiles;

    private int score;

    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() { return move; }

    // Сравниваем ходы
    @Override
    public int compareTo(MoveEfficiency o) {
        int result = numberOfEmptyTiles - o.numberOfEmptyTiles;
        if (result == 0) {
            return score - o.score;
        } else {
            return result;
        }
    }


}
