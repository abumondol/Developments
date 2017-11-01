/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handwash;

/**
 *
 * @author mm5gg
 */
public class Handwash {
   
    public static void main(String[] args) {
        try{
            DataProcessor.processData();
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
    
}
