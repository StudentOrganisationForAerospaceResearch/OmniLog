package ca.ucalgary.soar.omnilog;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by Alex Hamilton on 2017-06-25.
 * University of Calgary
 * alexander.hamilton@ucalgary.ca
 */

public class SumRingBufferTest {

    @Test
    public void testCreation(){
        try {
            RingSumBuffer A = new RingSumBuffer(10);
        }catch (Exception e) {fail();}
    }

    @Test
    public void testGaussSum(){
        int n=101;
        long sum1,sum2;

        RingSumBuffer A = new RingSumBuffer(n);

        for (int i=0; i<n; i++){
            A.push(i);
        }

        sum2=0;
        sum1=0;
        for(int i=1; i<n/2-1; i++){ sum2=sum2+i;}
        for(int i=(n/2); i<n; i++){ sum1=sum1+i;}
        assertTrue(String.format("Sum1 not correct initially %d=/=%d",A.getSum1(),sum1),sum1==A.getSum1());
        assertTrue(String.format("Sum2 not correct initially %d=/=%d",A.getSum2(),sum2),sum2==A.getSum2());


        for (int i=n; n<10000; i++){
            A.push(i);
            sum1=0;
            sum2=0;
            for(int j=0; j<i/2; j++){ sum2=sum2+j;}
            for(int j=(n/2); j<i; j++){ sum1=sum1+j;}
            assertTrue(String.format("Sum1 not correct after %d iterations: %d=/=%d",i,A.getSum1(),sum1),sum1==A.getSum1());
            assertTrue(String.format("Sum2 not correct after %d iterations: %d=/=%d",i,A.getSum2(),sum2),sum2==A.getSum2());
        }




    }

}
