import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.devsha256.mulelint.parser.MulePropertyExtractor;
import com.devsha256.mulelint.parser.MulePropertyExtractor.PropertyReference;

public class TestRegex {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("SampleMuleProject/src/main/mule/flow.xml")));
        List<PropertyReference> refs = MulePropertyExtractor.extractProperties(content);
        System.out.println("Extracted " + refs.size() + " properties from flow.xml");
        for (PropertyReference ref : refs) {
            System.out.println(ref.key + " at line " + ref.lineNumber);
        }
    }
}
