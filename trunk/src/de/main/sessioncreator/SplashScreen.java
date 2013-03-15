/*
 * Copyright 2011-2013 Sven Finsterwalder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.main.sessioncreator;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Sven Finsterwalder
 */
public class SplashScreen extends JWindow {

    private int duration;

    public SplashScreen(int d) {
        duration = d;
    }

    public void showSplash() {

        JPanel content = (JPanel)getContentPane();
        Color contentColor = new Color(64, 132, 242, 255);
        content.setBackground(contentColor);
        content.setOpaque(true);

        // Set the window's bounds, centering the window
        int width = 450;
        int height =120;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width-width)/2;
        int y = (screen.height-height)/2;
        setBounds(x,y,width,height);

        // Build the splash screen
        JLabel copyrt = new JLabel
                ("Welcome to the SessionCreator", JLabel.CENTER);
        
        copyrt.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        Color textColor = new Color(0, 8, 32, 255);
        copyrt.setForeground(textColor);
        content.add(copyrt, BorderLayout.CENTER);
        Color borderColor = new Color(255, 255, 255, 150);
        content.setBorder(BorderFactory.createLineBorder(borderColor, 4));

        // Display it
        setVisible(true);

        // Wait a little while, maybe while loading resources
        try { Thread.sleep(duration); } catch (Exception e) {}

        setVisible(false);

    }
}
