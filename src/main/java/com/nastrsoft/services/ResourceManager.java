package com.nastrsoft.services;

import com.nastrsoft.model.Type;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.valueOf;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

@Service
public class ResourceManager implements InitializingBean {

    public static final String DB_EXT = ".db";
    public static final String DIFF_SEPARATOR = "and";
    public static final String DIFF_EXT = ".sql";

    private List<Path> dbFiles;
    private List<Path> patchFiles;
    private Path latestDb;

    private Comparator<Path> dbsComparator = Comparator.comparingInt(p ->
            valueOf(substringBefore(p.getFileName().toString(), DB_EXT)));

    private Comparator<Path> patchComparator = Comparator.comparingInt((Path p) ->
            valueOf(substringBetween(p.getFileName().toString(), DIFF_SEPARATOR, DIFF_EXT)))
            .thenComparingInt(s1 -> valueOf(substringBefore(s1.getFileName().toString(), DIFF_SEPARATOR)));

    @PostConstruct
    public void afterPropertiesSet() throws FileNotFoundException {
        dbFiles = IntStreamEx.range(6).mapToObj(i -> Paths.get(i + ".db")).toImmutableList();
        latestDb = StreamEx.of(dbFiles).max(dbsComparator).orElseThrow(FileNotFoundException::new);
        patchFiles = IntStreamEx.range(valueOf(substringBefore(latestDb.getFileName().toString(), DB_EXT)))
                .mapToObj(i -> Paths.get(i + "and" + latestDb + ".sql")).toImmutableList();
    }

    public void setCustomLatestDB(String fileName) throws IOException, InterruptedException {
        Path path = Paths.get(fileName);
        if (latestDb != null && !dbFiles.isEmpty() && dbFiles.get(dbFiles.size() - 1).equals(path)) {
            patchFiles = new ArrayList<>();
            TimeUnit.SECONDS.sleep(3);
            latestDb = StreamEx.of(dbFiles).max(dbsComparator).orElseThrow(FileNotFoundException::new);
            patchFiles = IntStreamEx.range(valueOf(substringBefore(fileName, DB_EXT)))
                    .mapToObj(i -> Paths.get(i + "and" + latestDb + ".sql")).toImmutableList();
        } else {
            if (dbFiles.contains(path)) {
                patchFiles = new ArrayList<>();
                TimeUnit.SECONDS.sleep(3);
                latestDb = path;
                patchFiles = IntStreamEx.range(valueOf(substringBefore(fileName, DB_EXT)))
                        .mapToObj(i -> Paths.get(i + "and" + latestDb + ".sql")).toImmutableList();
            } else
                throw new FileNotFoundException("Provided DB " + path + " doesn't exist");
        }
    }

    public ResponseEntity<String> getResponse(Type type) throws InterruptedException {
        switch (type) {
            case DB:
                return ResponseEntity.ok("{ \"latest\":\"" + latestDb.getFileName().toString() + "\"," +
                        "\"list\":" +
                        dbFiles.stream().map(p1 -> "\"" + p1.getFileName().toString() + "\"").collect(joining(",", "[", "]")) +
                        "}");
            case PATCH:
                Instant now = Instant.now();
                while (patchFiles.isEmpty()) {
                    TimeUnit.SECONDS.sleep(1);
                    if (now.isAfter(now.plus(2, ChronoUnit.MINUTES)))
                        return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(patchFiles.stream().map(p -> "\"" + p.getFileName().toString() + "\"")
                        .collect(joining(",", "{\"list\":[", "]}")));
            default:
                return ResponseEntity.badRequest().build();
        }
    }

}
