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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import de.main.sessioncreator.DesktopApplication1View.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Sven Finsterwalder
 */
public class SwingHelper {

    FileHelper fileHelper = new FileHelper();

    public void setTab1EnableAt(JTabbedPane tab, int index) {
        int maxtab = tab.getTabCount();

        for (int i = 0; i < maxtab; i++) {
            if (i == index) {
                tab.setEnabledAt(index, true);
            } else {
                tab.setEnabledAt(i, false);
            }
        }

    }

    public void setTesterCombox(JComboBox jcb) {
        jcb.removeAllItems();
        ArrayList<String> Tester = new ArrayList<String>();
        Tester = fileHelper.getTester();
        for (String t : Tester) {
            jcb.addItem(t);
        }
    }

    public void setConfigPaths(JTextField jtf) {
        jtf.setText("");
        ArrayList<String> Path = new ArrayList<String>();
        Path = fileHelper.getConfigPath();
        for (String p : Path) {
            if (jtf.getName().toLowerCase().contains("todo") && p.toLowerCase().contains("todo")) {
                jtf.setText(p.substring(p.indexOf(":") + 1));
                break;
            } else if (jtf.getName().toLowerCase().contains("submitted") && p.toLowerCase().contains("submitted")) {
                jtf.setText(p.substring(p.indexOf(":") + 1));
                break;
            } else if (jtf.getName().toLowerCase().contains("coverage") && p.toLowerCase().contains("coverage")) {
                jtf.setText(p.substring(p.indexOf(":") + 1));
                break;
            }
        }
    }

    public void getAreastoPopupMenu(JPopupMenu jpm, File f, alBugsIssue albi) {

        fileHelper.getAreas(f);
        Iterator<Map.Entry<String, List>> it = fileHelper.areaMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry en = it.next();
            JMenu submenu = new JMenu(en.getKey().toString().substring(3));
//            submenu.getPopupMenu().setLayout(new GridLayout(10,0));

            for (Object o : fileHelper.areaMap.get(en.getKey().toString())) {
                JMenuItem mi = new JMenuItem(o.toString());
                mi.addActionListener(albi);
                submenu.add(mi);
                jpm.add(submenu);
            }
            MenuScroller.setScrollerFor(submenu);
        }
    }

    public boolean saveTodoSession(String Charter, String Areas,String todopath) {
        Object[] options = {"Save", "Cancel"};
        JTextField FileName = new JTextField();
        FileName.setText("et-todo-");
        FileName.setToolTipText("Please enter the name of session with fileextension (e.g. '.ses'");
        JLabel charter = new JLabel("Charter:");
        charter.setFont(charter.getFont().deriveFont(Font.BOLD));
        JLabel areas = new JLabel("The new file will contain your choosen Areas!");
        areas.setFont(areas.getFont().deriveFont(Font.BOLD));
        areas.setForeground(Color.red);
        JLabel filenamelbl = new JLabel("Filename (with fileextension '.ses'):");
        filenamelbl.setFont(filenamelbl.getFont().deriveFont(Font.BOLD));
        final JComponent[] inputs = new JComponent[]{
            charter,
            new JLabel(Charter),
            areas,
            filenamelbl,
            FileName,};
        int output = JOptionPane.showOptionDialog(null, inputs, "Please enter the Todo-Session-Name", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, inputs[4]);
        if(output==0){
            String file = todopath+System.getProperty("file.separator")+FileName.getText();
            String[] content = {Charter,Areas," "," "," "," "," "," "," "," "," "," "," "," "};
            return fileHelper.createTestsession(file, content);
        }
        else{
            return false;
        }
    }
}
