package ca.ucalgary.soar.omnilog;

/**
 * Created by Alex Hamilton on 2017-06-25.
 * University of Calgary
 * alexander.hamilton@ucalgary.ca
 */


/**
 *  This class impliments a ring buffer which stores the sum of the first and second halves of the
 *  buffer. This is used to determine if the rocket has passed its apogee, as the sum of the second
 *  half will be greater than the sum of the first.
 *
 */
public class RingSumBuffer {

    int max_size;       //maximum size of the ring buffer
    int size;           //number of elements currently in the buffer

    int head;           //first element into the buffer
    int tail;           //most recent element into the buffer
    int mid;            //midpoint of the buffer

    int hold;           //temporary variable
    int[] buff;         //array buffer
    private long sum1;
    private long sum2;    //sum of the first and second half of the buffer

    public RingSumBuffer(int length){

        length = (length<=1) ? 2 : length;           //Length should be at least 2
        length = (length%2==0) ? length : length+1;  //Length should be even

        max_size=length;

        size=0;
        head=0;
        tail=0;
        mid=0;
        hold=0;

        buff = new int[length];

        sum1=0;
        sum2=0;
    }

    public int getMax_size(){
        return  max_size;
    }




    /**
     *  Pushes the integer value x onto the ringbuffer and updates the sums accordingly.
     *
     *  @param x Integer value to be pushed onto the front of the queue
     */
    public void push(int x){

        if(size<=max_size){        //Is the buffer still filling?
            size=size+1;            //Increment the size

            if ((size%2)==0){         //Even size means there is no left out element
                sum1= sum1 +x;
                sum2= sum2 +hold;
            }

            else{                      //first and second halves are not the same length
                hold=buff[mid];     //Store the mid value for now...
                sum1= sum1 +x-hold;
                mid=mid+1;
            }
        }

        else{                       //The buffer is full (and of even length)
            sum1 = sum1 + x - buff[mid];
            sum2 = sum2 +buff[mid]-buff[head];

            //Increment the pointer positions
            mid=(mid+1)%max_size;
            head = (head+1)%max_size;
        }

        //update the tail
        tail = (tail+1)%max_size;
        buff[tail]=x;
    }

    public boolean isFull(){
        return size<max_size;
    }


    public int peek(){
        return buff[tail];
    }

    public int getElement(int i){
        return buff[i];
    }


    public long getSum1() {
        return sum1;
    }

    public long getSum2() {
        return sum2;
    }
}
