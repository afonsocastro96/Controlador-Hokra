/**
 * Created by afonso on 02-06-2015.
 */
public class Packet {
    boolean actionButton;
    int xAxis;
    int yAxis;

    public Packet() {
        actionButton = false;
        xAxis = 128;
        yAxis = 128;
    }

    public Packet(boolean actionButton, int xAxis, int yAxis) {
        this.actionButton = actionButton;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public boolean isActionButtonPressed() {
        return actionButton;
    }

    public void setActionButton(boolean actionButton) {
        this.actionButton = actionButton;
    }

    public int getxAxis() {
        return xAxis;
    }

    public void setxAxis(int xAxis) {
        this.xAxis = xAxis;
    }

    public int getyAxis() {
        return yAxis;
    }

    public void setyAxis(int yAxis) {
        this.yAxis = yAxis;
    }
}
