package com.lemonthe.filesorter;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.Map.*;
import java.io.*;

public class FileSorter {

    public static void sortByCreationTime(List<String> sourceFiles,
            String destinationPath,
            String params) throws IOException {
        init(sourceFiles, destinationPath, params,
                DistributionCriteria.CREATION_TIME);
    }

    public static void sortByModificationTime(List<String> sourceFiles,
            String destinationPath,
            String params) throws IOException {
        init(sourceFiles, destinationPath, params,
                DistributionCriteria.MODIFICATION_TIME);
    }

    public static void sortByAccessTime(List<String> sourceFiles,
            String destinationPath,
            String params) throws IOException {
        init(sourceFiles, destinationPath, params,
                DistributionCriteria.ACCESS_TIME);
    }


    private static void sort(Path src, List<SortingParams> params,
            DistributionCriteria criteria) throws IOException {
        List<SortingParams> paramsCopy = new LinkedList<SortingParams>(params);
        Set<Path> dirNames = new HashSet<Path>();
        Map<Path, Path> data = null;
        int paramsIndex;

        if (paramsCopy.isEmpty()) {
            return;
        }

        paramsIndex = paramsCopy.get(0).getIndex();
        paramsCopy.remove(0);

        data = mapFilesToDirs(src, paramsIndex, criteria);

        dirNames.addAll(data.values());
        for (Path s : dirNames) {
            System.out.println(s);
            Files.createDirectory(s);
        }

        for (Entry<Path, Path> e : data.entrySet()) {
            Files.move(e.getKey(), 
                    e.getValue().resolve(e.getKey().getFileName()));
        }

        for (Path d : dirNames)
            sort(d, paramsCopy, criteria);
    }

    static private Map<Path, Path> mapFilesToDirs(Path src, int index,
            DistributionCriteria criteria) throws IOException {
        Map<Path, Path> result = new HashMap<Path, Path>();
        try (DirectoryStream<Path> entries =
                Files.newDirectoryStream(src)) {
            for (Path entry : entries) {
                BasicFileAttributes bs = Files
                        .readAttributes(entry, BasicFileAttributes.class);
                String time;
                if (criteria == DistributionCriteria.CREATION_TIME)
                    time = bs.creationTime().toString().substring(0, 10);
                else if (criteria == DistributionCriteria.ACCESS_TIME)
                    time = bs.lastAccessTime().toString().substring(0, 10);
                else if (criteria == DistributionCriteria.MODIFICATION_TIME)
                    time = bs.lastModifiedTime().toString().substring(0, 10);
                else
                    time = bs.creationTime().toString().substring(0, 10);
                Path dirName = entry
                        .getParent()
                        .resolve(time.split("-")[index]);
                result.put(entry, dirName);
                System.out.println("DirName = " + dirName);
            }
        }
        return result;
    }

    static private void init(List<String> sourceFiles,
            String destinationPath,
            String params,
            DistributionCriteria criteria) throws IOException {
        List<SortingParams> sortParams = pullParams(params);
        Path destPath = Paths.get(destinationPath);
        List<Path> srcPaths = new LinkedList<Path>();
        for (String s : sourceFiles) {
            srcPaths.add(Paths.get(s));
        }
        
        initSorting(destPath, srcPaths);
        sort(destPath, sortParams, criteria);
    }

    static private void initSorting(Path destinationPath,
            List<Path> sourcePaths) throws IOException {
        Files.createDirectory(destinationPath);
            for (Path entry : sourcePaths) {
                Files.move(entry,
                        destinationPath.resolve(entry.getFileName())/*, StandardCopyOption.REPLACE_EXISTING*/);
            }
    }

    static public List<SortingParams> pullParams(String params) {
        List<SortingParams> result = new LinkedList<SortingParams>();
        for (char c : params.toCharArray()) {
            SortingParams t = switch (c) {
                case 'd', 'D':
                    yield SortingParams.DAY;
                case 'm', 'M':
                    yield SortingParams.MONTH;
                case 'y', 'Y':
                    yield SortingParams.YEAR;
                default:
                    yield SortingParams.YEAR;
            };
            if (!result.contains(t))
                result.add(t);
        }
        Collections.sort(result);
        System.out.println(result);
        return result;
    }

    enum SortingParams {
        YEAR(0),
        MONTH(1),
        DAY(2);
        SortingParams(int index) { this.index = index; }
        public int getIndex() { return this.index; }
        private int index;
    };
    enum DistributionCriteria {
        CREATION_TIME,
        MODIFICATION_TIME,
        ACCESS_TIME;
    }
}
