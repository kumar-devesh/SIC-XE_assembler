import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.Math;

class pass1
{
    /**
     *  Reads the test case file and outputs:
     *  Intermediate file
     *  Symboltab file
     */
    static int error_flag = 0;
    static String line ="";
    static String starting_address = "0";
    static String LOCCTR = "0";
    static String LOCCTR_next = "0";
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
    public static void main(String[] args) throws Exception
    {
        /**
         * error flags:
         * 0 => no error
         * 1 => duplicate symbol
         * 2 => invalid opcode
         * 3 => invalid instruction format
         */

        // pass the path to the file as a parameter
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("tc1.txt"), StandardCharsets.UTF_8));
          FileWriter symboltab = new FileWriter("symtab.txt");
          BufferedWriter bw_symboltab = new BufferedWriter(symboltab);
          FileWriter intermediate = new FileWriter("intermediate.txt");
          BufferedWriter bw_intermediate = new BufferedWriter(intermediate);)
        {
            line = br.readLine();
            String LABEL = get_label(line);
            String OPCODE = get_opcode(line);
            String OPERAND = get_operand(line);

            if (OPCODE=="START")
            {
                //starting address 
                error_flag=0;
                starting_address = OPERAND;
                LOCCTR = starting_address;
                bw_intermediate.write(LOCCTR+"\t\t"+error_flag+"\t\t"+line+"\n");
                line = br.readLine();
            }

            // start reading in the program
            do
            {
                error_flag=0;
                if (is_comment(line))
                {
                    //System.out.println("debug check comment");
                    bw_intermediate.write(line+"\n");
                    line = br.readLine();
                    continue;
                }

                //System.out.println("current line: "+line);
                LABEL = get_label(line);
                OPCODE = get_opcode(line);
                OPERAND = get_operand(line);

                System.out.println("\n"+line);
                System.out.printf("Label:{%s}, Opcode:{%s}, Operand:{%s}", LABEL,  OPCODE, OPERAND);

                /**
                 * write to the symbol table
                 * bw_symboltab.write(str+"\n");
                 */
                
                /**
                 * write to the intermediate file
                 * bw_intermediate.write(str+"\n");
                 */

                //symbol table 
                if (LABEL!="-1")
                {
                    //check if symbol is present
                    if (SYMTAB.containsKey(LABEL))
                    {
                        //set error flag
                        error_flag =1;
                    }
                    else
                    {
                        //add symbol
                        insert_symbol(LABEL, LOCCTR);
                    }
                }

                //ASSEMBLER DIRECTIVE CHECK
                if (ASSEMDIR.containsKey(OPCODE))
                {
                    if (OPERAND=="-1")
                    {
                        //invalid instruction format
                        error_flag=3;
                    }

                    if (OPCODE.equals("WORD"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+3);}

                    else if (OPCODE.equals("BYTE"))
                    {
                        char type = OPERAND.charAt(0);
                        int nb = 0;
                        if (type=='X')
                        {
                            //half bytes X'05'
                            float n_bytes = 0.0f;
                            for(int i=2; i<OPERAND.length()-1; i++)
                            {
                                n_bytes+=0.5;
                            }
                            nb = (int) Math.ceil(n_bytes);
                        }
                        else
                        {
                            //ascii characters C'EOF'
                            float n_bytes = 0.0f;
                            for(int i=2; i<OPERAND.length()-1; i++)
                            {
                                n_bytes+=1;
                            }
                            nb = (int) Math.ceil(n_bytes);
                        }
                        LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+nb);
                    }

                    // CHECK FOR OPCODES
                    else if (OPCODE.equals("RESW"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+3*Integer.parseInt(OPERAND));}

                    else if (OPCODE.equals("RESB"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(OPERAND));}
                    
                    if (!OPCODE.equals("BYTE") && !OPCODE.equals("RESB") && !OPCODE.equals("WORD") && !OPCODE.equals("RESW"))
                    {LOCCTR = LOCCTR_next;
                    bw_intermediate.write("\t\t\t\t"+line+"\n");
                    line = br.readLine();
                    continue;}
                }
                else if (OPTAB.containsKey(OPCODE))
                {
                    System.out.println("::: is opcode!");
                    //increase LOCCTR by the size of instruction
                    ArrayList<String> list = (ArrayList<String>) OPTAB.get(OPCODE);
                    LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(list.get(1)));
                }
                else if (OPTAB.containsKey(OPCODE.substring(1, OPCODE.length())) && OPCODE.charAt(0)=='+')
                {
                    System.out.println("::: is opcode format 4!");

                    //increase LOCCTR by the size of instruction
                    System.out.println("before "+LOCCTR);
                    LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+4);
                    System.out.println("after "+LOCCTR);
                }
                else 
                { 
                    // not a valid opcode!!
                    error_flag = 2;
                }

                //write to the intermediate file
                bw_intermediate.write(LOCCTR+"\t\t"+error_flag+"\t\t"+line+"\n");
                LOCCTR = LOCCTR_next;

                //read next line
                line = br.readLine();

            } while(line!=null && !is_end(line));
            bw_intermediate.write("\t\t\t\t"+line+"\n");

            //program length
            bw_intermediate.write("Program Length: "+convert.DectoHex(convert.HextoDec(LOCCTR)+convert.HextoDec(starting_address))+"\n");

            for (Map.Entry<String, String> ele : SYMTAB.entrySet()) 
            {
                String key = ele.getKey();
                String val = ele.getValue();
                line = key+"\t\t"+val;
                bw_symboltab.write(line+"\n");
            }
        }
    }
}