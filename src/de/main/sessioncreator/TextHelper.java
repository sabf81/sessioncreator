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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Sven Finsterwalder
 */
public class TextHelper {

    int sum;

    public String Timestamp(String format) {
        try {
            Date dt = new Date();
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.format(dt);
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean checkTaskBreakdown(HashMap<JLabel, String> values) {

        Set<JLabel> keys = values.keySet();
        int i = 0;
        int missing = 0;
        sum = 0;
        String Text = "";
        for (JLabel str : keys) {
            try {
                i = Integer.parseInt(values.get(str));
                sum = sum + i;
                Text = Text + "- " + str.getText() + "\n";
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(str, e, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (sum != 100) {
            missing = 100 - sum;
            if (missing < 0) {
                JOptionPane.showMessageDialog(null, "Please check the sum for:\n" + Text
                        + "The sum must match 100%!\n Your sum is " + sum + "%. Please remove " + missing + "%!", missing + "% too much!", JOptionPane.WARNING_MESSAGE);
                return false;
            } else {
                JOptionPane.showMessageDialog(null, "Please check the sum for:\n" + Text
                        + "The sum must match 100%!\n Your sum is " + sum + "%. Please add " + missing + "%!", missing + "% too little!", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public int countBugIssue(String text, String what) {
        String[] w = text.split(what);
        int anzahl = 0;
        if (w.length == 0) {
            anzahl = 0;
        } else {
            anzahl = w.length - 1;
        }
        return anzahl;
    }
}
