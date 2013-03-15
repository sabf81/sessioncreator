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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Sven Finsterwalder
 */
public class FileHelper {

    String nameOfCharter;
    public ArrayList<String> charterList = new ArrayList<String>();
    public TreeMap<String, List> areaMap = new TreeMap<String, List>();
    public ArrayList areaList = new ArrayList();
    public TreeMap<String, String> contentMap = new TreeMap<String, String>();
    public ArrayList<String> sessionList = new ArrayList<String>();
    private ArrayList<String> allTester = new ArrayList<String>();
    private ArrayList<String> configPaths = new ArrayList<String>();
    public ArrayList<String> areaChecklist = new ArrayList<String>();
    String toreviewSession;
    String zeilenumbruch = System.getProperty("line.separator");

    public boolean getFilesWithCharter(File dir, boolean subdirectory) {
        if (dir.exists()) {
            if (!dir.toString().contains(".svn")) {
                for (File files : dir.listFiles()) {
                    if (subdirectory) {
                        if (files.isDirectory() && !files.toString().contains(".svn")) {
                            getFilesWithCharter(files, subdirectory);
                        }
                    }
                    if (files.isFile() && files.getName().endsWith("ses")) {
                        String Charter = getCharter(files);
                        charterList.add(Charter + " (" + files.getPath() + ")");
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private String getCharter(File filename) {
        nameOfCharter = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                if (zeile.contains("#AREAS")) {
                    break;
                }
                nameOfCharter += zeile.replace("-----------------------------------------------", " ").replace("CHARTER", "");
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return nameOfCharter;
    }

    public void getAreasChecklist(File filename) {
        areaChecklist.clear();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                if (zeile.contains("START")) {
                    break;
                }
                areaChecklist.add(zeile);
            }
            while (!areaChecklist.get(0).contains("#AREAS")) {
                areaChecklist.remove(0);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "File not found", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public TreeMap<String, List> getAreas(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String zeile = null;
            int c = 0;
            while ((zeile = in.readLine()) != null) {
                if ((!zeile.contains("|") && !areaMap.containsKey(String.format("%02d", c) + " " + zeile))
                        || (zeile.contains("|") && !areaMap.containsKey(String.format("%02d", c) + " "
                        + (zeile.substring(0, zeile.indexOf("|")).trim().toUpperCase())))) {
                    areaList.clear();
                    c++;
                }
                if (zeile.contains("#") || zeile.startsWith(" ")) {
                } else if (zeile.contains("|")) {

                    areaList.add(zeile.trim());
                    areaMap.put(String.format("%02d", c) + " " + zeile.substring(0, zeile.indexOf("|")).trim().toUpperCase(), new ArrayList(areaList));
                } else if (!zeile.isEmpty() && !areaMap.containsKey(zeile)) {
                    areaList.add(zeile.trim());
                    areaMap.put(String.format("%02d", c) + " " + zeile.trim().toUpperCase(), new ArrayList(areaList));
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return areaMap;
    }

    public boolean createTestsession(String Filename, String[] content) {
        String sessionTemplate = System.getProperty("user.dir") + "/session-template.ses";
        contentMap.put("<charter>", content[0].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<area>", content[1].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<start>", content[2].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<tester>", content[3].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<duration>", content[4].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<problems>", content[5].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<setup>", content[6].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<bugi>", content[7].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<on mission>", content[8].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<datafile>", content[9].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<testnotes>", content[10].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<bugs>", content[11].trim());
        contentMap.put("<issues>", content[12].replaceAll("\n", zeilenumbruch).trim());
        contentMap.put("<review>", content[13].replaceAll("\n", zeilenumbruch).trim());
        Set<String> keys = contentMap.keySet();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Filename));
            BufferedReader in = new BufferedReader(new FileReader(sessionTemplate));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                if (zeile.contains("<")) {
                    for (String k : keys) {
                        if (zeile.contains(k)) {
                            if (contentMap.get(k).isEmpty()) {
                                writer.append("#N/A");
                                writer.newLine();
                            } else {
                                writer.append(contentMap.get(k));
                                writer.newLine();
                            }
                        }
                    }
                } else {
                    writer.append(zeile);
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();
            in.close();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public ArrayList<String> getSessionList(String dir) {
        sessionList.clear();
        try {
            File d = new File(dir);
            String[] fileList = d.list();
            sessionList.addAll(Arrays.asList(fileList));
            return sessionList;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e+" "+e.getCause().toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return sessionList;
        }
    }

    public String checkFileExists(String name) {
        String n = name;
        File f = new File(n);
        String newFileEnd = "";
        String newFileName = "";
        char nc;
        if (f.exists()) {
            StringBuilder b = new StringBuilder(n);
            nc = name.charAt(name.lastIndexOf("-") + 1);
            nc++;
            newFileEnd = newFileEnd + nc;
            newFileName = b.replace(n.lastIndexOf("-") + 1, n.lastIndexOf("-") + 2, newFileEnd).toString();
            String checkFileExists = checkFileExists(newFileName);
            return checkFileExists;
        } else {
            return n;
        }
    }

    public ArrayList<String> getTester() {
        String testerList = System.getProperty("user.dir") + "/Tester.txt";
        try {
            allTester.clear();
            BufferedReader in = new BufferedReader(new FileReader(testerList));
            String zeile = null;
            for (int i = 0; (zeile = in.readLine()) != null; i++) {
                if (zeile.isEmpty()) {
                } else {
                    allTester.add(zeile);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return allTester;
    }

    public ArrayList<String> getConfigPath() {
        String configList = System.getProperty("user.dir") + "/config.txt";
        try {
            configPaths.clear();
            BufferedReader in = new BufferedReader(new FileReader(configList));
            String zeile = null;
            for (int i = 0; (zeile = in.readLine()) != null; i++) {
                configPaths.add(zeile);
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "File not Found!", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "IO Exception", JOptionPane.ERROR_MESSAGE);
        }
        return configPaths;
    }

    public String getApprovedDir() {
        String dir = "";
        this.getConfigPath();
        for (String p : configPaths) {
            if (p.toLowerCase().contains("aproved")) {
                dir = p.substring(p.indexOf(":") + 1);
                break;
            }
        }
        return dir;
    }

    public void getTestsession(File filename) {
        toreviewSession = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String zeile = null;
            while ((zeile = in.readLine()) != null) {
                if (!zeile.endsWith("-") & !zeile.startsWith("---")) {
                    toreviewSession = toreviewSession + zeile + ("\n");
                }

            }
            in.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
