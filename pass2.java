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
    static String BASE_ADDRESS="";
    static String OPERAND = "";
    static String OBJECTCODE=""; // object codes for data, instructions
    static String LOCCTR = ""; // Pointing to current instruction
    static String PC = ""; // Program counter pointing to the next instruction

    static String spaces = "                                           ";
    static int modcount = 0;
    static ArrayList<String> MODRECORDS = new ArrayList<String>();
    static Hashtable<String, ArrayList<String>> OPTAB = tables.OPTAB();
    static ArrayList<String> OP = new ArrayList<String>(); //opcode info
    static Hashtable<String, String> ASSEMDIR =  tables.ASSEMDIR();
    static Hashtable<String, ArrayList<String>> REGISTER =  tables.REGISTER();
    static LinkedHashMap<String, ArrayList<String>> SYMTAB = new LinkedHashMap<String, ArrayList<String>>();
    static LinkedHashMap<String, ArrayList<String>> LITTAB = new LinkedHashMap<String, ArrayList<String>>();

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

    public static void insertSymbol(String label, ArrayList<String> value)
    {
        SYMTAB.put(label, value);
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
        System.out.println("\n\n printing the symbol table \n\n");
        for (Map.Entry<String, ArrayList<String>> ele : SYMTAB.entrySet()) 
        {
            String key = ele.getKey();
            ArrayList<String> val = ele.getValue();
            line = key+":"+val.get(0)+":"+val.get(1);
            System.out.println(line);
        }
    }

    public static void printLITTAB()
    {
        System.out.println("\n\n printing the literal table \n\n");
        for (Map.Entry<String, ArrayList<String>> ele : LITTAB.entrySet()) 
        {
            String key = ele.getKey();
            ArrayList<String> val = ele.getValue();
            line = key+":"+val.get(0)+":"+val.get(1)+":"+val.get(2);
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
    public static int charcount(String input, char ch)
    {
        int count=0;
        for(int i=0; i<input.length(); i++)
        {
            if(input.charAt(i) == ch)
            count++;
        }
        return count;
    }
    public static void addObjectCode(BufferedWriter bw_object) throws IOException
    {
        /**
         * add object code to text record
         */
        if (text.equals(""))
        {
            String next_start = convert.extendTo(6, LOCCTR);
            text=""; T=("T^"+convert.extendTo(6, next_start));
        }
        if ((text.length()+OBJECTCODE.length()-charcount(text, '^'))<60)
        {
            text+=("^"+OBJECTCODE);
            return;
        }
        else
        {
            TSIZE=text.length();
            int len = (int)((TSIZE-charcount(text, '^'))/2);
            T+=("^"+convert.extendTo(2, convert.DectoHex(len))+text+"\n");
            bw_object.write(T);

            //String next_start = convert.DectoHex(convert.HextoDec(T.split("\\^")[1])+convert.HextoDec(T.split("\\^")[2]));
            String next_start = convert.extendTo(6, LOCCTR);
            text=""; T=("T^"+convert.extendTo(6, next_start));
            text+=("^"+OBJECTCODE);
        }
    }
    public static void writeTextRecord(BufferedWriter bw_object) throws IOException
    {
        TSIZE=text.length();
        int len = (int)((TSIZE-charcount(text, '^'))/2);
        T+=("^"+convert.extendTo(2, convert.DectoHex(len))+text+"\n");
        bw_object.write(T);

        //String next_start = convert.DectoHex(convert.HextoDec(T.split("\\^")[1])+convert.HextoDec(T.split("\\^")[2]));
        String next_start = convert.extendTo(6, LOCCTR);
        text=""; T=("T^"+convert.extendTo(6, next_start));
    }

    public static boolean checkPCrel()
    {
        // disp = TA - PC
        int disp=0;
        if (OPERAND.charAt(0)=='=')
        {disp = convert.HextoDec(LITTAB.get(OPERAND).get(2))-convert.HextoDec(PC);}
        else
        {disp = convert.HextoDec(SYMTAB.get(OPERAND).get(1)) - convert.HextoDec(PC);}

        if (disp>=-2048 && disp<=2047)
        {
            return true;
        }
        return false;
    }
    public static boolean checkBASErel()
    {
        // disp = TA - PC
        int disp=0;

        if (OPERAND.charAt(0)=='=')
        {disp = convert.HextoDec(LITTAB.get(OPERAND).get(2))-convert.HextoDec(BASE_ADDRESS);}
        else
        {disp = convert.HextoDec(SYMTAB.get(OPERAND).get(1)) - convert.HextoDec(BASE_ADDRESS);}

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

    public static ArrayList<String> isAssemblerDirective(ArrayList<String> tokens)
    {
        int index=-1;
        boolean isAssemblerDirective=false;
        for(String token: tokens)
        {
            index+=1;
            if (ASSEMDIR.containsKey(token))
            {isAssemblerDirective=true; break;}
        }
        if (isAssemblerDirective)
        {
            int i=0;
            while (i<index)
            {tokens.remove(0); i+=1;}
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

                ArrayList<String> list = new ArrayList<String>();
                list.add(0, arr[1]);
                list.add(1, arr[2]);
                SYMTAB.put(arr[0], list);
            }
            //print symbol table
            //printSYMTAB();
          }
        
        // read literal table entries from the littab.txt file
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream("littab.txt"), StandardCharsets.UTF_8));)
          {
            while((line=br.readLine()) != null)
            {
                line = line.replaceAll("\t\t", " ").trim();
                String arr[] = line.split(" ");

                ArrayList<String> list = new ArrayList<String>();
                list.add(0, arr[1]);
                list.add(1, arr[2]);
                list.add(2, arr[3]);
                LITTAB.put(arr[0], list);
            }
            //print literal table
            printLITTAB();
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
            String last_line="";
            String end_line="";
            while((line=br.readLine())!=null)
            {
                last_line=line; // to save the last line to be written to listing file
                if (is_end(line))
                {end_line = line;}
                PC = LOCCTR;
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

                line = removeComment(line);
                tokens = isAssemblerDirective(tokens);
                //printTokens(tokens);

                if(ASSEMDIR.containsKey(tokens.get(0)) || ASSEMDIR.containsKey(tokens.get(1)))
                {
                    //System.out.println("Assembler directive found");

                    //perform the required assembly
                    String DIR = tokens.get(0);
                    if (DIR.equals("LTORG"))
                    {
                        // pass since the pass1 performs required operations
                        bw_listing.write(line+"\n");
                        continue;
                    }

                    int idx=0;
                    if (ASSEMDIR.containsKey(tokens.get(1)))
                    {
                        DIR = tokens.get(1);
                        idx =1;
                    }

                    // processing of assembler directives
                    if (DIR.equals("BASE"))
                    {
                        // base relative addressing can be used in case pc relative does not work
                        BASE_ADDRESS = SYMTAB.get(tokens.get(tokens.size()-1)).get(1);
                        BASE=true;
                    }
                    else if (DIR.equals("NOBASE"))
                    {
                        BASE=false;
                    }
                    else if (DIR.equals("EQU"))
                    {
                        // add to Symbol table in pass 1
                    }
                    else if (DIR.equals("USE"))
                    {
                        // switch the program block
                    }
                    else if (DIR.equals("RESB"))
                    {
                        // write the text record, switch to new
                        int size = Integer.parseInt(tokens.get(idx+1));
                        LOCCTR = convert.DectoHex(size+convert.HextoDec(LOCCTR));
                    }
                    else if (DIR.equals("RESW"))
                    {
                        // write the text record, switch to new
                        int size = Integer.parseInt(tokens.get(idx+1))*3;
                        LOCCTR = convert.DectoHex(size+convert.HextoDec(LOCCTR));
                    }
                    else if (DIR.equals("BYTE"))
                    {
                        // add the required bytes to listing, text record
                        if (tokens.get(1).charAt(0)=='C')
                        {
                            OBJECTCODE = convert.toHalfBytes(tokens.get(idx+1).substring(2, tokens.get(idx+1).length()-1));
                        }
                        else if (tokens.get(1).charAt(0)=='X')
                        {
                            OBJECTCODE = tokens.get(idx+1).substring(2, tokens.get(idx+1).length()-1);
                        }
                        TSIZE+=OBJECTCODE.length();
                        LOCCTR = convert.DectoHex(OBJECTCODE.length()+convert.HextoDec(LOCCTR));
                        addObjectCode(bw_object);
                    }
                    else if (DIR.equals("WORD"))
                    {
                        // add the one word constant to listing, text record
                        OBJECTCODE = convert.extendTo(6, tokens.get(idx+1));
                        LOCCTR = convert.DectoHex(OBJECTCODE.length()+convert.HextoDec(LOCCTR));
                        addObjectCode(bw_object);
                    }

                    //write this to the assembly listing file
                    bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                    if ((DIR.equals("RESW") || DIR.equals("RESB")) && !text.equals(""))
                    {
                        writeTextRecord(bw_object);
                    }
                    continue;
                }

                LOCCTR = tokens.get(0);
                PC = LOCCTR; // set PC = LOCCTR

                if (tokens.size()==4 && tokens.get(2).equals("*")) // handle literal tables
                {
                    PC = convert.DectoHex(convert.HextoDec(PC)+Integer.parseInt(LITTAB.get(tokens.get(3)).get(1)));
                    OBJECTCODE = LITTAB.get(tokens.get(3)).get(0);
                    bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                    addObjectCode(bw_object);
                    continue;
                }

                if (OPTAB.containsKey(tokens.get(2)) || (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3))) || 
                OPTAB.containsKey(tokens.get(2).substring(1)) || 
                (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3).substring(1)))) // format4
                {
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
                    else if (SYMTAB.containsKey(tokens.get(2)) && OPTAB.containsKey(tokens.get(3).substring(1)) && tokens.get(3).charAt(0)=='+')
                    {
                        //format = 4
                        OPCODE = tokens.get(3).substring(1);
                        OPERAND = tokens.get(4);
                        OP = OPTAB.get(OPCODE);
                        code = OP.get(0);
                        format = "4";
                    }

                    if (format.equals(""))
                    {System.out.println("error here"+line);}

                    //PC = convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(format));

                    if (OPCODE.equals("FIX") || OPCODE.equals("FLOAT"))
                    {
                        //assemble format 1 instruction and continue
                        LOCCTR = convert.DectoHex(convert.HextoDec(LOCCTR)+1);
                        OBJECTCODE = code;
                        addObjectCode(bw_object);
                        bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                        continue;
                    }

                    if (OPCODE.equals("RSUB"))
                    {
                        if (format.equals("3"))
                        {
                            OBJECTCODE = convert.DectoHex(convert.HextoDec(code)+3)+"0000";
                            addObjectCode(bw_object);
                            bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                            continue;
                        }
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

                    // FORMAT 2 instruction
                    if ((OPCODE.charAt(OPCODE.length()-1)=='R' && OPTAB.containsKey(OPCODE.substring(0, OPCODE.length()-1))) || OPCODE.equals("RMO") || OPCODE.equals("CLEAR"))
                    {
                        // register instruction assembly

                        PC = convert.DectoHex(convert.HextoDec(PC) + 2); // increment PC by 2
                        if (OPCODE.equals("CLEAR") || OPCODE.equals("TIXR"))
                        {
                            ArrayList<String> reg = new ArrayList<String>();
                            reg = REGISTER.get(OPERAND);
                            String R = convert.extendTo(1, convert.DectoHex(Integer.parseInt(reg.get(0))));
                            OBJECTCODE = code + R+"0";
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
                        addObjectCode(bw_object);
                        bw_listing.write(line + spaces.substring(line.length())+ OBJECTCODE + "\n");
                        LOCCTR = PC;
                        continue;
                    }

                    if (format.equals("4"))
                    {e=1;}

                    if (isConstant())
                    {
                        //Instruction with a constant operand
                        //System.out.println("constant operand:"+ OPERAND +":"+ format +":"+ code);

                        PC = convert.DectoHex(convert.HextoDec(PC) + convert.HextoDec(format)); // increment PC by format 3/4
                        String disp="";
                        try
                        {
                            int operand = Integer.parseInt(OPERAND);
                            if (operand <= 2047 && operand>=-2048)
                            {
                                disp = convert.DectoHex(operand);

                                if (format.equals("4"))
                                { disp = convert.extendTo(5, disp);}
                                if (format.equals("3"))
                                { disp = convert.extendTo(3, disp);}

                                if (operand<0 && format.equals("4"))
                                {
                                    disp = disp.substring(disp.length()-5);
                                }
                                else if (operand<0 && format.equals("3"))
                                {
                                    disp = disp.substring(disp.length()-3);
                                }
                            }
                            else if (operand<=131071 && operand>=-131072 && format.equals("4"))
                            {
                                disp = convert.DectoHex(operand);
                                if (operand<0)
                                {
                                    disp = disp.substring(disp.length()-5);
                                }
                                disp = convert.extendTo(5, disp);
                            }
                            else
                            {
                                bw_error.write(line+"\t\t"+"the constant operand is out of bounds");
                            }
                        }
                        finally
                        {
                            code = convert.extendTo(2, convert.DectoHex(convert.HextoDec(code)+2*n+1*i));
                            OBJECTCODE = code + convert.DectoHex(x*8+b*4+2*p+e) + disp;
                        }
                        addObjectCode(bw_object);
                        bw_listing.write(line + spaces.substring(line.length())+ OBJECTCODE + "\n");
                        LOCCTR = PC;
                        continue;
                    }

                    code=convert.extendTo(2, convert.DectoHex(convert.HextoDec(code)+2*n+i));
                    if (format.equals("4"))
                    {e=1;}
                    if (format.equals("3"))
                    {
                        String disp="";
                        PC = convert.DectoHex(convert.HextoDec(LOCCTR)+3);

                        // if pc relative is possible
                        if (checkPCrel())
                        {
                            // TA = PC+disp
                            p=1;
                            int int_disp=0;

                            if (OPERAND.charAt(0)=='=') //handle literal
                            {int_disp = convert.HextoDec(LITTAB.get(OPERAND).get(2))-convert.HextoDec(PC);}
                            else
                            {int_disp = convert.HextoDec(SYMTAB.get(OPERAND).get(1))-convert.HextoDec(PC);}
                            disp = convert.DectoHex(int_disp);
                            if (int_disp<0)
                            {
                                disp = disp.substring(disp.length()-3);
                            }
                        }

                        // else if base relative is possible
                        else if (BASE && checkBASErel() && !checkPCrel())
                        {
                            // TA = BASE + disp
                            b=1;
                            int int_disp=0;

                            if (OPERAND.charAt(0)=='=') //handle literal
                            {int_disp = convert.HextoDec(LITTAB.get(OPERAND).get(2))-convert.HextoDec(BASE_ADDRESS);}
                            else
                            {int_disp = convert.HextoDec(SYMTAB.get(OPERAND).get(1))-convert.HextoDec(BASE_ADDRESS);}

                            disp = convert.DectoHex(int_disp);
                            if (int_disp<0)
                            {
                                disp = disp.substring(disp.length()-3);
                            }
                        }

                        // else list an error [extended mode not specified]
                        else
                        {
                            bw_error.write(line+"\t"+"unable to assemble using PC/BASE relative, specify extended mode explicitly \n");
                            OBJECTCODE = code+"0000";
                            bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                            addObjectCode(bw_object);
                            continue;
                        }
                        OBJECTCODE = code+convert.DectoHex(x*8+b*4+2*p+e)+convert.extendTo(3, disp);
                    }
                    else if (format.equals("4"))
                    {
                        // assembled OPCODE, "x,b,p,e", address => 
                        PC = convert.DectoHex(convert.HextoDec(LOCCTR)+4);
                        OBJECTCODE = code + "1" + convert.extendTo(5, SYMTAB.get(OPERAND).get(1));
                        MODRECORDS.add(modcount++, ("M^"+convert.extendTo(6, convert.DectoHex(convert.HextoDec(LOCCTR)+1))+"^05"+"\n"));
                    }
                    addObjectCode(bw_object);
                    bw_listing.write(line+spaces.substring(line.length())+OBJECTCODE+"\n");
                    LOCCTR = PC;
                }
            }
            writeTextRecord(bw_object); // write the last text record
            // write modification records
            // write the end record
            line=last_line;
            bw_listing.write(line);
            
            for (String MODREC:MODRECORDS)
            {
                bw_object.write(MODREC);
            }
            ArrayList<String> tokens = getTokens(end_line);
            bw_object.write("END^"+convert.extendTo(6, SYMTAB.get(tokens.get(tokens.size()-1)).get(1)));
        }
    }
}