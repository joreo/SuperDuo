package it.jaschke.alexandria;

/**
 * Created on 1/8/2016.
 * A heap of math I understand but had no desire to reinvent
 */
public class ISBNConverter {

    public static String ISBN10toISBN13( String ISBN10 ) {
        String ISBN13  = ISBN10;
        if(ISBN13.length() == 10){
            ISBN13 = "978" + ISBN13.substring(0,9);
            int d;

            int sum = 0;
            for (int i = 0; i < ISBN13.length(); i++) {
                d = ((i % 2 == 0) ? 1 : 3);
                sum += ((((int) ISBN13.charAt(i)) - 48) * d);

            }
            sum = 10 - (sum % 10);
            ISBN13 += sum;
        }

        return ISBN13;
    }
}