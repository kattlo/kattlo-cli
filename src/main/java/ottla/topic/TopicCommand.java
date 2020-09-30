package ottla.topic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "topic",
    description = "Migrations over Topics"
)
public class TopicCommand implements Runnable {

    private String diretory;

    @Option(
        names = {
            "-d",
            "--directory"
        },
        description = "Diretory with topic migrations",
        required = true
    )
    public void setDirectory(String directory){
        this.diretory = Objects.requireNonNull(directory);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        System.out.println(Path.of(diretory));

    }
    
}
