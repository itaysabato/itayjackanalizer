import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private Writer writer;

    public CompilationEngine(JackTokenizer tokenizer, Writer writer) {
        this.tokenizer = tokenizer;
        this.writer = writer;
    }

    public void compileClass() throws IOException {
        writer.write("<class>\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();

            if(!type.equals(TokenType.KEYWORD)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.FIELD) || keyword.equals(Keyword.STATIC)){
                    compileClassVarDec(keyword);
                }
                else {
                    compileSubroutine(keyword);
                }
            }
        }

        writer.write("</class>\n");
    }

    private void compileSubroutine(Keyword keyword) throws IOException {
        writer.write("<subroutineDec>\n");
        writer.write(TokenType.KEYWORD.wrap(keyword));

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            if(!type.equals(TokenType.SYMBOL)){
                writer.write(type.wrap(tokenizer.token())+"\n");
            }
            else {
                String symbol = tokenizer.token();
                if(symbol.equals("(")){
                    writer.write(type.wrap(symbol)+"\n");
                    compileParameterList();
                    writer.write(type.wrap(")")+"\n");
                }
                else if(symbol.equals("{")){
                    compileSubroutineBody(symbol);
                    break;
                }
            }
        }
        writer.write("</subroutineDec>\n");
    }

    private void compileSubroutineBody(String symbol) throws IOException {
        writer.write("<subroutineBody>\n");
        writer.write(TokenType.SYMBOL.wrap(symbol)+"\n");

        while(tokenizer.advance()){
             TokenType type = tokenizer.tokenType();
            if(type.equals(TokenType.KEYWORD)){
                Keyword keyword = tokenizer.keyword();
                if(keyword.equals(Keyword.VAR)){
                    compileVarDec(keyword);
                }
                else {
                    compileStatements(keyword);
                    break;
                }
            }
            else {
                compileStatements(null);
                break;
            }
        }

        writer.write(TokenType.SYMBOL.wrap("}")+"\n");
        writer.write("</subroutineBody>\n");
    }

    private void compileParameterList() throws IOException {
       writer.write("<parameterList>\n");

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            if(token.equals(")") ) break;
            writer.write(type.wrap(token)+"\n");
        }

        writer.write("</parameterList>\n");
    }

    private void compileClassVarDec(Keyword keyword) throws IOException {
        compileTemplateVarDec(keyword,"classVarDec");
    }

    private void compileVarDec(Keyword keyword) throws IOException {
        compileTemplateVarDec(keyword,"varDec");
    }

    private void compileTemplateVarDec(Keyword keyword,String name) throws IOException {
        writer.write("<"+name+">\n");

        writer.write(TokenType.KEYWORD.wrap(keyword+"\n"));

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();
            if(token.equals(";") ){
                writer.write(type.wrap(token)+"\n");
                break;
            }
            writer.write(type.wrap(token)+"\n");
        }

        writer.write("</"+name+">\n");
    }

    private void compileStatements(Keyword keyword) throws IOException {

        writer.write("<statements>\n");

        if(keyword==null){
            writer.write("</statements>\n");
        }

        compileStatement(keyword);

        while(tokenizer.advance()){
            TokenType type = tokenizer.tokenType();
            String token = tokenizer.token();

            if(!type.equals(TokenType.KEYWORD) ) break;

            compileStatement(tokenizer.keyword());
        }

        writer.write("</statements>\n");
    }

    private void compileStatement(Keyword keyword) {
        //To change body of created methods use File | Settings | File Templates.
    }
}


