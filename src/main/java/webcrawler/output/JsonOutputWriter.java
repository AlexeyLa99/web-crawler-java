package webcrawler.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import webcrawler.result.CrawlResult;

/**
 * Writes crawl output as pretty-printed JSON.
 */
public class JsonOutputWriter implements OutputWriter {

    private final ObjectMapper mapper;

    public JsonOutputWriter() {
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void write(CrawlResult result, String filePath) throws IOException {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("pages", result.getPages());
        output.put("analysis", result.getAnalysis());
        mapper.writeValue(Path.of(filePath).toFile(), output);
    }
}
