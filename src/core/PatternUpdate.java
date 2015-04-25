/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jon-snow
 */
public class PatternUpdate {

    private LinkedHashMap<Integer, LinkedList<String>> patternUpdate = new LinkedHashMap<>();
    private Pattern regex;

    public PatternUpdate() {
        regex = Pattern.compile("\\(\\{\\w+(\\,\\w+)*\\}(\\,\\{\\w+(\\,\\w+)*\\})*\\)");
    }

    public PatternUpdate(Pattern regex) {
        this.regex = regex;
    }

    public boolean isValid(String orderedPartition) {
        Matcher m = regex.matcher(orderedPartition);

        if (m.matches()) {
            System.out.println("Pattern reconnu");
            return true;
        } else {
            System.out.println("Pattern non reconnu");
            return false;
        }
    }

    public boolean isFair(String orderedPartition, int nbVertex) {
        Set<Integer> presentNode = new HashSet();

        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(orderedPartition);

        while (m.find()) {
            if (!presentNode.add(Integer.parseInt(m.group()))) {
                return false;
            }
        }

        return presentNode.size() == nbVertex;
    }

    public boolean isFair(String orderedPartition, int nbVertex, String unitPart) {
        Set<Integer> presentNode = new HashSet();

        Pattern p = Pattern.compile(unitPart + "+");
        Matcher m = p.matcher(orderedPartition);

        while (m.find()) {
            if (!presentNode.add(Integer.parseInt(m.group()))) {
                return false;
            }
        }

        return presentNode.size() == nbVertex;
    }

    public void initPattern(String orderedPartition, int nbVertex) throws Exception {

        if (!(isValid(orderedPartition) && isFair(orderedPartition, nbVertex))) {
            throw new Exception("MalformedPatternException", new Throwable("MalformedPatternException"));
        }

        Pattern p = Pattern.compile("\\{\\w+(\\,\\w+)*\\}");
        Matcher m = p.matcher(orderedPartition);

        for (int numIter = 0; m.find(); numIter++) {
            LinkedList<String> parallelGroup = new LinkedList<>();

            Pattern p2 = Pattern.compile("\\w+");
            Matcher m2 = p2.matcher(m.group());
            System.out.println(m.group());
            
            while (m2.find()) {
                System.out.println(m2.group());
                parallelGroup.add(m2.group());
            }

            patternUpdate.put(numIter, parallelGroup);
        }
    }

    public void initPattern(String orderedPartition, int nbVertex, String unitPart) throws Exception {

        if (!(isValid(orderedPartition) && isFair(orderedPartition, nbVertex, unitPart))) {
            throw new Exception("MalformedPatternException", new Throwable("MalformedPatternException"));
        }

        Pattern p = Pattern.compile("\\{" + unitPart + "+(\\," + unitPart + "+)*\\}");
        Matcher m = p.matcher(orderedPartition);

        for (int numIter = 0; m.find(); numIter++) {
            LinkedList<String> parallelGroup = new LinkedList<>();

            Pattern p2 = Pattern.compile(unitPart + "+");
            Matcher m2 = p.matcher(m.group());

            while (m2.find()) {
                parallelGroup.add(m2.group());
            }

            patternUpdate.put(numIter, parallelGroup);
        }
    }

    public LinkedList<String> getStep(int num) {
        return patternUpdate.get(num);
    }

    public Set<Entry<Integer, LinkedList<String>>> getAllStep() {
        return patternUpdate.entrySet();
    }

    public static void main(String[] args) throws Exception {
        String s = new String("({1},{2,3})");
        int nbV = 3;

        PatternUpdate p = new PatternUpdate();

        p.initPattern(s, nbV);
    }
}
