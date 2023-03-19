package Engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ZobristHash {

    private long zobristTable[][];
    private Random rnd = new Random();

    private long randomInt(){
        return ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    }

    void initTable()
    {
        zobristTable = new long[64][12];
        for (int i = 0; i<64; i++)
                for (int k = 0; k<12; k++)
                    zobristTable[i][k] = randomInt();
    }

    private int indexOf(int piece){
        return piece < 16 ? piece % 8 : piece % 8 + 6;
    }

   public long computeHash(HashMap<Integer,Integer> board)
    {
        long h = 0;
        for (Map.Entry<Integer,Integer> entry : board.entrySet())
        {
            int piece = indexOf(entry.getValue());
            h ^= zobristTable[entry.getKey()][piece -1];
        }
        return h;
    }

}
