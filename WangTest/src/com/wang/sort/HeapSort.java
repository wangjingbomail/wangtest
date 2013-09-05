package com.wang.sort;

import java.util.List;

/**
 * 排序的类,不是线程安全的
 * @author wang
 *
 */
public class HeapSort {

	private List<Long> numList;
	
	/** list的长度，会随着堆的调整变化 **/
	private int length;
	
	public HeapSort(List<Long> idList) {
		this.numList = idList;
		length = this.numList.size();
		init();
	}
	
	/**
	 * 读取堆顶的值，但是不移走，如果堆中没有值，则返回null
	 * @return
	 */
	public Long peek(){
		if (numList!=null && length>0) {
		    return numList.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 读取并移动堆顶的值，并调整堆
	 * @return
	 */
	public Long remove() {
		Long num = null;
		if (numList!=null && length>0) {
		    num = numList.get(0);
		}
		
		if (num!=null) {
			Long theRight = numList.get(length-1);
			numList.set(0, theRight);
			length--;
			adjustHeap(0);  
			
		}
		
		return num;
		
	}
	
	/**
	 * 往堆里面放一个值
	 * @return
	 */
	public void put(Long num) {
		if (num!=null) {
		    numList.set(length, num);
		    length++;
		}
	}

	

	

	
	
	
	/**
	 * 创建堆
	 */
    public void init() {
        for (int i=numList.size()-1; i>=0; i--) {
            adjustHeap(i);
        }
    }
    
    
    /**
     * 堆的调整
     * @param pos 调整的位置
     */
    public void adjustHeap(int pos ) {
        int leftChildPos = pos*2+1;
        int rightChildPos = pos*2+2;
        
        if (leftChildPos > (length-1) ) return;
        
        int swap = 0 ;  //0:不交换，1:表示交换左节点  2:表示交换右节点
        if (rightChildPos <= (length-1) ) {
            //左右节点都存在
            
            if ( numList.get(pos) < numList.get(leftChildPos) && numList.get(pos) < numList.get(rightChildPos) ) {
                if ( numList.get(leftChildPos) < numList.get(rightChildPos) ) {
                    swap = 2;
                }else{
                    swap = 1;
                }
            }else if ( numList.get(pos) < numList.get(leftChildPos) ) {
                swap = 1;
            }else if ( numList.get(pos) < numList.get(rightChildPos) ) {
                swap =2;
            }
            
        
            
        }else{
               //只有左节点
            if (numList.get(pos) < numList.get(leftChildPos) ) {
                swap = 1;
            }
            
        }
        
        
        if (swap==1) {
            Long num = numList.get(pos);
            numList.set(pos, numList.get(leftChildPos) );
            numList.set(leftChildPos,num);
            
            adjustHeap(leftChildPos);
            
        }else if (swap==2){
            Long num = numList.get(pos);
            numList.set(pos, numList.get(rightChildPos) ) ;
            numList.set(rightChildPos, num);
            
            adjustHeap(rightChildPos);
            
        }
    }
    
    
    
    
  
    
}
