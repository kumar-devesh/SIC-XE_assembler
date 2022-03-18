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

class pass2
{
    /**
     *  Reads the intermediate file, symbol table file and outputs:
     *  object file
     *  assembly listing file
     */
    static int error_flag = 0;
    static int TSIZE=0; // size of the current text record object code
    static String line ="";
    static String text = ""; // text record instructions to be appended
    static String T=""; // Text record for object program
    static String TSTART="";
    static boolean write=false; // write the text record to object file
    static boolean BASE = false;

    static int random;
    static int n=0;
    static int i=0;
    static int x=0;
    static int b=0;
    static int p=0;
    static int e=0;

    static String LABEL = "";
    static String OPCODE = ""; // mnemonic opcode
    static String OPERAND = "";
    static String OBJECTCODE=""; // object codes for data, instructions
    static String LOCCTR = ""; // Pointing to current instruction
    static String PC = ""; // Program counter pointing to the next instruction

    static Hashtable<String, ArrayList<String>> OPTAB = tables.OPTAB();
    static ArrayList<String> OP = new ArrayList<String>(); //opcode info
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
    public static void addObjectCode(BufferedWriter bw_object) throws IOException
    {
        /**
         * add object code to text record
         */
        if ((text.length()+OBJECTCODE.length())<60)
        {text+=("^"+OBJECTCODE);
        return;}
        else
        {TSIZE=text.length();
        T+=("^"+convert.DectoHex(TSIZE)+text+"\n");
        bw_object.write(T);
        text=""; T=("T^"+convert.extendTo(6, LOCCTR));
        text+=("^"+OBJECTCODE);}
    }
    public static boolean checkPCrel()
    {
        // disp = TA - PC
        int disp = convert.HextoDec(SYMTAB.get(OPERAND)) - convert.HextoDec(PC);
        if (disp>=-2048 && disp<=2047)
        {
            return true;
        }
        return false;
    }
    public static boolean checkBASErel()
    {
        // disp = TA - PC
        ArrayList<String> reg = REGISTER.get("B");
        int disp = convert.HextoDec(SYMTAB.get(OPERAND)) - convert.HextoDec(reg.get(2));
        if (disp>=-2048 && disp<=2047)
        {
            return true;
        }
        return false;
    }
    public static boolean isConstant()
    {
        try 
        {
            random = Integer.parseInt(OPERAND);
            return true;
        }
        catch(Exception e)
        {}
        return false;
    }
    public static ArrayList<String> getMultipleOperands(ArrayList<String> tokens)
    {
        ArrayList<String> operands = new ArrayList<String>();
        if (OPTAB.containsKey(tokens.get(2)))
        {
            operands.add(tokens.get(3));
            operands.add(tokens.get(4));
        }
        else if (OPTAB.containsKey(tokens.get(3)))
        {
            operands.add(tokens.get(4));
            operands.add(tokens.get(5));
        }
        return operands;
    }

