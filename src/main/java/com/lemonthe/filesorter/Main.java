package com.lemonthe.filesorter;

import java.util.*;
import java.io.*;
import com.lemonthe.java.*;

class Main {
    private static CLArgsParser initParser() {
        CLArgsParser par = new CLArgsParser();
        String yearDescr    = "Sort by year";
        String monthDescr   = "Sort by month";
        String dayDescr     = "Sort by day";

        par.addPossibleOption(new CLOptionBuilder()
            .name("y")
            .description(yearDescr)
            .buildCLOption());
        par.addPossibleOption(new CLOptionBuilder()
            .name("m")
            .description(monthDescr)
            .buildCLOption());
        par.addPossibleOption(new CLOptionBuilder()
            .name("d")
            .description(dayDescr)
            .buildCLOption());
        par.addPossibleOption(new CLOptionBuilder()
            .name("criteria")
            .hasArg()
            .buildCLOption());
        return par;
    }
    
    public static void main(String[] args) {
        CLArgsParser par = initParser();

        try {
            par.processInput(args);
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        
        List<String> inputArgs = par.getArguments();
        List<CLReceivedOption> recOptions = par.getReceived();

        if (inputArgs.size() < 2) {
            System.out.println("Not enough args");
            return;
        }

        StringBuilder sortingParams = new StringBuilder();
        for (CLReceivedOption tmp : recOptions) {
            if (tmp.getOption().getName().equals("y"))
                sortingParams.append("y");
            else if (tmp.getOption().getName().equals("m"))
                sortingParams.append("m");
            else if (tmp.getOption().getName().equals("d"))
                sortingParams.append("d");
        }
        String srtParams = sortingParams.toString();
        System.out.println("srtparams: " + srtParams);
        if (srtParams.equals(""))
            System.out.println("Sorting params needed");


        try {
            String arg = null;
            for (CLReceivedOption tmp : recOptions)
                if (tmp.getOption().getName().equals("criteria"))
                    arg = tmp.getArgument();

            if (arg != null) {
                if (arg.equals("c"))
                    FileSorter.sortByCreationTime(
                            inputArgs.subList(0, inputArgs.size()-1),
                            inputArgs.get(inputArgs.size()-1),
                            srtParams);
                else if (arg.equals("m"))
                    FileSorter.sortByModificationTime(
                            inputArgs.subList(0, inputArgs.size()-1),
                            inputArgs.get(inputArgs.size()-1),
                            srtParams);
                else if (arg.equals("a"))
                    FileSorter.sortByAccessTime(
                            inputArgs.subList(0, inputArgs.size()-1),
                            inputArgs.get(inputArgs.size()-1),
                            srtParams);
                else
                    System.out.println("Incorrect option argument");
            } else {
                FileSorter.sortByCreationTime(inputArgs.subList(0, inputArgs.size()-1),
                        inputArgs.get(inputArgs.size()-1), srtParams);
            }

        } catch (IOException e ) {
            e.printStackTrace();
            return;
        }
        
    }
}
