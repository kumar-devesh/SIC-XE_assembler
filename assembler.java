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
            String file = args[0];
            System.out.println("file argument provided, running test case: "+file);
        }
        catch (Exception e)
        {
            String file = "../tc_final.txt";
            args = new String[1];
            args[0] = file;
            System.out.println("No file argument provided, running test case: "+file);
        }
        finally
        {
            pass2.main(args);
            System.out.println("PASS 2 completed! \nfind the outputs in error, listing and object files!");
        }
    }
}