    public static String removeComment(String line)
    {
        String linerc = line;
        if (line.contains("."))
        {
            linerc = line.substring(0, line.indexOf('.'));
        }
        return linerc;
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
        String X[]={""};
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("intermediate.txt"), StandardCharsets.UTF_8));)
        {
            //header record {name, length, address, first_line}
            X = getHeaderData(br);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("intermediate.txt"), StandardCharsets.UTF_8));
          FileWriter object = new FileWriter("object.txt");
          BufferedWriter bw_object = new BufferedWriter(object);
          FileWriter listing = new FileWriter("listing.txt");
          BufferedWriter bw_listing = new BufferedWriter(listing);
          FileWriter error = new FileWriter("error.txt");
          BufferedWriter bw_error = new BufferedWriter(error);)
        {
            OPCODE = get_opcode(X[3]);
            if (OPCODE.equals("START"))
            {
                //starting address 
                error_flag=0;
                bw_listing.write(X[3]+"\n");
                line=br.readLine();
            }

            //header record {name, length, address, first_line}
            String header = "H"+X[0]+"^"+X[2]+"^"+X[1]+"\n";

            TSTART = X[2]; //set starting address of first text record
            LOCCTR = X[2]; // set location counter
            bw_object.write(header); //header record

            //continue pass 2 after header record/ start assembler directive

            //intialize the first text record
            T="T^"+LOCCTR;
            while((line=br.readLine())!=null && !is_end(line))
            {
                System.out.println("current instruction: "+line);
                OBJECTCODE="";
                n=0;
                i=0;
                x=0;
                b=0;
                p=0;
                e=0;
                //get the object codes and append to `text`
                if (is_comment(line))
                {
                    //write the comment to the listing file
                    bw_listing.write(line+"\n");
                    continue;
                }
                ArrayList<String> tokens = getTokens(line);
                printTokens(tokens);

                line = removeComment(line);
                if(ASSEMDIR.containsKey(tokens.get(0)))
                {
                    //perform the required assembly
                    String DIR = tokens.get(0);
                    if (DIR.equals("BASE"))
                    {
                        // base relative addressing can be used in case pc relative does not work
                        BASE=true;
                    }
                    else if (DIR.equals("NOBASE"))
                    {
                        BASE=false;
                    }
                    else if (DIR.equals("LTORG"))
                    {
                        // print the literal table
                    }
                    else if (DIR.equals("USE"))
                    {
                        // switch the program block
                    }
                    else if (DIR.equals("RESB"))
                    {
                        // write the text record, switch to new
                        int size = Integer.parseInt(tokens.get(1));
                        LOCCTR = convert.DectoHex(size+convert.HextoDec(LOCCTR));
                    }
                    else if (DIR.equals("RESW"))
                    {
                        // write the text record, switch to new
                        int size = Integer.parseInt(tokens.get(1))*3;
                        LOCCTR = convert.DectoHex(size+convert.HextoDec(LOCCTR));
                    }
                    else if (DIR.equals("BYTE"))
                    {
                        // add the required bytes to listing, text record
                        if (tokens.get(1).charAt(0)=='C')
                        {
                            OBJECTCODE = convert.toHalfBytes(tokens.get(1).substring(2, tokens.get(1).length()-1));
                            TSIZE+=OBJECTCODE.length();
                            LOCCTR = convert.DectoHex(OBJECTCODE.length()+convert.HextoDec(LOCCTR));
                        }
                        else if (tokens.get(1).charAt(0)=='X')
                        {
                            OBJECTCODE = tokens.get(1).substring(2, tokens.get(1).length()-1);
                            TSIZE+=OBJECTCODE.length();
                            LOCCTR = convert.DectoHex(OBJECTCODE.length()+convert.HextoDec(LOCCTR));
                        }
                    }
                    else if (DIR.equals("WORD"))
                    {
                        // add the one word constant to listing, text record
                        OBJECTCODE = convert.extendTo(6, tokens.get(1));
                        LOCCTR = convert.DectoHex(OBJECTCODE.length()+convert.HextoDec(LOCCTR));
                    }

                    //write this to the assembly listing file
                    addObjectCode(bw_object);
                    bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                    continue;
                }
                if (OPTAB.containsKey(tokens.get(2)) || (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3))) || 
                OPTAB.containsKey(tokens.get(2).substring(1)) || 
                (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3).substring(1)))) // format4
                {
                    PC = LOCCTR; // set PC = LOCCTR+instruction format
                    String code="";
                    String format="";
                    // get the OPCODE, OPERAND
                    if (OPTAB.containsKey(tokens.get(2)))
                    {
                        OPCODE = tokens.get(2);
                        OP = OPTAB.get(OPCODE);
                        code = OP.get(0);
                        format = OP.get(1);
                        if (!OPCODE.equals("RSUB") && !OPCODE.equals("FIX") && !OPCODE.equals("FLOAT"))
                        {
                            OPERAND = tokens.get(3);
                        }
                        else
                        {
                            // assemble the format 1 instruction and continue
                            OBJECTCODE = code;
                            LOCCTR = convert.DectoHex(convert.HextoDec(LOCCTR)+1);
                            addObjectCode(bw_object);
                            bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                            continue;
                        }
                    }
                    else if (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3)))
                    {
                        OPCODE = tokens.get(3);
                        OP = OPTAB.get(OPCODE);
                        code = OP.get(0);
                        format = OP.get(1);
                        if (!OPCODE.equals("RSUB") && !OPCODE.equals("FIX") && !OPCODE.equals("FLOAT"))
                        {
                            OPERAND = tokens.get(4);
                        }
                        else
                        {
                            // assemble the format 1 instruction and continue
                            OBJECTCODE = code;
                            LOCCTR = convert.DectoHex(convert.HextoDec(LOCCTR)+1);
                            addObjectCode(bw_object);
                            bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                            continue;
                        }
                    }
                    else if (OPTAB.containsKey(tokens.get(2).substring(1)) && tokens.get(2).charAt(0)=='+')
                    {
                        //format = 4
                        OPCODE = tokens.get(2).substring(1);
                        OPERAND = tokens.get(3);
                        OP = OPTAB.get(OPCODE);
                        code = OP.get(0);
                        format = "4";
                    }
                    else if (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3).substring(1)) && tokens.get(2).charAt(0)=='+')
                    {
                        //format = 4
                        OPCODE = tokens.get(3).substring(1);
                        OPERAND = tokens.get(4);
                        OP = OPTAB.get(OPCODE);
                        code = OP.get(0);
                        format = "4";
                    }

                    PC = convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(format));
                    if (OPCODE.equals("RSUB") || OPCODE.equals("FIX") || OPCODE.equals("FLOAT"))
                    {
                        OBJECTCODE = code;
                        addObjectCode(bw_object);
                        bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                        continue;
                    }
                    if (OPERAND.charAt(0)=='@')
                    {n=1; OPERAND = OPERAND.substring(1);}
                    else if (OPERAND.charAt(0)=='#')
                    {i=1; OPERAND = OPERAND.substring(1);}
                    else
                    {n=1;i=1;} // simple mode of addressing

                    if (tokens.get(tokens.size()-1).equals("X") && tokens.get(tokens.size()-2).equals(OPERAND))
                    {
                        x=1; // index register is being used
                    }

                    code=convert.DectoHex(convert.HextoDec(code)+2*n+i);
                    if ((OPCODE.charAt(OPCODE.length()-1)=='R' && OPTAB.containsKey(OPCODE.substring(0, OPCODE.length()-1))) || OPCODE.equals("RMO") || OPCODE.equals("CLEAR"))
                    {
                        // register instruction assembly

                        PC = convert.DectoHex(convert.HextoDec(PC) + 2); // increment PC by 2
                        if (OPCODE.equals("CLEAR") || OPCODE.equals("TIXR"))
                        {
                            ArrayList<String> reg = new ArrayList<String>();
                            reg = REGISTER.get(OPERAND);
                            String R = convert.extendTo(1, convert.DectoHex(Integer.parseInt(reg.get(0))));
                            OBJECTCODE = code + R;
                        }
                        else if ((OPCODE.charAt(OPCODE.length()-1)=='R' && OPTAB.containsKey(OPCODE.substring(0, OPCODE.length()-1))) || OPCODE.equals("RMO"))
                        {
                            ArrayList<String> operands = getMultipleOperands(tokens);
                            String OPERAND1 = operands.get(0);
                            String OPERAND2 = operands.get(1);

                            ArrayList<String> reg1 = new ArrayList<String>();
                            reg1 = REGISTER.get(OPERAND1);
                            String R1 = convert.extendTo(1, convert.DectoHex(Integer.parseInt(reg1.get(0))));

                            ArrayList<String> reg2 = new ArrayList<String>();
                            reg2 = REGISTER.get(OPERAND2);
                            String R2 = Character.toString(convert.toHexDig(Integer.parseInt(reg2.get(0))));

                            OBJECTCODE = code + R1 + R2;
                        }
                        LOCCTR = PC;
                        addObjectCode(bw_object);
                        continue;
                    }

                    if ((OPCODE.charAt(OPCODE.length()-1)=='F' && OPTAB.containsKey(OPCODE.substring(0, OPCODE.length()-1))) 
                    || OPCODE.equals("LDF") || OPCODE.equals("STF"))
                    {
                        // float instruction assembly
                        continue;
                    }

                    if (isConstant())
                    {
                        System.out.println("Instruction with a constant operand");
                        continue;
                    }

                    if (format.equals("4"))
                    {e=1;}
                    else if (!format.equals("4"))
                    {
                        String disp="";
                        // if pc relative is possible
                        if (checkPCrel())
                        {
                            // TA = PC+disp
                            p=1;
                            disp = convert.DectoHex(convert.HextoDec(SYMTAB.get(OPERAND))-convert.HextoDec(PC));
                            if (convert.HextoDec(disp)<0)
                            {
                                disp = disp.substring(disp.length()-3);
                            }
                        }

                        // else if base relative is possible
                        else if (BASE && checkBASErel())
                        {
                            // TA = BASE + disp
                            b=1;
                            ArrayList<String> reg = REGISTER.get("B");
                            disp = convert.DectoHex(convert.HextoDec(SYMTAB.get(OPERAND))-convert.HextoDec(reg.get(2)));
                            if (convert.HextoDec(disp)<0)
                            {
                                disp = disp.substring(disp.length()-3);
                            }
                        }

                        // else list an error [extended mode not specified]
                        else
                        {
                            bw_error.write(line+"\t"+"unable to assemble using PC/BASE relative, specify extended mode explicitly \n");
                            OBJECTCODE = code+"0000";
                            bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                            addObjectCode(bw_object);
                            continue;
                        }
                    }
                    if (format.equals("4"))
                    {
                        // assembled OPCODE, "x,b,p,e", address => 
                        OBJECTCODE = convert.DectoHex(convert.HextoDec(code)+2*n+i) + "0001" + convert.extendTo(5, SYMTAB.get(OPERAND));
                    }
                    addObjectCode(bw_object);
                    bw_listing.write(line+"\t"+OBJECTCODE+"\n");
                }
            }
        }
    }
}