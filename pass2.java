import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.Math;

class pass2
{
    /**
     *  Reads the intermediate file, symbol table file and outputs:
     *  object file
     *  assembly listing file
     */
    static int error_flag = 0;
    static String line ="";
    static String text = ""; // text record instructions to be appended

    static String LABEL = "";
    static String OPCODE = "";
    static String OPERAND = "";

    static Hashtable<String, ArrayList<String>> OPTAB = tables.OPTAB();
    static Hashtable<String, String> ASSEMDIR =  tables.ASSEMDIR();
    static Hashtable<String, ArrayList<String>> REGISTER =  tables.REGISTER();
    static LinkedHashMap<String, String> SYMTAB = new LinkedHashMap<String, String>();

    public static String get_label(String line)
    {
        String x = remove_comment(line.trim());
        String arr[] = x.split(" ");
        String symbol = "";
        if (arr.length <= 2)
        {
            return "-1";
        }
        else 
        {
            symbol = arr[0];
        }
        return symbol;
    }
    public static String get_opcode(String line)
    {
        String x = remove_comment(line.trim());
        String arr[] = x.split(" ");
        String opcode="";
        if (arr.length <= 2)
        //include opcode only instructions
        {opcode=arr[0];}
        else 
        {opcode=arr[1];}
        return opcode;
    }
    public static String get_operand(String line)
    {
        String x = remove_comment(line.trim());
        String arr[] = x.split(" ");
        String operand="";
        if (arr.length == 1)
        {operand="-1";}
        else if (arr.length == 2)
        {operand=arr[1];}
        else 
        {operand=arr[2];}
        return operand;
    }

    public static boolean is_comment(String line)
    {
        String x = line.trim();
        if (x.charAt(0)=='.')
        {
            return true;
        }
        return false;
    }

    public static void insert_symbol(String label, String locctr)
    {
        SYMTAB.put(label, locctr);
    }

    public static boolean is_end(String line)
    {
        line = line.trim();
        if (line.length()<3)
        {return false;}
        if (line.substring(0,3).equals("END") && remove_comment(line.trim()).split(" ").length == 2)
        {return true;}
        return false;
    }

    public static String remove_comment(String line)
    {
        int idx = line.indexOf(".");
        String l="";
        if (idx<0)
        {return line;}

        for (int i=0; i<idx; i++)
        {l+=line.charAt(i);}
        return l;
    }

    public static void printSYMTAB()
    {
        for (Map.Entry<String, String> ele : SYMTAB.entrySet()) 
        {
            String key = ele.getKey();
            String val = ele.getValue();
            line = key+":"+val;
            System.out.println(line);
        }
    }
    
    public static String[] getHeaderData(BufferedReader br) throws FileNotFoundException, IOException
    {
        line ="";
        String prev="";
        String first="";
        try
        {
            prev = br.readLine();
            first = String.valueOf(prev);
            while((line=br.readLine()) != null)
            {prev=line;}
        }
        finally{}
        String length = prev.split(":")[1].trim();
        String name = first.split(" ")[0].trim();
        String arr[] = first.split(" ");
        String address = arr[arr.length-1].trim();
        String x[] = {name, convert.extendTo(6, length), convert.extendTo(6, address), first};
        return x;
    }

    public static void printTokens(ArrayList<String> tokens)
    {
        for(String token:tokens)
        {
            System.out.print("["+token+"]\t");
        }
        System.out.println();
    }

    public static ArrayList<String> getTokens(String l)
    {
        /**
         *  given line input from intermediate file return individual tokens
         */
        ArrayList<String> tokens = new ArrayList<String>();
        String x="";
        for (int i = 0; i < l.length(); i++) 
        {
            if (l.charAt(i)==' ' || l.charAt(i)=='\t')
            {
                continue;
            } 
            else if (l.charAt(i) == '.') 
            {
                return tokens;
            } 
            else
            {
                while (i < l.length() && (l.charAt(i)!=' ' && l.charAt(i) != ',' && l.charAt(i)!='\t')) 
                {
                    x += l.charAt(i);
                    i++;
                }
                tokens.add(x);
                x="";
            }
        }
        return tokens;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        /**
         * error flags:
         * 0 => no error
         * 1 => duplicate symbol
         * 2 => invalid opcode
         * 3 => invalid instruction format
         */

        // read symbol table entries from the symtab.txt file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("symtab.txt"), StandardCharsets.UTF_8));)
          {
            while((line=br.readLine()) != null)
            {
                line = line.replaceAll("\t\t", " ").trim();
                String arr[] = line.split(" ");
                SYMTAB.put(arr[0], arr[1]);

                //print symbol table
                //printSYMTAB();
            }
          }

        
        // pass the path to the file as a parameter
        String x[]={""};
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("intermediate.txt"), StandardCharsets.UTF_8));)
        {
            //header record {name, length, address, first_line}
            x = getHeaderData(br);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("intermediate.txt"), StandardCharsets.UTF_8));
          FileWriter object = new FileWriter("object.txt");
          BufferedWriter bw_object = new BufferedWriter(object);
          FileWriter listing = new FileWriter("listing.txt");
          BufferedWriter bw_listing = new BufferedWriter(listing);)
        {
            OPCODE = get_opcode(x[3]);
            if (OPCODE.equals("START"))
            {
                //starting address 
                error_flag=0;
                bw_listing.write(x[3]+"\n");
                line=br.readLine();
            }

            //header record {name, length, address, first_line}
            String header = "H"+x[0]+"^"+x[2]+"^"+x[1];
            bw_object.write(header); //header record

            //continue pass 2 after header record/ start assembler directive

            //intialize the first text record
            String T="";
            while((line=br.readLine())!=null && !is_end(line))
            {
                //get the object codes and append to `text`
                if (is_comment(line))
                {
                    //write the comment to the listing file
                    bw_listing.write(line+"\n");
                    continue;
                }
                ArrayList<String> tokens = getTokens(line);
                printTokens(tokens);
                //LABEL = get_label(line);
                //OPCODE = get_opcode(line);
                //OPERAND = get_operand(line);
            }
        }
    }
}