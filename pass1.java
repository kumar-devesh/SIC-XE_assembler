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
    static Hashtable<String, ArrayList<String>> LITTAB =  new Hashtable<String, ArrayList<String>>();
    static Hashtable<String, ArrayList<String>> REGISTER =  tables.REGISTER();
    static LinkedHashMap<String, ArrayList<String>> SYMTAB = new LinkedHashMap<String, ArrayList<String>>();

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
    public static void insert_symbol(String label, String locctr, String type)
    {
        ArrayList<String> temp = new ArrayList<String>();
        temp.add(0, locctr);
        temp.add(0, type);
        SYMTAB.put(label, temp);
    }
    public static int getSize(String OPERAND)
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
        return nb;
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
    public static boolean isConstant(String x)
    {
        try 
        {
            int random = Integer.parseInt(x);
            return true;
        }
        catch(Exception e)
        {}
        return false;
    }
    public static String getExpressionValue(String x)
    {
        String[] operands = x.split("[+-]");
        String value = operands[0];
        if (!isConstant(operands[0]))
        {value = SYMTAB.get(operands[0]).get(1);}

        ArrayList<Integer> idx = new ArrayList<Integer>();
        int count=0;

        for (int i=0; i<x.length(); i++)
        {
            if (x.charAt(i)=='+' || x.charAt(i)=='-')
            {idx.add(count, i); count+=1;}
        }
        for (int i=1; i<operands.length; i++)
        {
            if (x.charAt(idx.get(i-1))=='+')
            {
                if (isConstant(operands[i]))
                {
                    value = convert.DectoHex(convert.HextoDec(value)+convert.HextoDec(operands[i]));
                    continue;
                }
                value = convert.DectoHex(convert.HextoDec(value)+convert.HextoDec(SYMTAB.get(operands[i]).get(1)));
            }
            else if (x.charAt(idx.get(i-1))=='-')
            {
                if (isConstant(operands[i]))
                {
                    value = convert.DectoHex(convert.HextoDec(value)-convert.HextoDec(operands[i]));
                    continue;
                }
                value = convert.DectoHex(convert.HextoDec(value)-convert.HextoDec(SYMTAB.get(operands[i]).get(1)));
            }
        }
        return value;
    }
    public static String getExpressionType(String x)
    {
        int n_rel=0;
        String type="-1";
        int count=0;
        ArrayList<Integer> idx = new ArrayList<Integer>();

        if (x.equals("*"))
        {return "A";}

        for (int i=0; i<x.length(); i++)
        {
            if (x.charAt(i)=='+' || x.charAt(i)=='-')
            {idx.add(count, i); count+=1;}
        }

        String[] operands = x.split("[+-]");
        try
        {
            if (isConstant(operands[0]) || (SYMTAB.get(operands[0])).get(1).equals("A"))
            {}
            else {n_rel=1;}
        }
        catch(Exception e)
        {
            System.out.println("Forward reference encountered");
        }

        for (int i=1; i<operands.length; i++)
        {
            if (x.charAt(idx.get(i-1))=='+' && !(SYMTAB.get(operands[i])).get(1).equals("A"))
            {
                n_rel+=1;
            }
            else if (x.charAt(idx.get(i-1))=='-' && !(SYMTAB.get(operands[i])).get(1).equals("A"))
            {
                n_rel-=1;
            }
        }
        if (n_rel==1)
        {type="R";}
        else if (n_rel==0)
        {type="A";}

        //System.out.println(x+":"+ type);
        return type;
    }

    public static boolean isValidExpression(String x)
    {
        if (x.indexOf('*')!=-1 || x.indexOf('/')!=-1)
        {
            return false;
        }
        else if (getExpressionType(x).equals("R") || getExpressionType(x).equals("A"))
        {
            return true;
        }
        return false;
    }
    public static void setLITABn()
    {
        ArrayList<String> list = new ArrayList<String>();
        for (String LITERAL:LITTAB.keySet())
        {
            list = LITTAB.get(LITERAL);
            list.set(2, "n");
            LITTAB.replace(LITERAL, list);
        }
    }
    public static String getValueOperand(String x)
    {
        x = x.substring(1); // remove '='
        if (x.charAt(0)=='C')
        {
            x = convert.toHalfBytes(x.substring(2, x.length()-1));
        }
        else if (x.charAt(0)=='X')
        {
            x = x.substring(2, x.length()-1);
        }
        return x;
    }
    public static void main(String[] args) throws Exception
    {
        /**
         * error flags:
         * 0 => no error
         * 1 => duplicate symbol
         * 2 => invalid opcode
         * 3 => invalid instruction format
         * 4 => invalid expression
         */

        // pass the path to the file as a parameter
        args = new String[1];
        args[0] = "tc2.txt";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream(args[0]), StandardCharsets.UTF_8));
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

                LABEL = get_label(line);
                OPCODE = get_opcode(line);
                OPERAND = get_operand(line);

                //System.out.println("\n"+line);
                //System.out.printf("Label:{%s}, Opcode:{%s}, Operand:{%s} \n\n", LABEL,  OPCODE, OPERAND);

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
                        String type="R";
                        insert_symbol(LABEL, LOCCTR, type);
                    }
                }

                //ASSEMBLER DIRECTIVE CHECK
                if (ASSEMDIR.containsKey(OPCODE))
                {
                    if (OPERAND=="-1" && !OPCODE.equals("LTORG"))
                    {
                        //invalid instruction format
                        error_flag=3;
                    }

                    if (OPCODE.equals("LTORG"))
                    {
                        // scan the literal table and write the values to intermediate file
                        bw_intermediate.write("\t\t\t\t"+line+"\n");
                        
                        ArrayList<String> list = new ArrayList<String>();
                        for (String LITERAL:LITTAB.keySet())
                        {
                            list = LITTAB.get(LITERAL);
                            if (list.get(2).equals("y")) // write the literals if not previously written
                            {
                                LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+getSize(LITERAL.substring(1)));
                                bw_intermediate.write(LOCCTR+"\t\t"+error_flag+"\t\t*\t\t"+LITERAL+"\n");
                                LOCCTR = LOCCTR_next;
                            }
                        }
                        setLITABn();
                        line = br.readLine();
                        continue;
                    }

                    else if (OPCODE.equals("ORG"))
                    {
                        LOCCTR_next = OPERAND;
                        if (OPERAND.equals("*"))
                        {LOCCTR_next=LOCCTR;}
                        else
                        {LOCCTR_next=OPERAND;}
                    }

                    else if (OPCODE.equals("WORD"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+3);}

                    else if (OPCODE.equals("BYTE"))
                    {
                        int nb = getSize(OPERAND);
                        LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+nb);
                    }

                    // CHECK FOR OPCODES
                    else if (OPCODE.equals("RESW"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+3*Integer.parseInt(OPERAND));}

                    else if (OPCODE.equals("RESB"))
                    {LOCCTR_next=convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(OPERAND));}
                    
                    else if (OPCODE.equals("EQU"))
                    {
                        // no LOCCTR updates
                        if (!SYMTAB.containsKey(OPERAND) && !OPERAND.equals("*") && !isConstant(OPERAND) && !isValidExpression(OPERAND))
                        {
                            error_flag = 4; //list an error
                        }
                        else if (OPERAND.equals("*"))
                        {
                            insert_symbol(LABEL, LOCCTR, getExpressionType(OPERAND));
                        }
                        else if (SYMTAB.containsKey(OPERAND))
                        {
                            insert_symbol(LABEL, SYMTAB.get(OPERAND).get(1), getExpressionType(OPERAND));
                        }
                        else if (isValidExpression(OPERAND))
                        {
                            String value = getExpressionValue(OPERAND);
                            insert_symbol(LABEL, value, getExpressionType(OPERAND));
                        }
                        else
                        {insert_symbol(LABEL, OPERAND, getExpressionType(OPERAND));}
                    }

                    if (!OPCODE.equals("EQU") && !OPCODE.equals("BYTE") && !OPCODE.equals("RESB") && !OPCODE.equals("WORD") && !OPCODE.equals("RESW"))
                    {LOCCTR = LOCCTR_next;
                    bw_intermediate.write("\t\t\t\t"+line+"\n");
                    line = br.readLine();
                    continue;}
                }
                else if (OPTAB.containsKey(OPCODE))
                {
                    //increase LOCCTR by the size of instruction
                    ArrayList<String> list = (ArrayList<String>) OPTAB.get(OPCODE);
                    LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+Integer.parseInt(list.get(1)));
                    list = new ArrayList<String>();
                    if (OPERAND.charAt(0)=='=')
                    {
                        list.add(0, getValueOperand(OPERAND));
                        list.add(1, Integer.toString(getSize(OPERAND.substring(1))));
                        list.add(2, "y");
                        LITTAB.put(OPERAND, list);
                    }
                }
                else if (OPTAB.containsKey(OPCODE.substring(1, OPCODE.length())) && OPCODE.charAt(0)=='+')
                {
                    //increase LOCCTR by the size of instruction
                    LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+4);
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

            // write the END directive
            bw_intermediate.write("\t\t\t\t"+line+"\n");
            for (String LITERAL:LITTAB.keySet())
            {
                if (LITTAB.get(LITERAL).get(2).equals("y"))
                {
                    LOCCTR_next = convert.DectoHex(convert.HextoDec(LOCCTR)+getSize(LITERAL.substring(1)));
                    bw_intermediate.write(LOCCTR+"\t\t"+error_flag+"\t\t*\t\t"+LITERAL+"\n");
                    LOCCTR = LOCCTR_next;
                }
            }

            //program length
            bw_intermediate.write("Program Length: "+convert.DectoHex(convert.HextoDec(LOCCTR)+convert.HextoDec(starting_address))+"\n");

            for (Map.Entry<String, ArrayList<String>> ele : SYMTAB.entrySet()) 
            {
                String key = ele.getKey();
                ArrayList<String> val = ele.getValue();
                line = key+"\t\t"+val.get(0)+"\t\t"+val.get(1);
                bw_symboltab.write(line+"\n");
            }
        }
        // write the literal table
        try (FileWriter littab = new FileWriter("littab.txt");
        BufferedWriter bw_littab = new BufferedWriter(littab);)
        {
            ArrayList<String> list = new ArrayList<String>();
            for (String LITERAL:LITTAB.keySet())
            {
                list = LITTAB.get(LITERAL);
                bw_littab.write(LITERAL+"\t\t"+list.get(0)+"\t\t"+list.get(1)+"\n");
            }
        }
    }
}