import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

class tables
{
    @SuppressWarnings("unchecked")
    public static Hashtable<String, ArrayList<String>> OPTAB() throws ClassCastException
    {
        Hashtable<String, ArrayList<String>> optab = new Hashtable<String, ArrayList<String>>();

        ArrayList<String> list=new ArrayList<String>();
        list.add("18"); //opcode
        list.add("3"); //format
        optab.put("ADD", (ArrayList<String>) list.clone());

        list.set(0, "58"); //opcode
        list.set(1, "3"); //format
        optab.put("ADDF", (ArrayList<String>) list.clone());

        list.set(0, "90"); //opcode
        list.set(1, "2"); //format
        optab.put("ADDR", (ArrayList<String>) list.clone());

        list.set(0, "40"); //opcode
        list.set(1, "3"); //format
        optab.put("AND", (ArrayList<String>) list.clone());

        list.set(0, "B4"); //opcode
        list.set(1, "2"); //format
        optab.put("CLEAR", (ArrayList<String>) list.clone());

        list.set(0, "28"); //opcode
        list.set(1, "3"); //format
        optab.put("COMP", (ArrayList<String>) list.clone());

        list.set(0, "88"); //opcode
        list.set(1, "3"); //format
        optab.put("COMPF", (ArrayList<String>) list.clone());

        list.set(0, "A0"); //opcode
        list.set(1, "2"); //format
        optab.put("COMPR", (ArrayList<String>) list.clone());

        list.set(0, "24"); //opcode
        list.set(1, "3"); //format
        optab.put("DIV", (ArrayList<String>) list.clone());

        list.set(0, "64"); //opcode
        list.set(1, "3"); //format
        optab.put("DIVF", (ArrayList<String>) list.clone());

        list.set(0, "9C"); //opcode
        list.set(1, "2"); //format
        optab.put("DIVR", (ArrayList<String>) list.clone());

        list.set(0, "C4"); //opcode
        list.set(1, "1"); //format
        optab.put("FIX", (ArrayList<String>) list.clone());

        list.set(0, "C0"); //opcode
        list.set(1, "1"); //format
        optab.put("FLOAT", (ArrayList<String>) list.clone());

        list.set(0, "3C"); //opcode
        list.set(1, "3"); //format
        optab.put("J", (ArrayList<String>) list.clone());

        list.set(0, "30"); //opcode
        list.set(1, "3"); //format
        optab.put("JEQ", (ArrayList<String>) list.clone());

        list.set(0, "34"); //opcode
        list.set(1, "3"); //format
        optab.put("JGT", (ArrayList<String>) list.clone());

        list.set(0, "38"); //opcode
        list.set(1, "3"); //format
        optab.put("JLT",  (ArrayList<String>) list.clone());

        list.set(0, "48"); //opcode
        list.set(1, "3"); //format
        optab.put("JSUB",  (ArrayList<String>) list.clone());

        list.set(0, "00"); //opcode
        list.set(1, "3"); //format
        optab.put("LDA", (ArrayList<String>) list.clone());

        list.set(0, "68"); //opcode
        list.set(1, "3"); //format
        optab.put("LDB", (ArrayList<String>) list.clone());

        list.set(0, "50"); //opcode
        list.set(1, "3"); //format
        optab.put("LDCH", (ArrayList<String>) list.clone());

        list.set(0, "70"); //opcode
        list.set(1, "3"); //format
        optab.put("LDF", (ArrayList<String>) list.clone());

        list.set(0, "08"); //opcode
        list.set(1, "3"); //format
        optab.put("LDL", (ArrayList<String>) list.clone());

        list.set(0, "6C"); //opcode
        list.set(1, "3"); //format
        optab.put("LDS", (ArrayList<String>) list.clone());

        list.set(0, "74"); //opcode
        list.set(1, "3"); //format
        optab.put("LDT", (ArrayList<String>) list.clone());

        list.set(0, "04"); //opcode
        list.set(1, "3"); //format
        optab.put("LDX", (ArrayList<String>) list.clone());

        list.set(0, "20"); //opcode
        list.set(1, "3"); //format
        optab.put("MUL", (ArrayList<String>) list.clone());

        list.set(0, "60"); //opcode
        list.set(1, "3"); //format
        optab.put("MULF", (ArrayList<String>) list.clone());

        list.set(0, "98"); //opcode
        list.set(1, "2"); //format
        optab.put("MULR", (ArrayList<String>) list.clone());

        list.set(0, "44"); //opcode
        list.set(1, "3"); //format
        optab.put("OR", (ArrayList<String>) list.clone());

        list.set(0, "D8"); //opcode
        list.set(1, "3"); //format
        optab.put("RD", (ArrayList<String>) list.clone());

        list.set(0, "AC"); //opcode
        list.set(1, "2"); //format
        optab.put("RMO", (ArrayList<String>) list.clone());

        list.set(0, "4C"); //opcode
        list.set(1, "3"); //format
        optab.put("RSUB", (ArrayList<String>) list.clone());

        list.set(0, "0C"); //opcode
        list.set(1, "3"); //format
        optab.put("STA", (ArrayList<String>) list.clone());

        list.set(0, "78"); //opcode
        list.set(1, "3"); //format
        optab.put("STB", (ArrayList<String>) list.clone());

        list.set(0, "54"); //opcode
        list.set(1, "3"); //format
        optab.put("STCH", (ArrayList<String>) list.clone());

        list.set(0, "80"); //opcode
        list.set(1, "3"); //format
        optab.put("STF", (ArrayList<String>) list.clone());

        list.set(0, "14"); //opcode
        list.set(1, "3"); //format
        optab.put("STL", (ArrayList<String>) list.clone());

        list.set(0, "7C"); //opcode
        list.set(1, "3"); //format
        optab.put("STS", (ArrayList<String>) list.clone());

        list.set(0, "10"); //opcode
        list.set(1, "3"); //format
        optab.put("STX", (ArrayList<String>) list.clone());

        list.set(0, "1C"); //opcode
        list.set(1, "3"); //format
        optab.put("SUB", (ArrayList<String>) list.clone());

        list.set(0, "5C"); //opcode
        list.set(1, "3"); //format
        optab.put("SUBF", (ArrayList<String>) list.clone());

        list.set(0, "94"); //opcode
        list.set(1, "2"); //format
        optab.put("SUBR", (ArrayList<String>) list.clone());

        list.set(0, "E0"); //opcode
        list.set(1, "3"); //format
        optab.put("TD", (ArrayList<String>) list.clone());

        list.set(0, "2C"); //opcode
        list.set(1, "3"); //format
        optab.put("TIX", (ArrayList<String>) list.clone());

        list.set(0, "B8"); //opcode
        list.set(1, "2"); //format
        optab.put("TIXR", (ArrayList<String>) list.clone());

        list.set(0, "DC"); //opcode
        list.set(1, "3"); //format
        optab.put("WD", (ArrayList<String>) list.clone());
        return optab;
    }

