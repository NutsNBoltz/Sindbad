import javax.swing.*;
import java.awt.*;

public class Player {
    private int x, y;
    private int speed = 12;
    private Image sprite;
    int offsetY;


    public Player(int x, int y) {
        this.x = x;
        this.y = y;

        //this will load up the still image for now
        sprite = new ImageIcon("src/assets/sprites/player/rustam.png").getImage();
        offsetY = sprite.getHeight(null) -32; // 32 is the tileheight

    }

    public void moveUp()    {
        if (y - speed >= 0) y -= speed;
    }
    public void moveDown(int mapHeight, int tileHeight)  {
        if (y + tileHeight + speed <= mapHeight) y += speed;
    }
    public void moveLeft()  {
        if (x - speed >= 0) x -= speed;
    }
    public void moveRight(int mapWidth, int tileWidth)  {
        if (x + tileWidth + speed <= mapWidth) x += speed;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void draw(Graphics g, int screenX, int screenY) {

        g.drawImage(sprite, screenX , screenY - offsetY, null);
    }
}
