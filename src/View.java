import tile.Tile;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {

    private static final Color BG_COLOR = new Color(0xbbada0); // Background Color
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 96;
    private static final int TILE_MARGIN = 12;
    private Controller controller;

    protected boolean isLoseGame = false; // Флаг проигравшего
    protected boolean isWinnerGame = false; // Флаг победителя

    // Инициализируем View и делаем Controller слушателем нажатий на клавиатуру
    public View(Controller controller) {
        setFocusable(true);
        this.controller = controller;
        addKeyListener(controller);
    }
    // Переопределяем метод для отрисовки
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0,0, this.getSize().width, this.getSize().height);
        // Передаем Tile для отрисовки
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                drawTile(g, controller.getGameTiles()[y][x], x, y);
            }
        }
        // Отображаем счет во время игры
        g.drawString("Score: " + controller.getScore(), 140, 465);
        // Выводим сообщение в зависимости от того победил игрок или проиграл
        if (isWinnerGame) {
            JOptionPane.showMessageDialog(this, "!!! You winner !!!");
        } else if (isLoseGame) {
            JOptionPane.showMessageDialog(this, "You is a loser");
        }
    }
    // Метод отрисовки Tile
    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D graphics2D = (Graphics2D) g2;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int value = tile.value;
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y);
        graphics2D.setColor(tile.getTileColor());
        graphics2D.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 8, 8);
        graphics2D.setColor(tile.getFontColor());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.BOLD, size);
        graphics2D.setFont(font);

        String valueString = String.valueOf(value);
        final  FontMetrics fm = getFontMetrics(font);

        final int width = fm.stringWidth(valueString);
        final int height = -(int) fm.getLineMetrics(valueString, graphics2D).getBaselineOffsets()[2];

        if (value != 0) {
            graphics2D.drawString(
                    valueString,
                    xOffset+(TILE_SIZE - width) / 2,
                    yOffset + TILE_SIZE - (TILE_SIZE - height) / 2 - 2);
        }
    }

    private static int offsetCoors(int arg) { // Метод для получения размера
        return arg * (TILE_MARGIN + TILE_SIZE) + TILE_MARGIN;
    }
}
