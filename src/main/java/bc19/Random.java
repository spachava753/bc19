package bc19;

public class Random {
    public int nextInt(){
        return (int) Math.floor(Math.random());
    }

    public int nextInt(int upperBound){
        return (int) Math.floor(Math.random()*upperBound);
    }
}
