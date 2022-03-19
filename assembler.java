class assembler
{
    public static void main(String args[]) throws Exception
    {
        /**
         * Run the assembler using the command
         * javac -d build *.java
         * java assembler.java {tc1/ tc2}
         */

        args = new String[1];
        args[0] = "tc1.txt";
        pass1.main(args);
        pass2.main(args);
    }
}