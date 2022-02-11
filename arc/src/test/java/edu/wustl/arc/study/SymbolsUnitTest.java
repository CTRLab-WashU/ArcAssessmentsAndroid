/*
  Copyright (c) 2022 Washington University in St. Louis

  Washington University in St. Louis hereby grants to you a non-transferable,
  non-exclusive, royalty-free license to use and copy the computer code
  provided here (the "Software").  You agree to include this license and the
  above copyright notice in all copies of the Software.  The Software may not
  be distributed, shared, or transferred to any third party.  This license does
  not grant any rights or licenses to any other patents, copyrights, or other
  forms of intellectual property owned or controlled by
  Washington University in St. Louis.

  YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED
  "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING
  WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR
  PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER
  THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON
  UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR
  CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE,
  THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT
  OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*/
package edu.wustl.arc.study;


import edu.wustl.arc.paths.tests.SymbolTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(JUnit4.class)
public class SymbolsUnitTest {

    int MAX = 1000;
    Random random;
    List<Integer> symbols;
    int[] set;

    @Test
    public void test(){
        for(int i = 0; i < MAX; i++){
            setup(i);
            testNoDuplicateSymbols();
            testNoDuplicateCards();
            testNoInverseMatch();
        }
    }

    public void setup(int i){
        random = new Random(System.currentTimeMillis() + i);
        symbols =new ArrayList<Integer>();
        symbols.addAll(SymbolTest.symbolset);
        set = SymbolTest.generateNextRandomSet(random, symbols);
    }


    public void testNoDuplicateSymbols()
    {
        for(int i =0; i < 8; i = i + 2){
            Assert.assertNotEquals(set[i], set[i+1]);
        }
    }



    public void testNoDuplicateCards(){
        //testing cards 1 vs 2,3,4
        //testing cards 2 vs 1,3,4
        //testing cards 3 vs 1,2,4
        //testing cards 4 vs 1,2,3
        for(int x =0; x < 8; x = x + 2){
            for(int y = 0; y < 8; y = y +2){
                if(x != y){
                    boolean topSymbol = set[x] == set[y];
                    boolean botSymbol = set[x+1] == set[y+1];
                    Assert.assertFalse( topSymbol && botSymbol);
                }

            }

        }
    }


    public void testNoInverseMatch(){
        //testing cards 1 vs 2,3,4
        //testing cards 2 vs 1,3,4
        //testing cards 3 vs 1,2,4
        //testing cards 4 vs 1,2,3
        for(int x =0; x < 8; x = x + 2){
            for(int y = 0; y < 8; y = y +2){
                if(x != y){
                    boolean topSymbol = set[x] == set[y+1];
                    boolean botSymbol = set[x+1] == set[y];
                    Assert.assertFalse( topSymbol && botSymbol);
                }

            }

        }
    }
}
