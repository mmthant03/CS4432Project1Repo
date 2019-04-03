package simpledb.buffer;

import simpledb.file.*;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
   private Buffer[] bufferpool;
   private int numAvailable;

   /**
    * CS4432-Project1:
    * emptyFrameList --> to keep track of num of empty frames in the buffer
    * idTable --> to efficiently find specific block num
    * clockPointer --> since we use clock policy, we need a pointer to rotate,
    *                   change refbit and replace frames
    * numBuffs --> num of buffer that is given to the bufferpool
    */
   private LinkedList<Integer> emptyFrameList;
   private Hashtable<Integer, Integer> idTable;
   private int clockPointer = 0;
   private int numBuffs;
   
   /**
    * CS4432-Project1:
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on both the {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} objects 
    * that it gets from the class
    * {@link simpledb.server.SimpleDB}.
    * Those objects are created during system initialization.
    * Thus this constructor cannot be called until 
    * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
    * is called first.
    * @param numbuffs the number of buffer slots to allocate
    */
   BasicBufferMgr(int numbuffs) {
      bufferpool = new Buffer[numbuffs];
      numAvailable = numbuffs;
      idTable = new Hashtable<Integer, Integer>(numbuffs);
      // create empty buffer list
      emptyFrameList = new LinkedList<Integer>();
      //Pool Creation
      for (int i=0; i<numbuffs; i++) {
         bufferpool[i] = new Buffer(i);
         emptyFrameList.add(i);
      }
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.isModifiedBy(txnum))
         buff.flush();
   }
   
   /**
    * CS4432-Project1:
    * Pins a buffer to the specified block.
    * If there is already a buffer assigned to that block
    * then that buffer is used;
    * otherwise, an unpinned buffer from the pool is chosen.
    * Afterwards, assigned block num and buffer index is stored in idTable
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
         updateBuff(buff);
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * CS4432-Project1:
    * Allocates a new block in the specified file, and
    * pins a buffer to it.
    * Update idTable with assigned block num and buffer index.
    * Returns null (without allocating the block) if
    * there are no available buffers.
    * @param filename the name of the file
    * @param fmtr a pageformatter object, used to format the new block
    * @return the pinned buffer
    */
   synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      updateBuff(buff);
      numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Unpins the specified buffer.
    * @param buff the buffer to be unpinned
    */
   synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned())
         numAvailable++;
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   int available() {
      return numAvailable;
   }

   /**
    * CS4432-Project1:
    * @author Myo Thant, Robert Dutile
    * Update the hash table when the new block is assigned or
    * Existing buffer has a block replacement.
    * @param buff object to replace inside hash table
    */
   private void updateBuff(Buffer buff) {
      int buffId = buff.getBufferIndex();
      int blockId = buff.getBlockNum();
      if(idTable.containsKey(blockId)) {
         idTable.remove(blockId);
         idTable.put(blockId, buffId);
      }
      else {
         idTable.put(blockId, buffId);
      }
   }
   
   /**
    * CS4432-Project1:
    * The method is reinvented to finding existing buffer from buffer hash table
    * instead of iterating through the array
    * @param blk block number
    * @return buffer if block number exists in hash table or,
    * null if it does not exist
    */
    private Buffer findExistingBuffer(Block blk) {
      int blockId = blk.number();
      if(idTable.containsKey(blockId)) {
         int buffId = idTable.get(blockId);
         return bufferpool[buffId];
      }
      else {
         return null;
      }
   }
   
   /**
    * CS4432-Project1:
    * This method is modified.
    * First, buffer manager check the emptyFrameList and take the first frame in the list.
    * Then use that frame to assign block
    * If there are no empty frames, buffer manager replace the frames in the list by clock policy
    * @return buff empty buffer or replacement buffer
    */
   private Buffer chooseUnpinnedBuffer() {
      // take the first frame from the empty frame list
      Integer emptyBuff = emptyFrameList.pollFirst();
      if(emptyBuff != null) {
         Buffer buff = bufferpool[emptyBuff];
         return buff;
      }
      else { // need to modified for clock replacement policy
         // for (Buffer buff : bufferpool)
         //    if (!buff.isPinned())
         //    return buff;
         Buffer buff = clockReplace();
         if(buff != null) {
            return buff;
         }
      return null;
      }
   }

   /**
    * CS4432-Project1:
    * Clock replacement policy
    * Start from the clock pointer frame
    * If the frame is pinned, skip,
    * If the frame is unpinned and refbit = 1, change refbit to 0;
    * If the frame is unpinned and refbit = 0, use the frame for replacement
    * @return buff frame to be replaced.
    */
   private Buffer clockReplace() {
      int isReplaced = 0;
      Buffer buff = null;
      while(isReplaced == 0) {
         buff = bufferpool[clockPointer];
         boolean pinned = buff.isPinned();
         boolean refbit = buff.getRef();
         clockPointer++;
         if (clockPointer == numBuffs) {
            clockPointer = 0;
         }

         if(pinned == false && refbit == false) {
            isReplaced = 1;
         }
         else if (pinned == false && refbit == true) {
            buff.setRef(false);
         }
      }
      return buff;
   }

   /**
    * CS4432-Project1:
    * Return all the buffer information inside the bufferpool
    * @return String all buffer information
    */
    @Override
    public String toString() {
       String buffInfo = "";
       for(int i = 0; i < numBuffs; i++) {
          buffInfo = buffInfo + bufferpool[i].toString() + "\n";
       }
       return buffInfo;
    }
}
