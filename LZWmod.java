import java.math.*;

public class LZWmod {
	//R stays the same. Only used to load ASCII vals into Dictionary
    private static final int R = 256;        // number of input chars
    
    //The range of bit lengths for the codewords. Starts at minW and does not exceed maxW
    private static final int minW = 9;         // codeword width
    private static final int maxW = 16;
    private static int currW = minW;
    private static int currL = 512;


    public static String dictOption;
    
    public static void compress() { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        boolean maxReached = false;

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);
            //System.err.println(s);
            BinaryStdOut.write(st.get(s),currW);
            //System.err.println(s + " " + st.get(s) + " written to file at " + currW + " bits");
            int t = s.length();
            if (t < input.length() && code < currL){
                st.put(input.substring(0, t + 1), code);
               // System.err.println(input.substring(0, t+1) + " " + code + " added to dictionary");
                code++;
                input = input.substring(t);
            }

            if(t>=input.length())
                break;


            else if(t<input.length() && code == currL)
            {
                if(currW < maxW){
                    currW++;
                    currL = (int) Math.pow(2,currW);
                }

                else if(currW == maxW)
                {
                    if(dictOption.equals("r"))
                    {
                        TST<Integer> temper = new TST<Integer>();
                        for(int i = 0; i < R; i++)
                            temper.put("" + (char) i, i);

                        code = R + 1;

                        st = temper; 
                        currW = minW;
                        currL = (int) Math.pow(2, currW);
                    }

                    else if(dictOption.equals("m"))
                    {

                    }

                    else if(dictOption.equals("n"))
                    {
                        if(maxReached == true){
                            //BinaryStdOut.write(st.get(s),currW);            // Scan past s in input.
                            //System.err.println(s + " " + st.get(s) + " written to file at " + currW + " bits");
                            input = input.substring(t);
                        }

                        else
                            maxReached = true;

                    }
                }
            }
        }
        BinaryStdOut.write(R, currW);
        BinaryStdOut.close();
    } 

    public static void expand() {
        String[] st = new String[(int)Math.pow(2, maxW)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";

        int codeword = BinaryStdIn.readInt(currW); //Reads in codeword from file at current width
        String val = st[codeword];	
        //System.err.println(val + " " + codeword + " found at " + currW);

        while (true)
         {
            BinaryStdOut.write(val);
            //System.err.println(val + " " + codeword + " was written to file");

            codeword = BinaryStdIn.readInt(currW);
            if (codeword == R) break;

            String s = st[codeword];

            if (i == codeword) 
                s = val + val.charAt(0);   // special case hack

            if (i < currL-1) 
            {
                //System.err.println(val + " " + s.charAt(0) + " " + i);
                st[i] = val + s.charAt(0);
                //System.err.println(st[i] + " " + i + " added");
                i++;
            }

            if(i == currL-1)
            {
                if(currW < maxW)
                {
                    val = s;
                    st[i] = val + s.charAt(0);
                    currW++;
                    currL = (int) Math.pow(2,currW);
                }

                else if(currW == maxW)
                {
                    if(dictOption.equals("n")){
                    val =s;
                    BinaryStdOut.write(val);
                    //System.err.println(val + " " + codeword + " was written");

                    codeword = BinaryStdIn.readInt(currW);

                    s = st[codeword];
                    st[i] = val + s.charAt(0);
                    //System.err.println(st[i] + " " + i + " " + " added");

                    i = (int) Math.pow(2,maxW) +1;
                    }

                    else if(dictOption.equals("r"))
                    {
                        val =s;
                        BinaryStdOut.write(val);
                        //System.err.println(val + " " + codeword + " was written");

                        codeword = BinaryStdIn.readInt(minW);

                        s = st[codeword];
                        st[i] = val + s.charAt(0);
                        //.err.println(st[i] + " " + i + " " + " added");


                        String [] newST = new String [(int) Math.pow(2,maxW)];

                        for(i = 0; i<R; i++)
                        {
                            st[i] = "" + (char) i;

                        }
                        st[i++] = "";

                        currW = minW;
                        currL = (int) Math.pow(2,currW);



                    }
                }
            }
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {

        if(args[1].equals("r"))
            dictOption = "r";
        else if(args[1].equals("m"))
            dictOption = "m";
        else if(args[1].equals("n"))
            dictOption = "n";
        else throw new RuntimeException("Illegal command line argument");

        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}