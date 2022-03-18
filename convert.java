class convert
{
    public static int toDecDig(char a)
    {
        if(a=='0') return 0;
        if(a=='1') return 1;
        if(a=='2') return 2;
        if(a=='3') return 3;
        if(a=='4') return 4;
        if(a=='5') return 5;
        if(a=='6') return 6;
        if(a=='7') return 7;
        if(a=='8') return 8;
        if(a=='9') return 9;
        if(a=='A') return 10;
        if(a=='B') return 11;
        if(a=='C') return 12;
        if(a=='D') return 13;
        if(a=='E') return 14;
        if(a=='F') return 15;
        return 0;
    }

    public static char toHexDig(int a)
    {
        if(a==0) return '0';
        if(a==1) return '1';
        if(a==2) return '2';
        if(a==3) return '3';
        if(a==4) return '4';
        if(a==5) return '5';
        if(a==6) return '6';
        if(a==7) return '7';
        if(a==8) return '8';
        if(a==9) return '9';
        if(a==10) return 'A';
        if(a==11) return 'B';
        if(a==12) return 'C';
        if(a==13) return 'D';
        if(a==14) return 'E';
        if(a==15) return 'F';
        return '0';
    }

    public static String toHalfBytes(String a)
    {
        /**
         * Input: A ASCII string value
         * 
         * Returns: Hexadecimal String of half bytes 
         */
        String f="";
        int ch=0;
        for(int i=0; i<a.length(); ++i)
        {
            ch = (int) a.charAt(i);
            f+=(toHexDig((int)(ch/16))+""+toHexDig((int) ch%16));
        }
        return f;
    }

    public static String extendTo(int n_dig, String a)
    {
        /**
         * Input: A hexadecimal string value
         * 
         * Returns: Hexadecimal String half bytes extended 
         */
        
        String temp="";
        for(int i=0; i<n_dig-a.length(); ++i)
            temp+='0';
        return temp+a;
    }

    public static int HextoDec(String val)
    {
        /**
         * Input: A hexadecimal string value to be converted to decimal equivalent
         * 
         * Returns: Decimal Equivalent for the String input to perform arithmetic
         */

        int res = Integer.parseInt(val, 16);
        return res;
    }

    public static String DectoHex(int val)
    {
        /**
         * Input: A Decimal integer value to be converted to decimal equivalent
         * 
         * Returns: Hexadecimal String Equivalent for the String input to perform arithmetic
         */

        String res = Integer.toHexString(val);
        return res;
    }

    public static int BintoDec(String val)
    {
        /**
         * Input: A binary string value to be converted to decimal equivalent
         * 
         * Returns: Decimal Equivalent for the String input to perform arithmetic
         */

        int res = Integer.parseInt(val, 2);
        return res;
    }

    public static String DectoBin(int val)
    {
        /**
         * Input: A string value to be converted to decimal equivalent
         * 
         * Returns: Decimal Equivalent for the String input to perform arithmetic
         */

        String res = Integer.toBinaryString(val);
        return res;
    }
    public static void main(String args[])
    {
        int n1=-1, n2=1;
        System.out.println(DectoHex(n1).substring(DectoHex(n1).length()-3)+" "+DectoHex(n2));
    }
}