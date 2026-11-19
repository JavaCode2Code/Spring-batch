package org.example.springbatch001.partitioning;

import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class ColumnRangePartitioner implements Partitioner {
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
      int min =1;
      int max=1000;
      int taregtSize=(max-min)/gridSize+1; //500
      System.out.println("taregtSize="+taregtSize);
      Map<String,ExecutionContext> result=new HashMap<>();
      int number=0;
      int start=min;
      int end=start +taregtSize-1;
      //1 to 500
        //501 to 1000
        while
            (start<=max){
            ExecutionContext value=new ExecutionContext();
            result.put("partition" + number,value);
            if(end>=max){
                end=max;
            }
          value.putInt("mivValue",start);
            value.putInt("maxValue",end);
            start+=taregtSize;end+=taregtSize;
            number++;

        }
        System.out.println(" partition result:  " + result.toString());

        return result;


    }
}
