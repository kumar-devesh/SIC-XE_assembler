class assembler
{
    public static void main(String args[]) throws Exception
    {
        /**
         * Run the assembler using the command
         * javac -d build *.java
         * java assembler.java {tc1/ tc2}
         */
        try
        {
            String x = args[0];
            pass1.main(args);
            pass2.main(args);
        }
        catch (Exception e)
        {
            String file = "tc_final.txt";
            args = new String[1];
            args[0] = file;
            System.out.println("No file argument provided, running: "+file);
            pass1.main(args);
            System.out.println("PASS 1 completed! \nfind the outputs in intermediate file, symbol table file!");
            pass2.main(args);
            System.out.println("PASS 2 completed! \nfind the outputs in error, listing and object files!");
        }
    }
}