    @SuppressWarnings("unchecked")
    public static Hashtable<String, ArrayList<String>> REGISTER()
    {
        Hashtable<String, ArrayList<String>> reg = new Hashtable<String, ArrayList<String>>();

        //reg.put(key, value);
        ArrayList<String> list=new ArrayList<String>();
        
        list.add("0"); //number
        list.add("3"); //size in bytes
        list.add("0"); //value
        reg.put("A", (ArrayList<String>) list.clone());

        list.set(0, "1"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("X", (ArrayList<String>) list.clone());
        
        list.set(0, "2"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("L", (ArrayList<String>) list.clone());

        list.set(0, "3"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("B", (ArrayList<String>) list.clone());

        list.set(0, "4"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("S", (ArrayList<String>) list.clone());

        list.set(0, "5"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("T", (ArrayList<String>) list.clone());

        list.set(0, "6"); //number
        list.set(1, "6"); //size in bytes
        list.set(2, "0"); //value
        reg.put("F", (ArrayList<String>) list.clone());

        list.set(0, "8"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("PC", (ArrayList<String>) list.clone());

        list.set(0, "9"); //number
        list.set(1, "3"); //size in bytes
        list.set(2, "0"); //value
        reg.put("SW", (ArrayList<String>) list.clone());
        return reg;
    }

    public static Hashtable<String, String> ASSEMDIR()
    {
        Hashtable<String, String> assemdir = new Hashtable<String, String>();
        assemdir.put("START", "y");
        assemdir.put("END", "y");
        assemdir.put("BYTE", "y");
        assemdir.put("WORD", "y");
        assemdir.put("RESB", "y");
        assemdir.put("RESW", "y");
        assemdir.put("BASE", "y");
        assemdir.put("NOBASE", "y");
        assemdir.put("LTORG", "y");
        assemdir.put("USE", "y");
        return assemdir;
    }

    public static void main(String args[])
    {
        Hashtable<String, ArrayList<String>> optab = OPTAB();

        //print all the instructions
        System.out.println("???????????? INSTRUCTIONS ????????????????");
        System.out.println();
        System.out.println(optab.size() +" "+ optab.isEmpty());

        //get individual format, opcode for mnemonic
        ArrayList<String> list = (ArrayList<String>) optab.get("ADD");
        //System.out.println(optab.containsKey("ADD"));

        for (Enumeration<String> e = optab.keys(); e.hasMoreElements();) 
        {
            String key = (String) e.nextElement();
            list = (ArrayList<String>) optab.get(key);
            System.out.println(key+" "+list.get(0)+" "+list.get(1));
        }

        Hashtable<String, ArrayList<String>> reg = REGISTER();
        System.out.println();

        //print all the registers
        System.out.println("???????????? REGISTERS ????????????????");
        System.out.println();
        System.out.println(reg.size() +" "+ reg.isEmpty());

        //get individual format, opcode for mnemonic
        list = (ArrayList<String>) reg.get("A");
        //System.out.println(list.get(0)+" "+ list.get(1));

        for (Enumeration<String> e = reg.keys(); e.hasMoreElements();) 
        {
            String key = (String) e.nextElement();
            list = (ArrayList<String>) reg.get(key);
            System.out.println(key+" "+list.get(0)+" "+list.get(1)+" "+ list.get(2));
        }

        Hashtable<String, String> assemdir = ASSEMDIR();
        System.out.println();

        //print all the assembler directives 
        System.out.println("???????????? ASSEMBLER DIRECTIVES ????????????????");
        System.out.println();

        System.out.println(assemdir.size() +" "+ assemdir.isEmpty());
        //get individual format, opcode for mnemonic
        String x = assemdir.get("A");
        //System.out.println(list.get(0)+" "+ list.get(1));

        for (Enumeration<String> e = assemdir.keys(); e.hasMoreElements();) 
        {
            String key = (String) e.nextElement();
            x = (String) assemdir.get(key);
            System.out.println(key+" "+x);
        }
    }
}
