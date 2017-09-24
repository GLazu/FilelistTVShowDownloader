import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class TrayIconDemo {
    public void displayTray(String notifTitle, String notifMsg) throws AWTException, java.net.MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("C:\\Program Files (x86)\\Deluge\\deluge-1.3.15-py2.7.egg\\deluge\\data\\pixmaps\\deluge.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getToolkit().createImage(getClass().getResource("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, notifTitle);
        //Let the system resizes the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        tray.add(trayIcon);
        trayIcon.displayMessage(notifTitle, notifMsg, MessageType.INFO);
        tray.remove(trayIcon);
    }
}