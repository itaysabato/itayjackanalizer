import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Rotmus
 * Date: 27/12/2010
 * Time: 21:24:06
 * To change this template use File | Settings | File Templates.
 */
public class MyReader implements Readable{

    private ArrayList<String> strings;
    private Scanner source;
    private String buf="";
   private  boolean lookfor = false ;
    
    public MyReader(File file, ArrayList<String> strings) throws FileNotFoundException {
        this.strings = strings;
        source = new Scanner(file);
        source.useDelimiter("(\\s)");
    }

    public int read(CharBuffer cb) throws IOException {
        String str = "";
        String line = "";
        int size = 0;
       //if remains:
        if(buf.length()>0){
            if(buf.length()<cb.length() )   size = buf.length();
            else size = cb.length();
            cb.append(buf.substring(0,size));
            buf = buf.substring(size);
            return size;
        }


        if(source.hasNextLine()){
            line = source.nextLine();
        }
        else{
            while(source.hasNext()){
                line+=" "+source.next();  
            }
            if(line.length()==0) return -1;
        }
        line = change(line,"//");
        line = change(line,"/*");
        line = change(line,"*/");
        
        // / / :
        if(line.contains("//")){
                line = line.substring(0,line.indexOf("//"));
        }
        // " / * " :
         while(!line.equals("")){
             if(!lookfor){
                 if(line.contains("/*")) {
                    str += line.substring(0,line.indexOf("/*"));
                    if(line.indexOf("/*")+2>=line.length()){
                        lookfor = true;
                        break;
                    }
                    else line =  line.substring(line.indexOf("/*")+2);
                     lookfor = true;
                 }
                 else {
                     str +=line;
                     break;
                 }
             }
             
             if(lookfor){
                  if(line.contains("*/")){
                        if(line.indexOf("*/")+2>=line.length()){
                            lookfor = false;
                            break;
                        }
                        else   line =  line.substring(line.indexOf("*/")+2);
                        lookfor = false;
                  }
                  else break;
             }
         }
        int i=0, start=0;
        // " " " :

        while(str.indexOf('"',i)!=-1)  {
            start = str.indexOf('"',i);
            i =   str.indexOf('"',start+1);
            if(i+1>=str.length()){
                strings.add(str.substring(start+1,i).replaceAll("א","//").replaceAll("ב","/*").replaceAll("ג","*/"));
            	str = str.substring(0,start+1)+"\"";
                break;
            }
            else{
                strings.add(str.substring(start+1,i).replaceAll("א","//").replaceAll("ב","/*").replaceAll("ג","*/"));
                str = str.substring(0,start+1)+str.substring(i);
                i=start+2;
            }
        }

        if(cb.length()<str.length()) {
            buf = str.substring(cb.length());
            str = str.substring(0,cb.length());
        }
        cb.append(str);

        return str.length();
    }

	private String change(String line,String str) {
		int from = 0;
		int token = 0;
		int merch = 0;
		int merch2 = 0;
		String type = null;
		if(str.equals("//")) type = "א";
		else if(str.equals("/*")) type = "ב";
		else if(str.equals("*/")) type = "ג";
		
		if(line.isEmpty()) return line;
		
		while(from<line.length()){
			merch = line.indexOf("\"", from);
			if(merch!=-1){
				token = line.indexOf(str, merch);
				if(token==-1) break;
				merch2 = line.indexOf("\"", merch+1);
				if(merch2==-1) break;
				if(token<merch2)
					line = line.substring(0,token)+type+line.substring(token+2);
				from = merch2+1;
			}
			else break;
		}
		return line;
		
	}
}
