package newlang5;

public interface LexicalAnalyzer {
    public LexicalUnit get() throws Exception;
    public boolean expect(LexicalType type) throws Exception;
    public boolean expect(int ahead, LexicalType type) throws Exception;
    public void unget(LexicalUnit token) throws Exception;
    public LexicalUnit peek() throws Exception;
    public LexicalUnit peek(int ahead) throws Exception;
}
