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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

/**
 *
 * @author Sven Finsterwalder
 */
public class ReportingHelper extends JWindow {

    FileHelper fileHelper = new FileHelper();
    TextHelper textHelper = new TextHelper();
    ArrayList<String> Path = new ArrayList<String>();
    public TreeMap<String, Integer> charterMap = new TreeMap<String, Integer>();
    final File directory = new File(fileHelper.getApprovedDir());

    public void addReport(JPanel jp) {
        jp.removeAll();
        PieDataset dataset = createDataset();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, "Sessions by Tester");
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.updateUI();
        chartPanel.setSize(490, 350);

        jp.revalidate();
        jp.setOpaque(true);
        jp.add(chartPanel);
        jp.revalidate();
        jp.setVisible(true);
        jp.setSize(700, 500);
        jp.revalidate();
        jp.repaint();
    }

    /**
     * Creates a sample dataset
     */
    private PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        ArrayList<String> Tester = new ArrayList<String>();
        ArrayList<String> sessionList = fileHelper.getSessionList(fileHelper.getApprovedDir());
        Tester = fileHelper.getTester();
        int[] counter = new int[Tester.size()];
        for (String s : sessionList) {
            for (String n : Tester) {
                if (s.contains(n.substring(0, 3))) {
                    counter[Tester.indexOf(n)]++;
                    break;
                }
            }
        }
        for (String n : Tester) {
            result.setValue(n.substring(4), counter[Tester.indexOf(n)]);
        }
        return result;

    }

    /**
     * Creates a chart
     */
    private JFreeChart createChart(PieDataset dataset, String title) {

//        JFreeChart chart = ChartFactory.createPieChart3D(
        JFreeChart chart = ChartFactory.createPieChart(
                title, // chart title
                dataset, // data
                false, // include legend
                true,
                false);

//        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
        plot.setStartAngle(90);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;

    }

    public DefaultTableModel getTableModel() {

        charterMap.clear();
        getCharterBackgroundW(directory);
        Set<String> keys = charterMap.keySet();
        String col[] = {"Charter", "Number of Testsessions"};
        DefaultTableModel model = new DefaultTableModel(null, col) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String k : keys) {
            model.insertRow(0, new Object[]{k, charterMap.get(k)});
        }
        return model;
    }

    private void getCharterBackgroundW(File f) {
        fileHelper.charterList.clear();
        if (fileHelper.getFilesWithCharter(f, false)) {
            int i = 0;
            for (String charter : fileHelper.charterList) {
                if (charterMap.containsKey(charter.substring(0, charter.indexOf("(" + f)))) {
                    i = charterMap.get(charter.substring(0, charter.indexOf("(" + f)));
                    i++;
                    charterMap.remove(charter.substring(0, charter.indexOf("(" + f)));
                    charterMap.put(charter.substring(0, charter.indexOf("(" + f)), i);
                } else {
                    charterMap.put(charter.substring(0, charter.indexOf("(" + f)), 1);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Directory '" + f + "' does not exsits, please edit the config.txt!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getAllSessionCount(DefaultTableModel model) {
        int rows = model.getRowCount();
        int counter = 0;
        int i = 0;
        while (i < rows) {
            counter += Integer.valueOf(model.getValueAt(i, 1).toString());
            i++;
        }
        return String.valueOf(counter);
    }

    public String[] getAllBugsAndIssues() {
        int countedBugs = 0;
        int countedIssues = 0;
        String[] BaI = new String[2];
        for (File filename : directory.listFiles()) {
            if (filename.isFile()&& filename.getName().endsWith("ses")) {
                fileHelper.getTestsession(filename);
                String Bugs = fileHelper.toreviewSession.substring(fileHelper.toreviewSession.indexOf("BUGS") + "BUGS".length(),
                        fileHelper.toreviewSession.indexOf("ISSUES")).trim();
                String Issues = fileHelper.toreviewSession.substring(fileHelper.toreviewSession.indexOf("ISSUES") + "ISSUES".length(),
                        fileHelper.toreviewSession.indexOf("REVIEW")).trim();

                countedBugs += textHelper.countBugIssue(Bugs, "#BUG");
                countedIssues += textHelper.countBugIssue(Issues, "#ISSUE");
            }
        }
        BaI[0]="Bugs: "+countedBugs;
        BaI[1]="Issues: " + countedIssues;
        return BaI;

    }
}
