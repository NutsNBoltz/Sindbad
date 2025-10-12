import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class GameMap extends JPanel implements KeyListener {
    private Image tileImage;
    private int[][] mapData;
    private final int tileWidth = 32;
    private final int tileHeight = 32;
    private final Player player;

    private int cameraX;
    private int cameraY;

    private int mapWidth;
    private int mapHeight;

    public GameMap() {
        setPreferredSize(new Dimension(1920, 1080));
        setFocusable(true);
        addKeyListener(this);


        int startX = (getPreferredSize().width) / 2;
        int startY = (getPreferredSize().height) / 2;
        player = new Player(startX, startY);

        try {
            // Load your tileset image
            tileImage = new ImageIcon("src/assets/maps/tileset.png").getImage();

            // Load your TMX map
            File file = new File("src/assets/maps/sindbad_map.tmx");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            Element map = doc.getDocumentElement();
            int width = Integer.parseInt(map.getAttribute("width"));
            int height = Integer.parseInt(map.getAttribute("height"));
            mapData = new int[height][width];

            mapWidth = width * tileWidth;
            mapHeight = height * tileHeight;

            // Extract layer data
            NodeList layers = doc.getElementsByTagName("layer");
            if (layers.getLength() > 0) {
                Element layer = (Element) layers.item(0);
                String[] data = layer.getElementsByTagName("data").item(0).getTextContent().trim().split(",");

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        mapData[y][x] = Integer.parseInt(data[y * width + x].trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapData == null || tileImage == null) return;

        int tilesPerRow = tileImage.getWidth(null) / tileWidth;

        // === 🧭 CAMERA FOLLOW BOX LOGIC ===
        int deadZoneWidth = 6 * tileWidth;   // 6 tiles wide
        int deadZoneHeight = 6 * tileHeight; // 6 tiles tall

        // The dead zone is centered in the screen
        int leftBound = cameraX + (getWidth() - deadZoneWidth) / 2;
        int rightBound = leftBound + deadZoneWidth;
        int topBound = cameraY + (getHeight() - deadZoneHeight) / 2;
        int bottomBound = topBound + deadZoneHeight;

        // Adjust camera if player leaves the dead zone
        if (player.getX() < leftBound)
            cameraX -= (leftBound - player.getX());
        else if (player.getX() > rightBound)
            cameraX += (player.getX() - rightBound);

        if (player.getY() < topBound)
            cameraY -= (topBound - player.getY());
        else if (player.getY() > bottomBound)
            cameraY += (player.getY() - bottomBound);

        // Clamp camera so it doesn't go outside map
        cameraX = Math.max(0, Math.min(cameraX, mapData[0].length * tileWidth - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, mapData.length * tileHeight - getHeight()));

        // === 🧱 DRAW MAP ===
        for (int y = 0; y < mapData.length; y++) {
            for (int x = 0; x < mapData[y].length; x++) {
                int gid = mapData[y][x];
                if (gid == 0) continue;

                int tileX = ((gid - 1) % tilesPerRow) * tileWidth;
                int tileY = ((gid - 1) / tilesPerRow) * tileHeight;

                int drawX = x * tileWidth - cameraX;
                int drawY = y * tileHeight - cameraY;

                // Skip tiles outside view (optimization)
                if (drawX + tileWidth < 0 || drawY + tileHeight < 0 ||
                        drawX > getWidth() || drawY > getHeight()) continue;

                g.drawImage(tileImage,
                        drawX, drawY,
                        drawX + tileWidth,
                        drawY + tileHeight,
                        tileX, tileY,
                        tileX + tileWidth,
                        tileY + tileHeight,
                        null);
            }
        }

        // === 🧍‍♂️ DRAW PLAYER ===
        // Player's position relative to camera
        int playerScreenX = player.getX() - cameraX;
        int playerScreenY = player.getY() - cameraY;
        player.draw(g, playerScreenX, playerScreenY);

        // (Optional) draw the dead-zone rectangle for debugging
    /*
    g.setColor(new Color(255, 0, 0, 100));
    g.drawRect(
        (getWidth() - deadZoneWidth) / 2,
        (getHeight() - deadZoneHeight) / 2,
        deadZoneWidth,
        deadZoneHeight
    );
    */
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> player.moveUp();
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> player.moveDown(mapHeight, tileHeight);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> player.moveLeft();
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> player.moveRight(mapWidth, tileWidth);
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
