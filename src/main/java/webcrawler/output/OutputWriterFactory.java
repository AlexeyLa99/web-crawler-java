package webcrawler.output;

/**
 * Factory for creating output writers by format name.
 */
public final class OutputWriterFactory {

    private OutputWriterFactory() {
    }

    /**
     * Creates an output writer for the requested format.
     *
     * @param format output format (currently supports json)
     * @return matching writer instance
     */
    public static OutputWriter createWriter(String format) {
        if (format == null) {
            return new JsonOutputWriter();
        }

        String normalized = format.trim().toLowerCase();
        if ("json".equals(normalized)) {
            return new JsonOutputWriter();
        }

        return new JsonOutputWriter();
    }
}
